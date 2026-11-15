package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.CannotGenerateUniqueTestDataException;
import com.bamdoliro.maru.domain.form.service.AssignExaminationNumberService;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.form.service.RandomFormGenerator;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.domain.value.Password;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.presentation.form.dto.request.GenerateTestFormListRequest;
import com.bamdoliro.maru.presentation.form.dto.response.GenerateTestFormResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// 원서 접수 테스트용 원서와 사용자를 만든다. 회원가입 절차를 거치지 않으므로 인증 문자 등 SMS는 발송되지 않는다.
@RequiredArgsConstructor
@UseCase
public class GenerateTestFormUseCase {

    static final int MAX_PHONE_NUMBER_RETRY = 10;

    private final UserRepository userRepository;
    private final FormRepository formRepository;
    private final RandomFormGenerator randomFormGenerator;
    private final CalculateFormScoreService calculateFormScoreService;
    private final AssignExaminationNumberService assignExaminationNumberService;

    @Transactional
    public GenerateTestFormResponse execute(GenerateTestFormListRequest request) {
        Map<FormType, Integer> countByType = new EnumMap<>(FormType.class);
        request.getFormList().forEach(item -> countByType.merge(item.getType(), item.getCount(), Integer::sum));

        // 로그인할 일이 없는 계정이므로 알 수 없는 비밀번호를 한 번만 인코딩해서 모든 테스트 사용자에게 쓴다.
        String encodedPassword = PasswordUtil.encode(UUID.randomUUID().toString());
        Set<String> usedPhoneNumbers = new HashSet<>();
        List<User> users = new ArrayList<>();
        List<Form> forms = new ArrayList<>();

        countByType.forEach((type, count) -> {
            for (int i = 0; i < count; i++) {
                User user = User.createTestData(
                        generateUniquePhoneNumber(usedPhoneNumbers),
                        randomFormGenerator.randomName(),
                        new Password(encodedPassword)
                );
                Form form = randomFormGenerator.generate(user, type);
                calculateFormScoreService.execute(form);
                form.submit();

                users.add(user);
                forms.add(form);
            }
        });

        userRepository.saveAll(users);
        assignExaminationNumberService.executeAll(forms);
        formRepository.saveAll(forms);

        return new GenerateTestFormResponse(forms.size(), countByType);
    }

    private String generateUniquePhoneNumber(Set<String> usedPhoneNumbers) {
        for (int i = 0; i < MAX_PHONE_NUMBER_RETRY; i++) {
            String phoneNumber = randomFormGenerator.randomPhoneNumber();
            if (!usedPhoneNumbers.contains(phoneNumber) && !userRepository.existsByPhoneNumber(phoneNumber)) {
                usedPhoneNumbers.add(phoneNumber);
                return phoneNumber;
            }
        }

        throw new CannotGenerateUniqueTestDataException();
    }
}
