package com.bamdoliro.maru.presentation.message;

import com.bamdoliro.maru.application.message.SendMessageUseCase;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.message.SendMessageService;
import com.bamdoliro.maru.presentation.message.dto.request.SendMessageRequest;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.auth.Authority;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/message")
@RestController
public class MessageController {

    private final SendMessageUseCase sendMessageUseCase;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/status")
    public void sendMessageByStatus(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestBody @Valid SendMessageRequest request
    ) {
        sendMessageUseCase.execute(request);
    }
}
