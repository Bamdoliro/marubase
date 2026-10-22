package com.bamdoliro.maru.infrastructure.persistence.policy;

import com.bamdoliro.maru.domain.policy.AdmissionPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdmissionPolicyRepository extends JpaRepository<AdmissionPolicy, Long> {
}
