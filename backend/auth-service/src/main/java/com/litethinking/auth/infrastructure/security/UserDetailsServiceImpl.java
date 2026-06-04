package com.litethinking.auth.infrastructure.security;

import com.litethinking.auth.domain.model.User;
import com.litethinking.auth.domain.port.out.UserRepositoryPort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepositoryPort userRepositoryPort;

    public UserDetailsServiceImpl(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User domainUser = userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return new org.springframework.security.core.userdetails.User(
                domainUser.getUsername(),
                domainUser.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + domainUser.getRole().getName()))
        );
    }
}
