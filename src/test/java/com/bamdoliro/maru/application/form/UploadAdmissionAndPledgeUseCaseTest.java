package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.application.schedule.AdmissionScheduleFacade;
import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.InvalidFormStatusException;
import com.bamdoliro.maru.domain.form.exception.OutOfAdmissionAndPledgePeriodException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.s3.FileService;
import com.bamdoliro.maru.infrastructure.s3.dto.request.FileMetadata;
import com.bamdoliro.maru.infrastructure.s3.validator.FileValidator;
import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.shared.config.TimeConfig;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.SharedFixture;
import com.bamdoliro.maru.shared.fixture.ScheduleFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.bamdoliro.maru.shared.constants.FileConstant.MB;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class UploadAdmissionAndPledgeUseCaseTest {

    @Mock
    private FileService fileService;

    @Mock
    private FormFacade formFacade;

    @Mock
    private AdmissionScheduleFacade admissionScheduleFacade;

    @Test
    void 입학등록원_및_금연서약서를_업로드한다() {
        //given
        AdmissionSchedule schedule = ScheduleFixture.createSchedule();
        User user = UserFixture.createUser();
        Form form = FormFixture.createForm(FormType.REGULAR);
        FileMetadata metadata = new FileMetadata(
                "admission-and-pledge.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                10 * MB
        );
        form.pass();

        given(admissionScheduleFacade.getCurrentSchedule()).willReturn(schedule);
        given(formFacade.getForm(user)).willReturn(form);
        given(fileService.getPresignedUrl(any(String.class), any(String.class), any(FileMetadata.class), any(FileValidator.class))).willReturn(SharedFixture.createAdmissionAndPledgeUrlResponse());

        UploadAdmissionAndPledgeUseCase uploadAdmissionAndPledgeUseCase = createUseCase(
                schedule.getAdmissionAndPledgeStart().plusSeconds(1)
        );

        //when
        uploadAdmissionAndPledgeUseCase.execute(user, metadata);

        //then
        verify(formFacade, times(1)).getForm(user);
        verify(fileService, times(1)).getPresignedUrl(any(String.class), any(String.class), any(FileMetadata.class), any(FileValidator.class));
    }

    @Test
    void 입학등록원_및_금연서약서를_업로드할_때_제출_기간이_아니면_에러가_발생한다() {
        // given
        AdmissionSchedule schedule = ScheduleFixture.createSchedule();
        User user = UserFixture.createUser();
        FileMetadata metadata = new FileMetadata(
                "admission-and-pledge.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                10 * MB
        );

        given(admissionScheduleFacade.getCurrentSchedule()).willReturn(schedule);
        UploadAdmissionAndPledgeUseCase uploadAdmissionAndPledgeUseCase = createUseCase(
                schedule.getAdmissionAndPledgeStart().minusSeconds(1)
        );

        // when and then
        assertThrows(
                OutOfAdmissionAndPledgePeriodException.class,
                () -> uploadAdmissionAndPledgeUseCase.execute(user, metadata)
        );

        verify(formFacade, never()).getForm(user);
        verify(fileService, never()).getPresignedUrl(any(String.class), any(String.class), any(FileMetadata.class), any(FileValidator.class));
    }

    @Test
    void 최종합격자가_아닌_지원자가_입학등록원_및_금연서약서를_업로드하면_에러가_발생한다() {
        //given
        AdmissionSchedule schedule = ScheduleFixture.createSchedule();
        User user = UserFixture.createUser();
        Form form = FormFixture.createForm(FormType.REGULAR);
        FileMetadata metadata = new FileMetadata(
                "admission-and-pledge.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                10 * MB
        );

        given(formFacade.getForm(user)).willReturn(form);
        given(admissionScheduleFacade.getCurrentSchedule()).willReturn(schedule);
        UploadAdmissionAndPledgeUseCase uploadAdmissionAndPledgeUseCase = createUseCase(
                schedule.getAdmissionAndPledgeStart().plusSeconds(1)
        );

        //when
        assertThrows(
                InvalidFormStatusException.class,
                () -> uploadAdmissionAndPledgeUseCase.execute(user, metadata)
        );

        //then
        verify(formFacade, times(1)).getForm(user);
        verify(fileService, never()).getPresignedUrl(any(String.class), any(String.class), any(FileMetadata.class), any(FileValidator.class));
    }

    private UploadAdmissionAndPledgeUseCase createUseCase(LocalDateTime dateTime) {
        ZoneId zoneId = TimeConfig.SERVICE_ZONE_ID;
        Instant instant = dateTime.atZone(zoneId).toInstant();
        Clock clock = Clock.fixed(instant, zoneId);
        return new UploadAdmissionAndPledgeUseCase(
                fileService,
                formFacade,
                admissionScheduleFacade,
                clock
        );
    }
}
