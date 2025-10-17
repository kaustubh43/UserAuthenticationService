package org.ecommerce.userauthenticationservice.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class User extends BaseModel {
    private String name;

    private String email;

    private String password;

    private String phoneNumber;

    private List<Role> role = new ArrayList<>();
}
