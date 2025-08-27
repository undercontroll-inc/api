package com.undercontroll.api.infrastructure.persistence.adapter;

import com.undercontroll.api.domain.exceptions.UserNotFoundException;
import com.undercontroll.api.domain.model.User;
import com.undercontroll.api.infrastructure.persistence.repository.UserJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserPersistenceAdapter {

    private final UserJpaRepository repository;

    public UserPersistenceAdapter(UserJpaRepository repository) {
        this.repository = repository;
    }

    public User saveUser(User user) {
        return repository.save(user);
    }

    public List<User> getUsers() {
        return repository.findAll();
    }

    public User getUsersById(Integer userId) {
        return repository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    public void deleteUser (Integer userId) {
        Optional<User> userFound = repository.findById(userId);

        if (userFound.isEmpty()) {
            throw new UserNotFoundException("Order item not found for deletion");
        }

        repository.delete(userFound.get());
    }

    @Transactional
    public void updateUser(User user) {
        repository.save(user);
    }
}