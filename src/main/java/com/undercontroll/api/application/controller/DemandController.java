package com.undercontroll.api.application.controller;

import com.undercontroll.api.application.dto.CreateDemandRequest;
import com.undercontroll.api.application.port.DemandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/demands")
public class DemandController {

    private final DemandPort demandPort;

    @PostMapping
    public ResponseEntity<Void> createDemand(
            @RequestBody CreateDemandRequest createDemandRequest
    ) {
        demandPort.createDemand(
                createDemandRequest
        );

        return ResponseEntity.status(201).build();
    }

}
