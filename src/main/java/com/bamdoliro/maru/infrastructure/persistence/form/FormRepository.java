package com.bamdoliro.maru.infrastructure.persistence.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FormRepository extends JpaRepository<Form, Long>, FormRepositoryCustom {

    boolean existsByUserId(Long userId);

    Optional<Form> findByUser(User user);

    @Query("SELECT MAX(f.examinationNumber) " +
            "FROM Form f " +
            "WHERE f.examinationNumber BETWEEN :minValue AND :maxValue")
    Optional<Long> findMaxExaminationNumber(
            @Param("minValue") Long minValue, @Param("maxValue") Long maxValue);

    Optional<Form> findByExaminationNumber(Long examinationNumber);
}
