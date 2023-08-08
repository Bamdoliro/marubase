package com.bamdoliro.maru.infrastructure.persistence.form;

import com.bamdoliro.maru.domain.form.domain.DraftForm;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

public interface DraftFormRepository extends KeyValueRepository<DraftForm, String> {
}
