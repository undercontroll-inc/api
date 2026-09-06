package com.undercontroll.domain.usecase.component.impl;

import com.undercontroll.domain.usecase.component.DeleteComponentPort;
import com.undercontroll.domain.model.ComponentPart;
import com.undercontroll.domain.exception.ComponentNotFoundException;
import com.undercontroll.domain.exception.InvalidDeleteComponentException;
import com.undercontroll.domain.gateway.ComponentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteComponentImpl implements DeleteComponentPort {

    private final ComponentGateway componentGateway;

    @Override
    @Transactional
    @CacheEvict(value = {"components", "componentsByCategory", "componentsByName", "component"}, allEntries = true)
    public void execute(Integer componentId) {
        validateDelete(componentId);

        ComponentPart component = componentGateway.findById(componentId)
                .orElseThrow(() -> new ComponentNotFoundException("Component not found with id " + componentId));

        log.info("Attempting to delete component with id: {}, name: {}", componentId, component.getName());

        if (!component.getDemands().isEmpty()) {
            log.warn("Component {} has {} active demand(s) that will be removed",
                    componentId, component.getDemands().size());

            component.getDemands().forEach(demand ->
                log.info("Removing demand {} for component {} in order {}",
                        demand.getId(), componentId, demand.getOrder().getId())
            );
        }

        componentGateway.deleteById(component.getId());
        log.info("Component {} successfully deleted", componentId);
    }

    private void validateDelete(Integer componentId) {
        if (componentId == null || componentId <= 0) {
            throw new InvalidDeleteComponentException("Invalid id for deletion");
        }
    }
}
