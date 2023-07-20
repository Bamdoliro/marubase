package com.bamdoliro.maru.infrastructure.persistence.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.user.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FormRepository extends CrudRepository<Form, Long>, FormRepositoryCustom {

    boolean existsByUserId(Long userId);

    Optional<Form> findByUser(User user);

    @Query("SELECT MAX(f.examinationNumber) " +
            "FROM Form f " +
            "WHERE f.examinationNumber BETWEEN :minValue AND :maxValue")
    Optional<Long> findMaxExaminationNumber(
            @Param("minValue") Long minValue, @Param("maxValue") Long maxValue);

    @Query("SELECT f.uuid " +
            "FROM Form f " +
            "WHERE f.user = :user")
    Optional<UUID> findFormUuidByUser(@Param("user") User user);
}
