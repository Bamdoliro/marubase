package com.bamdoliro.maru.infrastructure.persistence.form;

import com.bamdoliro.maru.domain.form.domain.DraftForm;
import org.springframework.data.repository.CrudRepository;

public interface DraftFormRepository extends CrudRepository<DraftForm, String> {
}
