package com.undercontroll.api.application.controller;

import com.undercontroll.api.application.dto.*;
import com.undercontroll.api.application.port.UserPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserPort userPort;

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(
            @RequestBody CreateUserRequest request
    ) {
        CreateUserResponse user = userPort.createUser(request);

        return ResponseEntity.status(201).body(user);
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthUserResponse> auth(
            @RequestBody @Valid AuthUserRequest request
    ) {
        AuthUserResponse response = userPort.authUser(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/google")
    public ResponseEntity<AuthUserResponse> authGoogle(
            @RequestBody @Valid AuthGoogleRequest request
    ) {
        AuthUserResponse response = userPort.authUserByGoogle(request);

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<Void> updateOrder(
            @RequestBody UpdateUserRequest request
    ) {
        userPort.updateUser(request);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> users = userPort.getUsers();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUsersById(
            @PathVariable Integer userId
    ) {
        UserDto user = userPort.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Integer userId
    ) {
        userPort.deleteUser(userId);

        return ResponseEntity.ok().build();
    }

}
