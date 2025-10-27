package org.ecommerce.userauthenticationservice.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.antlr.v4.runtime.misc.Pair;
import org.ecommerce.userauthenticationservice.exceptions.PasswordMismatchException;
import org.ecommerce.userauthenticationservice.exceptions.UserExistsException;
import org.ecommerce.userauthenticationservice.exceptions.UserNotRegisteredException;
import org.ecommerce.userauthenticationservice.models.User;
import org.ecommerce.userauthenticationservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
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
    public Pair<User,String> login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UserNotRegisteredException("User not registered. Email: " + email);
        }
        User user = optionalUser.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordMismatchException("Incorrect password for email: " + email);
        }
//        String message = "{\n" +
//                "   \"email\": \"kaustubh@gmail.com\",\n" +
//                "   \"roles\": [\n" +
//                "      \"instructor\",\n" +
//                "      \"buddy\"\n" +
//                "   ],\n" +
//                "   \"expirationDate\": \"2ndApril2026\"\n" +
//                "}";
//        byte[] content = message.getBytes(StandardCharsets.UTF_8);
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("issued_by", "ecommerce-app");
        long currentTimeMillis = System.currentTimeMillis();
        claims.put("iat", currentTimeMillis); // Issued at
        claims.put("exp", currentTimeMillis + 3600000); // Expiry: Token valid for 1 hour

        MacAlgorithm algorithm = Jwts.SIG.HS256;
        SecretKey secretKey = algorithm.key().build();

        String token = Jwts.builder().claims(claims).signWith(secretKey).compact();

        return new Pair<>(user, token);
    }
    // Validate JWT Token
    // -> Check if the token we receive is valid or not, present in the db or not.
    // -> Check if the token is expired or not by doing reverse engineering.
}
