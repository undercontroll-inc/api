package com.undercontroll.api.model;

import com.undercontroll.api.exception.UserNotFoundException;
import com.undercontroll.api.model.User;
import com.undercontroll.api.repository.UserJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter {

    private final UserJpaRepository repository;

    public User saveUser(User user) {return repository.save(user);}

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
            throw new UserNotFoundException("User not found for deletion");
        }

        repository.delete(userFound.get());
    }

    public Optional<User> getUserByEmail(String email) {
        return repository.findUserByEmail(email);
    }

    public Optional<User> getUserByPhone(String phone) {
        return repository.findUserByPhone(phone);
    }

    public Optional<User> getUserByCpf(String cpf) {
        return repository.findUserByCpf(cpf);
    }

    @Transactional
    public void updateUser(User user) {
        repository.save(user);
    }
}