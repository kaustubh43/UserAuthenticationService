package org.ecommerce.userauthenticationservice.dtos;

import lombok.*;
import org.ecommerce.userauthenticationservice.models.Role;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    private List<Role> roles;
}
