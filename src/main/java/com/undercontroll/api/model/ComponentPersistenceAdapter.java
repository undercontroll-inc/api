package com.undercontroll.api.model;

import com.undercontroll.api.model.ComponentPart;
import com.undercontroll.api.repository.ComponentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ComponentPersistenceAdapter {

    private final ComponentJpaRepository repository;

    public void save(ComponentPart componentPart) {
        this.repository.save(componentPart);
    }

    public void update(ComponentPart componentPart) {
        this.save(componentPart);
    }

    public Optional<ComponentPart> findById(Integer id) {
        return repository.findById(id);
    }

    public List<ComponentPart> findAll() {
        return this.repository.findAll();
    }

    public  List<ComponentPart> findByCategory(String category) {
        return this.repository.findByCategory(category);
    }

    public List<ComponentPart> findByName(String name) {
        return this.repository.findByName(name);
    }

    public void delete(ComponentPart component) {
        repository.delete(component);
    }

}
