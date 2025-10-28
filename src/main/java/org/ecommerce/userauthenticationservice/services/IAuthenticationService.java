package org.ecommerce.userauthenticationservice.services;

import org.antlr.v4.runtime.misc.Pair;
import org.ecommerce.userauthenticationservice.models.User;

public interface IAuthenticationService {
    User signUp(String email, String name, String password, String phoneNumber);

    Pair<User,String> login(String email, String password);

    boolean validateToken(String token, Long userId);
}
