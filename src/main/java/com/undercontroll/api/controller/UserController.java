package com.undercontroll.api.controller;

import com.undercontroll.api.dto.*;
import com.undercontroll.api.model.User;
import com.undercontroll.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(
            @RequestBody CreateUserRequest request
    ) {
        CreateUserResponse user = service.createUser(request);

        return ResponseEntity.status(201).body(user);
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthUserResponse> auth(
            @RequestBody @Valid AuthUserRequest request
    ) {
        AuthUserResponse response = service.authUser(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/google")
    public ResponseEntity<AuthUserResponse> authGoogle(
            @RequestBody @Valid AuthGoogleRequest request
    ) {
        AuthUserResponse response = service.authUserByGoogle(request);

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<Void> updateOrder(
            @RequestBody UpdateUserRequest request
    ) {
        service.updateUser(request);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> users = service.getUsers();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(
            @PathVariable Integer userId
    ) {
        User user = service.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Integer userId
    ) {
        service.deleteUser(userId);

        return ResponseEntity.ok().build();
    }

}
