package com.undercontroll.api.model;

import com.undercontroll.api.dto.*;
import java.util.List;

public interface UserPort {

    CreateUserResponse createUser(CreateUserRequest request);
    AuthUserResponse authUser(AuthUserRequest request);
    void updateUser(UpdateUserRequest request);
    List<UserDto> getUsers();
    UserDto getUserById(Integer userId);
    void deleteUser(Integer userId);
    AuthUserResponse authUserByGoogle(AuthGoogleRequest request);

}
