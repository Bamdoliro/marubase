package com.bamdoliro.maru.infrastructure.persistence.auth;

import com.bamdoliro.maru.domain.auth.domain.Token;
import org.springframework.data.repository.CrudRepository;

public interface TokenRepository extends CrudRepository<Token, String> {
}
