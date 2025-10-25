package org.ecommerce.userauthenticationservice.dtos;

import lombok.Getter;
import lombok.Setter;
import org.ecommerce.userauthenticationservice.models.Role;

import java.util.List;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private List<Role> roles;
}
