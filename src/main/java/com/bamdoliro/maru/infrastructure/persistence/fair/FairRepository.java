package com.bamdoliro.maru.infrastructure.persistence.fair;

import com.bamdoliro.maru.domain.fair.domain.Fair;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface FairRepository extends CrudRepository<Fair, Long>, FairRepositoryCustom {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select fair from Fair fair where fair.id = :id")
    Optional<Fair> findByIdForUpdate(@Param("id") Long id);
}
