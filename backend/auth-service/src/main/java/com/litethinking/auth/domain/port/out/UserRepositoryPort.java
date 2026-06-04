package com.litethinking.auth.domain.port.out;

import com.litethinking.auth.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {

    Optional<User> findByUsername(String username);

    User save(User user);
}
