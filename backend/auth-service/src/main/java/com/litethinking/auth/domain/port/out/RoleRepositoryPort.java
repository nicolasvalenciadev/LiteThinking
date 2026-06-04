package com.litethinking.auth.domain.port.out;

import com.litethinking.auth.domain.model.Role;

import java.util.Optional;

public interface RoleRepositoryPort {

    Optional<Role> findByName(String name);
}
