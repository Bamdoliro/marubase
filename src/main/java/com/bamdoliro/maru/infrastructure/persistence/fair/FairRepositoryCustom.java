package com.bamdoliro.maru.infrastructure.persistence.fair;

import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.domain.fair.domain.type.FairType;

import java.util.List;

public interface FairRepositoryCustom {

    List<Fair> findByType(FairType type);
}
