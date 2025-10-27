package org.ecommerce.userauthenticationservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Session extends BaseModel {
    private String token;
    @ManyToOne
    private User user;
}
// 1                M
// User             Session
// 1                1
// 1:M
