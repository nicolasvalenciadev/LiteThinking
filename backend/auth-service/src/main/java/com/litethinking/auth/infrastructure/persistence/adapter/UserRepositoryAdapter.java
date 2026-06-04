package com.litethinking.auth.infrastructure.persistence.adapter;

import com.litethinking.auth.domain.model.User;
import com.litethinking.auth.domain.port.out.UserRepositoryPort;
import com.litethinking.auth.infrastructure.mapper.UserMapper;
import com.litethinking.auth.infrastructure.persistence.entity.RoleEntity;
import com.litethinking.auth.infrastructure.persistence.entity.UserEntity;
import com.litethinking.auth.infrastructure.persistence.repository.RoleJpaRepository;
import com.litethinking.auth.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final UserMapper userMapper;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository,
                                  RoleJpaRepository roleJpaRepository,
                                  UserMapper userMapper) {
        this.userJpaRepository = userJpaRepository;
        this.roleJpaRepository = roleJpaRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .map(userMapper::toDomain);
    }

    @Override
    public User save(User user) {
        RoleEntity roleEntity = roleJpaRepository.findById(user.getRole().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Rol no encontrado: " + user.getRole().getId()));
        UserEntity entity = userMapper.toEntity(user, roleEntity);
        return userMapper.toDomain(userJpaRepository.save(entity));
    }
}
