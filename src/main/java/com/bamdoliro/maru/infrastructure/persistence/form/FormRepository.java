package com.bamdoliro.maru.infrastructure.persistence.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FormRepository extends JpaRepository<Form, Long>, FormRepositoryCustom {

    Optional<Form> findByUser(User user);

    Optional<Form> findByExaminationNumber(Long examinationNumber);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Form f where f.user.id in (select u.id from User u where u.isTestData = true)")
    int deleteAllOfTestData();
}
