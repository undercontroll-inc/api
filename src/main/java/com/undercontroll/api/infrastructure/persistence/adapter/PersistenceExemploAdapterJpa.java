package com.undercontroll.api.infrastructure.persistence.adapter;

public class PersistenceExemploAdapterJpa {

    private final MapperExemplo  mapper;
    private final RepositoryExemplo repository;

    public PersistenceExemploAdapterJpa(MapperExemplo mapper, RepositoryExemplo repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

}
