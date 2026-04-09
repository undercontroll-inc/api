package com.undercontroll.domain.usecase.user.impl;

import com.undercontroll.domain.usecase.user.GetCustomerByIdPort;
import com.undercontroll.domain.model.User;
import com.undercontroll.domain.gateway.UserGateway;
import com.undercontroll.application.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetCustomerByIdImpl implements GetCustomerByIdPort {

    private final UserGateway userGateway;

    @Override
    public Output execute(Input input) {
        Optional<User> customer = userGateway.findCustomerById(input.customerId());
        if (customer.isEmpty()) {
            return new Output(null);
        }
        User u = customer.get();
        return new Output(new UserDto(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getLastName(),
                u.getAddress(),
                u.getCpf(),
                u.getCEP(),
                u.getPhone(),
                u.getAvatarUrl(),
                u.getHasWhatsApp(),
                u.getAlreadyRecurrent(),
                u.getInFirstLogin(),
                u.getUserType()
        ));
    }
}
