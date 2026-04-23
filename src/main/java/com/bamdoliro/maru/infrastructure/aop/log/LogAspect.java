package com.bamdoliro.maru.infrastructure.aop.log;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.log.*;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.domain.type.Authority;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.infrastructure.persistence.log.*;
import com.bamdoliro.maru.presentation.auth.dto.request.LogInRequest;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Hibernate;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Objects;


@RequiredArgsConstructor
@Aspect
@Component
public class LogAspect {

    private final Javers javers;
    private final FormFacade formFacade;
    private final UserFacade userFacade;
    private final EntityManager entityManager;
    private final AdminLoginLogRepository adminLoginLogRepository;
    private final FormSubmitLogRepository formSubmitLogRepository;
    private final FormUpdateLogRepository formUpdateLogRepository;
    private final UpdatedFieldRepository updatedFieldRepository;
    private final AdminFormViewLogRepository adminLookupFormLogRepository;
    private final AdminFormStatusChangeLogRepository adminFormStatusChangeLogRepository;


    @AfterReturning(value = "execution(* com.bamdoliro.maru.application.auth.LogInUseCase.execute(..))")
    public void logAdminLoginSuccess(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String phoneNumber = ((LogInRequest) args[0]).getPhoneNumber();

        User user = userFacade.getUser(phoneNumber);

        if (user.getAuthority() == Authority.ADMIN) {
            HttpServletRequest request = getCurrentHttpRequest();

            String clientIp = getClientIp(request);
            String userAgent = getUserAgent(request);

            AdminLoginLog log = new AdminLoginLog(phoneNumber, clientIp, userAgent, user);
            adminLoginLogRepository.save(log);
        }
    }

    @AfterReturning(value = "execution(* com.bamdoliro.maru.application.form.SubmitFormUseCase.execute(..))")
    public void logSubmitFormSuccess(JoinPoint joinPoint) {
        generateFormSubmitLog(joinPoint, FormStatus.SUBMITTED);
    }

    @AfterReturning(value = "execution(* com.bamdoliro.maru.application.form.SubmitFinalFormUseCase.execute(..))")
    public void logSubmitFinalFormSuccess(JoinPoint joinPoint) {
        generateFormSubmitLog(joinPoint, FormStatus.FINAL_SUBMITTED);
    }

    @AfterReturning(value = "execution(* com.bamdoliro.maru.application.form.QueryFormUseCase.execute(..))")
    public void logAdminLookupFormSuccess(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        User user = (User) args[0];

        if(user.getAuthority() == Authority.ADMIN) {
            HttpServletRequest request = getCurrentHttpRequest();

            String clientIp = getClientIp(request);
            String userAgent = getUserAgent(request);

            Long formId = (Long) args[1];

            Form form = formFacade.getForm(formId);

            AdminFromViewLog log = new AdminFromViewLog(clientIp, userAgent, user, form);

            adminLookupFormLogRepository.save(log);
        }
    }

    @Around(
            value = "execution(* com.bamdoliro.maru.application.form.ApproveFormUseCase.execute(..)) || " +
                    "execution(* com.bamdoliro.maru.application.form.RejectFormUseCase.execute(..)) || " +
                    "execution(* com.bamdoliro.maru.application.form.ReceiveFormUseCase.execute(..))"
    )
    public void logChangeFormStatus(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        User user = (User) args[1];

        Long formId = (Long) args[0];

        Form form = formFacade.getForm(formId);

        HttpServletRequest request = getCurrentHttpRequest();
        String clientIp = getClientIp(request);
        String userAgent = getUserAgent(request);

        FormStatus beforeStatus = form.getStatus();

        joinPoint.proceed();

        FormStatus afterStatus = form.getStatus();

        AdminFormStatusChangeLog log = AdminFormStatusChangeLog.builder()
                .clientIp(clientIp)
                .userAgent(userAgent)
                .user(user)
                .form(form)
                .beforeStatus(beforeStatus)
                .afterStatus(afterStatus)
                .build();

        adminFormStatusChangeLogRepository.save(log);
    }

    @Around(value = "execution(* com.bamdoliro.maru.application.form.UpdateFormUseCase.execute(..))")
    public void logUpdateFormSuccess(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        User user = (User) args[0];
        Form originalForm = formFacade.getForm(user);
        Hibernate.initialize(originalForm.getGrade().getSubjectList().getValue());
        Hibernate.initialize(originalForm.getGrade().getCertificateList().getValue());
        entityManager.detach(originalForm);

        joinPoint.proceed();

        Form updatedForm = formFacade.getForm(user);

        HttpServletRequest request = getCurrentHttpRequest();

        String clientIp = getClientIp(request);
        String userAgent = getUserAgent(request);

        FormUpdateLog formUpdateLog = FormUpdateLog.builder()
                .phoneNumber(user.getPhoneNumber())
                .clientIp(clientIp)
                .userAgent(userAgent)
                .user(user)
                .form(updatedForm)
                .build();
        formUpdateLogRepository.save(formUpdateLog);

        Diff diff = javers.compare(originalForm, updatedForm);

        diff.getChanges().forEach(change -> {
            if (change instanceof ValueChange valueChange) {
                String fieldName = valueChange.getPropertyName();
                String oldValue = Objects.toString(valueChange.getLeft(), "null");
                String newValue = Objects.toString(valueChange.getRight(), "null");
                UpdatedField updatedField = UpdatedField.builder()
                        .formUpdateLog(formUpdateLog)
                        .filedName(fieldName)
                        .oldValue(oldValue)
                        .newValue(newValue)
                        .build();
                updatedFieldRepository.save(updatedField);
            }
        });
    }

    private HttpServletRequest getCurrentHttpRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    private String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("x-forwarded-for");

        if (clientIp == null) return request.getRemoteAddr();
        return clientIp.split(",")[0];
    }

    private String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        if (userAgent == null) return "Unknown";
        return userAgent;
    }

    private void generateFormSubmitLog(JoinPoint joinPoint, FormStatus status) {
        Object[] args = joinPoint.getArgs();

        User user = (User) args[0];
        Form form = formFacade.getForm(user);

        HttpServletRequest request = getCurrentHttpRequest();

        String clientIp = getClientIp(request);
        String userAgent = getUserAgent(request);

        FormSubmitLog formSubmitLog = FormSubmitLog.builder()
                .phoneNumber(user.getPhoneNumber())
                .clientIp(clientIp)
                .userAgent(userAgent)
                .user(user)
                .form(form)
                .status(status)
                .build();
        formSubmitLogRepository.save(formSubmitLog);
    }
}
