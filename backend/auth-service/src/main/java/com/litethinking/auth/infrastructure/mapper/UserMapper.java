package com.litethinking.auth.infrastructure.mapper;

import com.litethinking.auth.domain.model.Role;
import com.litethinking.auth.domain.model.User;
import com.litethinking.auth.infrastructure.persistence.entity.RoleEntity;
import com.litethinking.auth.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        Role role = new Role(entity.getRole().getId(), entity.getRole().getName());
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                role,
                entity.getClientId()
        );
    }

    public UserEntity toEntity(User user, RoleEntity roleEntity) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setRole(roleEntity);
        entity.setClientId(user.getClientId());
        return entity;
    }
}
