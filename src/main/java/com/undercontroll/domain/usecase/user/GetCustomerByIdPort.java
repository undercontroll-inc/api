package com.undercontroll.domain.usecase.user;

import com.undercontroll.application.dto.UserDto;

public interface GetCustomerByIdPort {
    record Input(
            Integer customerId
    ) {}

    record Output(
            UserDto customer
    ) {}

    Output execute(Input input);
}
