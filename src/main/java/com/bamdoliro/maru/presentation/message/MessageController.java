package com.bamdoliro.maru.presentation.message;

import com.bamdoliro.maru.application.message.SendMessageUseCase;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.message.dto.request.SendMessageByStatusRequest;
import com.bamdoliro.maru.presentation.message.dto.request.SendMessageByTypeRequest;
import com.bamdoliro.maru.presentation.message.dto.request.SendMessageToAllUserRequest;
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
            @RequestBody @Valid SendMessageByStatusRequest request
    ) {
        sendMessageUseCase.execute(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/type")
    public void sendMessageByType(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestBody @Valid SendMessageByTypeRequest request
    ) {
        sendMessageUseCase.execute(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/broadcast")
    public void sendMessageToAllUser(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestBody @Valid SendMessageToAllUserRequest request
    ) {
        sendMessageUseCase.execute(request);
    }
}
