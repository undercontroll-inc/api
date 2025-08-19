package com.undercontroll.api.domain.service;

import com.undercontroll.api.application.port.PortExemplo;
import com.undercontroll.api.infrastructure.persistence.adapter.PersistenceExemploAdapterJpa;

public class ServiceExemplo implements PortExemplo {

    private final PersistenceExemploAdapterJpa persistenceExemploAdapterJpa;

    public ServiceExemplo(PersistenceExemploAdapterJpa persistenceExemploAdapterJpa) {
        this.persistenceExemploAdapterJpa = persistenceExemploAdapterJpa;
    }


}
