package com.undercontroll.api.application.port;

import com.undercontroll.api.application.dto.ComponentDto;
import com.undercontroll.api.application.dto.RegisterComponentRequest;
import com.undercontroll.api.application.dto.RegisterComponentResponse;
import com.undercontroll.api.application.dto.UpdateComponentRequest;

import java.util.List;

public interface ComponentPort {

    RegisterComponentResponse register(RegisterComponentRequest request);
    void updateComponent(UpdateComponentRequest request);
    List<ComponentDto> getComponents();
    List<ComponentDto> getComponentsByCategory(String category);
    List<ComponentDto> getComponentsByName(String name);

}
