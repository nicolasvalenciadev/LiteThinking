package com.litethinking.auth.domain.port.in;

public interface AuthUseCase {

    String login(String username, String password);

    boolean validateToken(String token);
}
