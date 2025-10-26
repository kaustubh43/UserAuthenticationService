package org.ecommerce.userauthenticationservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.Getter;
import lombok.Setter;
import org.ecommerce.userauthenticationservice.repositories.UserRepository;

@Getter
@Setter
@Entity
public class Role extends BaseModel {
    private String value;
}
