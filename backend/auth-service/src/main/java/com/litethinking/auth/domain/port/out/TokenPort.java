package com.litethinking.auth.domain.port.out;

import com.litethinking.auth.domain.model.User;

public interface TokenPort {

    String generateToken(User user);

    boolean isValid(String token);

    String extractUsername(String token);
}
