package com.undercontroll.domain.usecase.component;

public interface DeleteComponentPort {
    record Input(
            Integer componentId
    ) {}

    record Output(
            Boolean success,
            String message
    ) {}

    Output execute(Input input);
}
