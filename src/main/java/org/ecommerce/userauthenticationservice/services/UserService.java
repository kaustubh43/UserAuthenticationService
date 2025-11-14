package org.ecommerce.userauthenticationservice.services;

import org.ecommerce.userauthenticationservice.models.User;
import org.ecommerce.userauthenticationservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserDetails(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return null;
        }
        User user = optionalUser.get();
        return user;
    }
}
