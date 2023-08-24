package com.bamdoliro.maru.infrastructure.persistence.fair;

import com.bamdoliro.maru.domain.fair.domain.Fair;
import org.springframework.data.repository.CrudRepository;

public interface FairRepository extends CrudRepository<Fair, Long>, FairRepositoryCustom {
}
