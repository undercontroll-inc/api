package com.undercontroll.domain.usecase.user.impl;

import com.undercontroll.application.dto.user.UserDto;
import com.undercontroll.application.mapper.UserDtoMapper;
import com.undercontroll.domain.usecase.user.GetUsersPort;
import com.undercontroll.domain.enums.UserType;
import com.undercontroll.domain.gateway.UserGateway;
import com.undercontroll.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUsersImpl implements GetUsersPort {

    private final UserGateway userGateway;
    private final UserDtoMapper userDtoMapper;

    @Override
    public List<UserDto> execute(UserType type, Boolean hasEmail) {
        List<User> users;

        if (UserType.CUSTOMER.equals(type)) {
            users = Boolean.TRUE.equals(hasEmail)
                    ? userGateway.findAllCustomersThatHaveEmail()
                    : userGateway.findAllCustomers();
        } else {
            users = userGateway.findAll();
        }

        return users.stream()
                .map(userDtoMapper::toDto)
                .toList();
    }
}
