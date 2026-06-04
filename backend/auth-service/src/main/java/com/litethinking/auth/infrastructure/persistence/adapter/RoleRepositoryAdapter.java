package com.litethinking.auth.infrastructure.persistence.adapter;

import com.litethinking.auth.domain.model.Role;
import com.litethinking.auth.domain.port.out.RoleRepositoryPort;
import com.litethinking.auth.infrastructure.persistence.repository.RoleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository roleJpaRepository;

    public RoleRepositoryAdapter(RoleJpaRepository roleJpaRepository) {
        this.roleJpaRepository = roleJpaRepository;
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByName(name)
                .map(entity -> new Role(entity.getId(), entity.getName()));
    }
}
