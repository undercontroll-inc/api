package com.undercontroll.infrastructure.mapper;

import com.undercontroll.domain.model.Demand;
import com.undercontroll.infrastructure.persistence.entity.ComponentPartJpaEntity;
import com.undercontroll.infrastructure.persistence.entity.DemandJpaEntity;
import com.undercontroll.infrastructure.persistence.entity.OrderJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DemandMapperImpl.class, ComponentPartMapperImpl.class})
class DemandMapperTest {

    @Autowired
    private DemandMapper demandMapper;

    @Test
    @DisplayName("Should map order id from entity to domain")
    void shouldMapOrderIdFromEntityToDomain() {
        DemandJpaEntity entity = DemandJpaEntity.builder()
                .id(1)
                .quantity(3L)
                .order(OrderJpaEntity.builder().id(7).build())
                .component(ComponentPartJpaEntity.builder().id(10).build())
                .build();

        Demand demand = demandMapper.toDomain(entity);

        assertNotNull(demand);
        assertNotNull(demand.getOrder());
        assertEquals(7, demand.getOrder().getId());
    }
}
