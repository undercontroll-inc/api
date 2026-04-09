package com.undercontroll.domain.usecase.user;

public interface DeleteUserPort {
    record Input(
            Integer userId
    ) {}

    record Output(
            Boolean success,
            String message
    ) {}

    Output execute(Input input);
}
