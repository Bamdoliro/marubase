package com.bamdoliro.maru.application.message;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.message.SendMessageService;
import com.bamdoliro.maru.infrastructure.message.exception.FailedToSendException;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.message.dto.request.SendMessageRequest;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SendMessageUseCaseTest {

    @InjectMocks
    private SendMessageUseCase sendMessageUseCase;

    @Mock
    private SendMessageService sendMessageService;

    @Mock
    private FormRepository formRepository;

    @Test
    void 원서를_최종제출한_학생들에게_메시지를_보낸다(){

        //given
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.submit("https://maru.bamdoliro.com/pdf/1");
        given(formRepository.findByStatus(FormStatus.FINAL_SUBMITTED)).willReturn(List.of(form));
        SendMessageRequest request = new SendMessageRequest("부산소마고 공지사항", "테스트입니다", FormStatus.FINAL_SUBMITTED);

        //when
        sendMessageUseCase.execute(request);

        //then
        verify(formRepository, times(1)).findByStatus(FormStatus.FINAL_SUBMITTED);
        verify(sendMessageService, times(1)).execute(List.of(form.getUser().getPhoneNumber()), request.getText(), request.getTitle());
    }

    @Test
    void 원서를_승인받은_학생들에게_메시지를_보낸다(){

        //given
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.approve();
        given(formRepository.findByStatus(FormStatus.APPROVED)).willReturn(List.of(form));
        SendMessageRequest request = new SendMessageRequest("부산소마고 공지사항", "테스트입니다", FormStatus.APPROVED);

        //when
        sendMessageUseCase.execute(request);

        //then
        verify(formRepository, times(1)).findByStatus(FormStatus.APPROVED);
        verify(sendMessageService, times(1)).execute(List.of(form.getUser().getPhoneNumber()), request.getText(), request.getTitle());
    }

    @Test
    void 원서를_반려받은_학생들에게_메시지를_보낸다(){

        //given
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.reject();
        given(formRepository.findByStatus(FormStatus.REJECTED)).willReturn(List.of(form));
        SendMessageRequest request = new SendMessageRequest("부산소마고 공지사항", "학생들의 원서가 조건을 충족하지 못해 반려되었습니다.", FormStatus.REJECTED);

        //when
        sendMessageUseCase.execute(request);

        //then
        verify(formRepository, times(1)).findByStatus(FormStatus.REJECTED);
        verify(sendMessageService, times(1)).execute(List.of(form.getUser().getPhoneNumber()), request.getText(), request.getTitle());
    }

    @Test
    void 원서를_접수한_학생들에게_메시지를_보낸다(){

        //given
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.receive();
        given(formRepository.findByStatus(FormStatus.RECEIVED)).willReturn(List.of(form));
        SendMessageRequest request = new SendMessageRequest("부산소마고 공지사항", "제출하신 원서가 접수되었습니다.", FormStatus.RECEIVED);

        //when
        sendMessageUseCase.execute(request);

        //then
        verify(formRepository, times(1)).findByStatus(FormStatus.RECEIVED);
        verify(sendMessageService, times(1)).execute(List.of(form.getUser().getPhoneNumber()), request.getText(), request.getTitle());
    }

    @Test
    void 제1차_합격자들에게_메시지를_보낸다(){

        //given
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.firstPass();
        given(formRepository.findByStatus(FormStatus.FIRST_PASSED)).willReturn(List.of(form));
        SendMessageRequest request = new SendMessageRequest("부산소마고 공지사항", "1차 전형에 합격하신것을 축하드립니다. 면접 장소를 확인하시고 꼭 제시간에 방문하시길 바라겠습니다,", FormStatus.FIRST_PASSED);

        //when
        sendMessageUseCase.execute(request);

        //then
        verify(formRepository, times(1)).findByStatus(FormStatus.FIRST_PASSED);
        verify(sendMessageService, times(1)).execute(List.of(form.getUser().getPhoneNumber()), request.getText(), request.getTitle());
    }

    @Test
    void 제1차_불합격자들에게_메시지를_보낸다(){

        //given
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.firstFail();
        given(formRepository.findByStatus(FormStatus.FIRST_FAILED)).willReturn(List.of(form));
        SendMessageRequest request = new SendMessageRequest("부산소마고 공지사항", "1차 전형에 불합격하신것에 대해 유감입니당~", FormStatus.FIRST_FAILED);

        //when
        sendMessageUseCase.execute(request);

        //then
        verify(formRepository, times(1)).findByStatus(FormStatus.FIRST_FAILED);
        verify(sendMessageService, times(1)).execute(List.of(form.getUser().getPhoneNumber()), request.getText(), request.getTitle());
    }

    @Test
    void 제2차전형에_불참한_학생들에게_메시지를_보낸다(){

        //given
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.noShow();
        given(formRepository.findByStatus(FormStatus.NO_SHOW)).willReturn(List.of(form));
        SendMessageRequest request = new SendMessageRequest("부산소마고 공지사항", "해당 지원자들은 2차전형에 '불참'하였으므로 패널티가 있을 예정입니다.", FormStatus.NO_SHOW);

        //when
        sendMessageUseCase.execute(request);

        //then
        verify(formRepository, times(1)).findByStatus(FormStatus.NO_SHOW);
        verify(sendMessageService, times(1)).execute(List.of(form.getUser().getPhoneNumber()), request.getText(), request.getTitle());
    }

    @Test
    void 최종합격자들에게_메시지를_보낸다(){

        //given
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.pass();
        given(formRepository.findByStatus(FormStatus.PASSED)).willReturn(List.of(form));
        SendMessageRequest request = new SendMessageRequest("부산소마고 공지사항", "합격하였습니다. 축하드립니다.", FormStatus.PASSED);

        //when
        sendMessageUseCase.execute(request);

        //then
        verify(formRepository, times(1)).findByStatus(FormStatus.PASSED);
        verify(sendMessageService, times(1)).execute(List.of(form.getUser().getPhoneNumber()), request.getText(), request.getTitle());
    }

    @Test
    void 최종불합격자들에게_메시지를_보낸다(){

        //given
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.fail();
        given(formRepository.findByStatus(FormStatus.FAILED)).willReturn(List.of(form));
        SendMessageRequest request = new SendMessageRequest("부산소마고 공지사항", "불합격", FormStatus.FAILED);

        //when
        sendMessageUseCase.execute(request);

        //then
        verify(formRepository, times(1)).findByStatus(FormStatus.FAILED);
        verify(sendMessageService, times(1)).execute(List.of(form.getUser().getPhoneNumber()), request.getText(), request.getTitle());
    }

    @Test
    void 보낼메시지대상이_존재하지_않으면_오류가_발생한다(){

        //given
        willThrow(new FailedToSendException()).given(formRepository).findByStatus(FormStatus.SUBMITTED);
        SendMessageRequest request = new SendMessageRequest("부산소마고 공지사항", "제출이 완료되었습니다.", FormStatus.SUBMITTED);

        //when and then
        Assertions.assertThrows(FailedToSendException.class,
                () -> sendMessageUseCase.execute(request));
        verify(formRepository, times(1)).findByStatus(FormStatus.SUBMITTED);
        verify(sendMessageService, never()).execute(anyList(), anyString(), anyString());
    }
}
