package com.undercontroll.api.controller.impl;

import com.undercontroll.api.controller.DemandApi;
import com.undercontroll.api.dto.CreateDemandRequest;
import com.undercontroll.api.dto.DemandDto;
import com.undercontroll.api.model.Demand;
import com.undercontroll.api.service.DemandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/api/demands", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DemandController implements DemandApi {

    private final DemandService demandService;

    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Demand> createDemand(@RequestBody CreateDemandRequest request) {
        Demand demand = demandService.createDemand(request);
        return ResponseEntity.status(201).body(demand);
    }

    @Override
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<DemandDto>> getDemandsByOrder(@PathVariable Integer orderId) {
        List<DemandDto> demands = demandService.getDemandsByOrderId(orderId);
        return demands.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(demands);
    }

    @Override
    @GetMapping("/order/{orderId}/component/{componentId}")
    public ResponseEntity<DemandDto> getDemandByOrderAndComponent(@PathVariable Integer orderId, @PathVariable Integer componentId) {
        DemandDto demand = demandService.getDemandByOrderAndComponentId(orderId, componentId);
        return ResponseEntity.ok(demand);
    }

    @Override
    @DeleteMapping("/{demandId}")
    public ResponseEntity<Void> deleteDemand(@PathVariable Integer demandId) {
        demandService.deleteDemandById(demandId);
        return ResponseEntity.ok().build();
    }

    @Override
    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<Void> deleteAllDemands(@PathVariable Integer orderId) {
        demandService.deleteAllDemandsByOrderId(orderId);
        return ResponseEntity.ok().build();
    }
}

