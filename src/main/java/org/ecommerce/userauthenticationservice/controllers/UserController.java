package org.ecommerce.userauthenticationservice.controllers;

import org.ecommerce.userauthenticationservice.dtos.UserDto;
import org.ecommerce.userauthenticationservice.models.User;
import org.ecommerce.userauthenticationservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        User user = userService.getUserDetails(id);
        System.out.println(user.getName());

        return from(user);
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
