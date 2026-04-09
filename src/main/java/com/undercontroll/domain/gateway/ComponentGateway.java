package com.undercontroll.domain.gateway;

import com.undercontroll.domain.model.ComponentPart;

import java.util.List;
import java.util.Optional;

public interface ComponentGateway {

    ComponentPart save(ComponentPart component);

    void deleteById(Integer id);

    Optional<ComponentPart> findById(Integer id);

    List<ComponentPart> findAll();

    List<ComponentPart> findByName(String name);

    List<ComponentPart> findByCategory(String category);

}
