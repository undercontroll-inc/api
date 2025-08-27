package com.undercontroll.api.application.port;

import com.undercontroll.api.application.dto.*;
import java.util.List;

public interface UserPort {

    CreateUserResponse createUser(CreateUserRequest request);
    void updateUser(UpdateUserRequest request);
    List<UserDto> getUsers();
    UserDto getUserById(Integer userId);
    void deleteUser(Integer userId);

}
