package com.bamdoliro.maru.infrastructure.persistence.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FormRepository extends JpaRepository<Form, Long>, FormRepositoryCustom {

    boolean existsByIdNotNull();

    Optional<Form> findByUser(User user);

    Optional<Form> findByExaminationNumber(Long examinationNumber);
}
