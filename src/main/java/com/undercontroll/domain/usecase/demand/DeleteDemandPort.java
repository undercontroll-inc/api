package com.undercontroll.domain.usecase.demand;

public interface DeleteDemandPort {
    record Input(
            Integer demandId
    ) {}

    record Output(
            Boolean success,
            String message
    ) {}

    Output execute(Input input);
}
