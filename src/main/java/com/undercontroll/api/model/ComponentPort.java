package com.undercontroll.api.model;

import com.undercontroll.api.dto.ComponentDto;
import com.undercontroll.api.dto.RegisterComponentRequest;
import com.undercontroll.api.dto.RegisterComponentResponse;
import com.undercontroll.api.dto.UpdateComponentRequest;

import java.util.List;

public interface ComponentPort {

    RegisterComponentResponse register(RegisterComponentRequest request);
    void updateComponent(UpdateComponentRequest request);
    List<ComponentDto> getComponents();
    List<ComponentDto> getComponentsByCategory(String category);
    List<ComponentDto> getComponentsByName(String name);
    void deleteComponent(Integer componentId);

}
