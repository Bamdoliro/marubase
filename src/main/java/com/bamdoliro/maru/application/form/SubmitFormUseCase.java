package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.exception.FormAlreadySubmittedException;
import com.bamdoliro.maru.domain.form.service.AssignExaminationNumberService;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.annotation.ValidateApplicationFormPeriod;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@UseCase
public class SubmitFormUseCase {

    // 운영 DB의 tbl_form UNIQUE (user_id) 제약 이름
    static final String FORM_USER_UNIQUE_CONSTRAINT = "uk170dieqvc09ryf9hjk3kcpfkj";

    private final FormRepository formRepository;
    private final CalculateFormScoreService calculateFormScoreService;
    private final AssignExaminationNumberService assignExaminationNumberService;

    @ValidateApplicationFormPeriod
    @Transactional
    public void execute(User user, SubmitFormRequest request) {
        GradeRequestValidator.validate(request.getEducation(), request.getGrade());
        validateOnlyOneFormPerUser(user);

        Form form = Form.builder()
                .applicant(request.getApplicant().toValue())
                .parent(request.getParent().toValue())
                .education(request.getEducation().toValue())
                .grade(request.getGrade().toValue())
                .document(request.getDocument().toValue())
                .type(request.getType())
                .user(user)
                .build();

        calculateFormScoreService.execute(form);
        assignExaminationNumberService.execute(form);
        save(form);
    }

    private void validateOnlyOneFormPerUser(User user) {
        Optional<Form> form = formRepository.findByUser(user);
        if (form.isPresent()) {
            throw new FormAlreadySubmittedException();
        }
    }

    // id 생성 전략이 IDENTITY라 save 시점에 INSERT가 실행되므로 제약 위반도 여기서 발생한다.
    private void save(Form form) {
        try {
            formRepository.save(form);
        } catch (DataIntegrityViolationException e) {
            if (isFormUserUniqueViolation(e)) {
                throw new FormAlreadySubmittedException();
            }
            throw e;
        }
    }

    private boolean isFormUserUniqueViolation(DataIntegrityViolationException e) {
        for (Throwable cause = e; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException violation
                    && FORM_USER_UNIQUE_CONSTRAINT.equalsIgnoreCase(violation.getConstraintName())) {
                return true;
            }
        }

        // 제약 이름이 운영 상수와 일치하지 않을 때를 위한 보조 수단. PostgreSQL 형식: Key (user_id)=(...) already exists.
        String message = NestedExceptionUtils.getMostSpecificCause(e).getMessage();
        return message != null && message.contains("(user_id)") && message.contains("already exists");
    }
}
