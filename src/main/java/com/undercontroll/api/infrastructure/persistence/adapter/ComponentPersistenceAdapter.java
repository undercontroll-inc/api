package com.undercontroll.api.infrastructure.persistence.adapter;

import com.undercontroll.api.domain.model.ComponentPart;
import com.undercontroll.api.infrastructure.persistence.repository.ComponentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ComponentPersistenceAdapter {

    private final ComponentJpaRepository repository;

    public ComponentPersistenceAdapter(ComponentJpaRepository repository) {
        this.repository = repository;
    }

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

}
