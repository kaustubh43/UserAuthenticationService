package org.ecommerce.userauthenticationservice.repositories;

import org.ecommerce.userauthenticationservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
