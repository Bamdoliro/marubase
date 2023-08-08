package com.bamdoliro.maru.infrastructure.persistence.form;

import com.bamdoliro.maru.domain.form.domain.DraftForm;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

import java.util.Optional;

public interface DraftFormRepository extends KeyValueRepository<DraftForm, String> {
}
