package com.undercontroll.domain.usecase.auth;

import com.undercontroll.application.dto.auth.CreatePasswordEventRequest;
import com.undercontroll.domain.model.PasswordEvent;

public interface CreatePasswordEventPort {
    PasswordEvent execute(CreatePasswordEventRequest request);
}
