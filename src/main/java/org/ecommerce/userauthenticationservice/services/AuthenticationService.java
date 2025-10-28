package org.ecommerce.userauthenticationservice.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.antlr.v4.runtime.misc.Pair;
import org.ecommerce.userauthenticationservice.exceptions.PasswordMismatchException;
import org.ecommerce.userauthenticationservice.exceptions.UserExistsException;
import org.ecommerce.userauthenticationservice.exceptions.UserNotRegisteredException;
import org.ecommerce.userauthenticationservice.models.Role;
import org.ecommerce.userauthenticationservice.models.Session;
import org.ecommerce.userauthenticationservice.models.Status;
import org.ecommerce.userauthenticationservice.models.User;
import org.ecommerce.userauthenticationservice.repositories.SessionRepository;
import org.ecommerce.userauthenticationservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticationService implements IAuthenticationService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SessionRepository sessionRepository;
    private final SecretKey secretKey;
    private final SecurityExpressionHandler securityExpressionHandler;

    @Autowired
    public AuthenticationService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, SessionRepository sessionRepository, SecretKey secretKey, SecurityExpressionHandler securityExpressionHandler) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionRepository = sessionRepository;
        this.secretKey = secretKey;
        this.securityExpressionHandler = securityExpressionHandler;
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
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("issued_by", "ecommerce-app");
        long currentTimeMillis = System.currentTimeMillis();
        claims.put("iat", currentTimeMillis / 1000); // Issued at
        claims.put("exp", (currentTimeMillis / 1000) + 3600); // Expiry: Token valid for 1 hour
        claims.put("roles", user.getRoles());

        String token = Jwts.builder().claims(claims).signWith(secretKey).compact();
        Session session = new Session();
        session.setUser(user);
        session.setToken(token);
        session.setStatus(Status.ACTIVE);

        sessionRepository.save(session);
        return new Pair<>(user, token);
    }
    // Validate JWT Token
    // -> Check if the token we receive is valid or not, present in the db or not.
    // -> Check if the token is expired or not by doing reverse engineering.
    public boolean validateToken(String token, Long userId) {
        Optional<Session> optionalSession = sessionRepository.findByTokenAndUser_Id(token, userId);
        if(optionalSession.isEmpty()) {
            return false;
        }

        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        Claims claims = jwtParser.parseClaimsJws(token).getPayload();
        Long exp = claims.get("exp", Long.class);
        // Validate roles.
        List<Role> roles = claims.get("roles", List.class);

        long currentTimeMillis = System.currentTimeMillis() / 1000;

        System.out.println("exp: " + exp);
        System.out.println("currentTimeMillis: " + currentTimeMillis);

        if(exp < currentTimeMillis ) {
            Session session = optionalSession.get();
            // Expired token, set session as INACTIVE
            session.setStatus(Status.INACTIVE);
            sessionRepository.save(session);
            return false;
        }
        return true;
    }
}
