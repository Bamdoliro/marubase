package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.log.AdminFormStatusChangeLogRepository;
import com.bamdoliro.maru.infrastructure.persistence.log.AdminFormViewLogRepository;
import com.bamdoliro.maru.infrastructure.persistence.log.AdminLoginLogRepository;
import com.bamdoliro.maru.infrastructure.persistence.log.FormSubmitLogRepository;
import com.bamdoliro.maru.infrastructure.persistence.log.FormUpdateLogRepository;
import com.bamdoliro.maru.infrastructure.persistence.log.UpdatedFieldRepository;
import com.bamdoliro.maru.infrastructure.persistence.schedule.AdmissionScheduleChangeLogRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.presentation.form.dto.response.DeleteTestFormResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

// 테스트 데이터(User.isTestData = true)인 사용자와 그 원서를 지운다.
// tbl_user, tbl_form을 참조하는 로그를 먼저 지운 뒤 원서, 사용자 순서로 지운다.
@RequiredArgsConstructor
@UseCase
public class DeleteTestFormUseCase {

    private final UpdatedFieldRepository updatedFieldRepository;
    private final FormUpdateLogRepository formUpdateLogRepository;
    private final FormSubmitLogRepository formSubmitLogRepository;
    private final AdminFormViewLogRepository adminFormViewLogRepository;
    private final AdminFormStatusChangeLogRepository adminFormStatusChangeLogRepository;
    private final AdminLoginLogRepository adminLoginLogRepository;
    private final AdmissionScheduleChangeLogRepository admissionScheduleChangeLogRepository;
    private final FormRepository formRepository;
    private final UserRepository userRepository;

    @Transactional
    public DeleteTestFormResponse execute() {
        updatedFieldRepository.deleteAllOfTestData();
        formUpdateLogRepository.deleteAllOfTestData();
        formSubmitLogRepository.deleteAllOfTestData();
        adminFormViewLogRepository.deleteAllOfTestData();
        adminFormStatusChangeLogRepository.deleteAllOfTestData();
        adminLoginLogRepository.deleteAllOfTestData();
        admissionScheduleChangeLogRepository.deleteAllOfTestData();

        int deletedFormCount = formRepository.deleteAllOfTestData();
        int deletedUserCount = userRepository.deleteAllOfTestData();

        return new DeleteTestFormResponse(deletedFormCount, deletedUserCount);
    }
}
