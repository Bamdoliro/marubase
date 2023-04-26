package com.bamdoliro.maru.shared.security.auth;

import com.bamdoliro.maru.domain.user.service.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthDetailsService implements UserDetailsService {

    private final UserFacade userFacade;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return new AuthDetails(userFacade.getUser(email));
    }
}
