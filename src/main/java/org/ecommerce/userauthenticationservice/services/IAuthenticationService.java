package org.ecommerce.userauthenticationservice.services;

import org.ecommerce.userauthenticationservice.models.User;

public interface IAuthenticationService {
    User signUp(String email, String name, String password, String phoneNumber);

    User login(String email, String password);
}
