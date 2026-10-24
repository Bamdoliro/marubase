package com.bamdoliro.maru.infrastructure.persistence.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FormRepository extends JpaRepository<Form, Long>, FormRepositoryCustom {

    Optional<Form> findByUser(User user);

    Optional<Form> findByExaminationNumber(Long examinationNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from Form f where f.id in :ids order by f.id")
    List<Form> findAllByIdsForUpdate(@Param("ids") List<Long> ids);
}
