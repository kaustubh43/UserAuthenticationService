package org.ecommerce.userauthenticationservice.controllers;

import org.antlr.v4.runtime.misc.Pair;
import org.ecommerce.userauthenticationservice.dtos.LoginRequestDto;
import org.ecommerce.userauthenticationservice.dtos.SignUpRequestDto;
import org.ecommerce.userauthenticationservice.dtos.UserDto;
import org.ecommerce.userauthenticationservice.dtos.ValidateTokenRequestDto;
import org.ecommerce.userauthenticationservice.exceptions.PasswordMismatchException;
import org.ecommerce.userauthenticationservice.exceptions.UserNotRegisteredException;
import org.ecommerce.userauthenticationservice.models.User;
import org.ecommerce.userauthenticationservice.services.IAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IAuthenticationService authenticationService;

    @Autowired
    public AuthController(IAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        try{
           User user = authenticationService.signUp(signUpRequestDto.getEmail(), signUpRequestDto.getName(), signUpRequestDto.getPassword(), signUpRequestDto.getPhoneNumber());
           return new ResponseEntity<>(from(user), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        try{
            Pair<User, String> response = authenticationService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            String token = response.b;
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Set-Cookie", token);
            return new ResponseEntity<>(from(response.a), headers, HttpStatus.OK);
        } catch (PasswordMismatchException e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } catch (UserNotRegisteredException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<String> validateToken(@RequestBody ValidateTokenRequestDto validateTokenRequestDto) {
        boolean isValid = authenticationService.validateToken(validateTokenRequestDto.getToken(), validateTokenRequestDto.getUserId());
        if(!isValid) {
            return new ResponseEntity<>("Token is not valid.", HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>("Token valid and within expiry", HttpStatus.OK);
        }
    }

    // Todo: Implementation for user logout
    public ResponseEntity<UserDto> logout() {
        return new ResponseEntity<>(null, HttpStatus.NOT_IMPLEMENTED);
    }

    public UserDto from(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .roles(user.getRoles())
                .build();
    }
}
