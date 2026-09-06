package com.undercontroll.application.controller.impl;

import com.undercontroll.domain.usecase.demand.*;
import com.undercontroll.application.controller.DemandApi;
import com.undercontroll.application.dto.demand.CreateDemandRequest;
import com.undercontroll.application.dto.demand.DemandDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DemandController implements DemandApi {

    private final CreateDemandPort createDemandPort;
    private final GetDemandsPort getDemandsPort;
    private final DeleteDemandPort deleteDemandPort;
    private final DeleteAllDemandsByOrderPort deleteAllDemandsByOrderPort;

    @Override
    public ResponseEntity<DemandDto> createDemand(Integer orderId, CreateDemandRequest request) {
        return ResponseEntity.status(201).body(createDemandPort.execute(orderId, request));
    }

    @Override
    public ResponseEntity<List<DemandDto>> getDemands(Integer orderId, Integer componentId) {
        return ResponseEntity.ok(getDemandsPort.execute(orderId, componentId));
    }

    @Override
    public ResponseEntity<Void> deleteDemand(Integer orderId, Integer demandId) {
        deleteDemandPort.execute(demandId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteAllDemands(Integer orderId) {
        deleteAllDemandsByOrderPort.execute(orderId);
        return ResponseEntity.noContent().build();
    }
}
