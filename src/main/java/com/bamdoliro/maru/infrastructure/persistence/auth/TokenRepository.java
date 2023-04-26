package com.bamdoliro.maru.infrastructure.persistence.auth;

import com.bamdoliro.maru.domain.auth.domain.Token;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

import java.util.Optional;

public interface TokenRepository extends KeyValueRepository<Token, String> {

    Optional<Token> findByToken(String token);
}
