package org.ecommerce.userauthenticationservice.controllers;

import org.ecommerce.userauthenticationservice.dtos.LoginRequestDto;
import org.ecommerce.userauthenticationservice.dtos.SignUpRequestDto;
import org.ecommerce.userauthenticationservice.dtos.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        // Todo: Implementation for user signup
        return null;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return null;
    }

    // Todo: Implementation for user logout
}
