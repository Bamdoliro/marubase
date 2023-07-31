package com.bamdoliro.maru.domain.form.service;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.exception.FormNotFoundException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class FormFacade {

    private final FormRepository formRepository;

    public Form getForm(Long id) {
        return formRepository.findById(id)
                .orElseThrow(FormNotFoundException::new);
    }

    public Form getForm(User user) {
        return formRepository.findByUser(user)
                .orElseThrow(FormNotFoundException::new);
    }
}
