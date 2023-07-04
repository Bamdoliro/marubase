package com.bamdoliro.maru.presentation.notice;

import com.bamdoliro.maru.application.notice.CreateNoticeUseCase;
import com.bamdoliro.maru.presentation.question.dto.request.CreateQuestionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/notice")
@RestController
public class NoticeController {

    private final CreateNoticeUseCase createNoticeUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createNotice(@RequestBody @Valid CreateQuestionRequest request){
        createNoticeUseCase.execute(request);
    }


}
