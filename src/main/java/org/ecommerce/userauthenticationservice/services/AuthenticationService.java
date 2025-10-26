package org.ecommerce.userauthenticationservice.services;

import org.ecommerce.userauthenticationservice.exceptions.PasswordMismatchException;
import org.ecommerce.userauthenticationservice.exceptions.UserExistsException;
import org.ecommerce.userauthenticationservice.exceptions.UserNotRegisteredException;
import org.ecommerce.userauthenticationservice.models.User;
import org.ecommerce.userauthenticationservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService implements IAuthenticationService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User signUp(String email, String name, String password, String phoneNumber) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            throw new UserExistsException("User already exists with email: " + email);
        }

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhoneNumber(phoneNumber);
        userRepository.save(user);
        return user;
    }

    @Override
    public User login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UserNotRegisteredException("User not registered. Email: " + email);
        }
        User user = optionalUser.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordMismatchException("Incorrect password for email: " + email);
        }
        // Todo: Generate JWT
        return user;
    }
}
