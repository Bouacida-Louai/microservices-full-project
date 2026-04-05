package org.sid.userservice.service;

import org.sid.userservice.entities.User;
import org.sid.userservice.exception.BadRequestException;
import org.sid.userservice.exception.ResourceNotFoundException;
import org.sid.userservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new BadRequestException("User name cannot be empty");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new BadRequestException("User email cannot be empty");
        }
        return userRepository.save(user);
    }
}
