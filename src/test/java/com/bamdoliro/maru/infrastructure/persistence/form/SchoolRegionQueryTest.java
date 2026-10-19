package com.bamdoliro.maru.infrastructure.persistence.form;

import com.bamdoliro.maru.application.analysis.QuerySchoolStatusUseCase;
import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.domain.value.Education;
import com.bamdoliro.maru.domain.form.domain.value.School;
import com.bamdoliro.maru.infrastructure.persistence.form.vo.SchoolStatusVo;
import com.bamdoliro.maru.presentation.analysis.dto.request.SchoolStatusRequest;
import com.bamdoliro.maru.presentation.analysis.dto.response.SchoolStatusResponse;
import com.bamdoliro.maru.shared.config.JasyptConfig;
import com.bamdoliro.maru.shared.config.JpaConfig;
import com.bamdoliro.maru.shared.config.QuerydslConfig;
import com.bamdoliro.maru.shared.config.properties.JasyptProperties;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({QuerydslConfig.class, JasyptConfig.class, JpaConfig.class})
@EnableConfigurationProperties(JasyptProperties.class)
class SchoolRegionQueryTest {

    private static final List<FormStatus> SUBMITTED = List.of(FormStatus.SUBMITTED);

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FormRepository formRepository;

    @Test
    void 구가_포함된_부산_소재지는_타지역과_중복되지_않는다() {
        saveSchool("부산중", "부산광역시", "부산광역시 중구 중앙대로 1");
        saveSchool("중구중", "부산광역시 중구", "부산광역시 중구 중앙대로 2");
        saveSchool("해운대중", "부산광역시 해운대구", "부산광역시 해운대구 해운대로 3");
        saveSchool("서울중", "서울특별시", "서울특별시 중구 세종대로 1");

        assertThat(formRepository.findSchoolByAddress(SUBMITTED, "부산광역시"))
                .extracting(SchoolStatusVo::getSchoolName)
                .containsExactlyInAnyOrder("부산중", "중구중", "해운대중");
        assertThat(formRepository.findNotBusanSchool(SUBMITTED))
                .extracting(SchoolStatusVo::getSchoolName)
                .containsExactly("서울중");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void 구가_없거나_공백이면_부산_전체를_조회한다(String gu) {
        saveSchool("소재지만부산중", "부산광역시", "중구 중앙대로 1");
        saveSchool("주소만부산중", null, "부산광역시 해운대구 해운대로 3");
        saveSchool("서울중", "서울특별시", "서울특별시 중구 세종대로 1");

        List<SchoolStatusResponse> result = new QuerySchoolStatusUseCase(formRepository)
                .execute(new SchoolStatusRequest(SUBMITTED, true, gu));

        assertThat(result).extracting(SchoolStatusResponse::getSchoolName)
                .containsExactlyInAnyOrder("소재지만부산중", "주소만부산중");
    }

    @Test
    void 소재지가_누락되어도_주소로_지역을_판정한다() {
        saveSchool("부산중", null, "부산광역시 중구 중앙대로 1");
        saveSchool("서울중", null, "서울특별시 중구 세종대로 1");
        saveSchool("공백소재지중", "  ", "경상남도 김해시 가락로 1");
        saveSchool("지역미상중", null, null);
        saveSchool("공백지역중", "  ", "  ");

        assertThat(formRepository.findSchoolByAddress(SUBMITTED, "부산광역시"))
                .extracting(SchoolStatusVo::getSchoolName).containsExactly("부산중");
        assertThat(formRepository.findNotBusanSchool(SUBMITTED))
                .extracting(SchoolStatusVo::getSchoolName)
                .containsExactlyInAnyOrder("서울중", "공백소재지중");
    }

    @Test
    void 소재지와_주소가_불일치해도_부산과_타지역은_중복되지_않는다() {
        saveSchool("주소부산중", "경상남도", "부산광역시 중구 중앙대로 1");
        saveSchool("소재지부산중", "부산광역시 해운대구", "경상남도 김해시 가락로 1");

        assertThat(formRepository.findSchoolByAddress(SUBMITTED, "부산광역시"))
                .extracting(SchoolStatusVo::getSchoolName)
                .containsExactlyInAnyOrder("주소부산중", "소재지부산중");
        assertThat(formRepository.findNotBusanSchool(SUBMITTED)).isEmpty();
    }

    @Test
    void 특정구와_원서상태_조건을_함께_적용한다() {
        saveSchool("중구중", "부산광역시 중구", "부산광역시 중구 중앙대로 1");
        saveSchool("해운대중", "부산광역시", "부산광역시 해운대구 해운대로 3");
        Form received = saveSchool("접수중", "부산광역시 중구", "부산광역시 중구 중앙대로 2");
        received.receive();
        Form otherReceived = saveSchool("서울접수중", "서울특별시", "서울특별시 중구 세종대로 1");
        otherReceived.receive();
        entityManager.flush();

        assertThat(formRepository.findSchoolByAddress(SUBMITTED, "부산광역시 중구"))
                .extracting(SchoolStatusVo::getSchoolName).containsExactly("중구중");
        assertThat(formRepository.findNotBusanSchool(SUBMITTED)).isEmpty();
    }

    @Test
    void 지역명_경계를_구분하고_앞뒤_공백을_제거한다() {
        saveSchool("부산중", " 부산광역시 해운대구 ", " 해운대구 해운대로 1 ");
        saveSchool("유사명중", "부산광역시외", "경상남도 김해시 가락로 1");

        assertThat(formRepository.findSchoolByAddress(SUBMITTED, "부산광역시"))
                .extracting(SchoolStatusVo::getSchoolName).containsExactly("부산중");
        assertThat(formRepository.findNotBusanSchool(SUBMITTED))
                .extracting(SchoolStatusVo::getSchoolName).containsExactly("유사명중");
    }

    private Form saveSchool(String name, String location, String address) {
        Form form = FormFixture.createForm(FormType.REGULAR);
        Education education = form.getEducation();
        form.update(form.getApplicant(), form.getParent(),
                new Education(education.getGraduationType(), education.getGraduationYear(),
                        new School(name, location, address, "7631003"), education.getTeacher()),
                form.getGrade(), form.getDocument(), form.getType());
        entityManager.persist(form.getUser());
        entityManager.persistAndFlush(form);
        return form;
    }
}
