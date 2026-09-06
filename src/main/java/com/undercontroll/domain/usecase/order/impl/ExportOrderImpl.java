package com.undercontroll.domain.usecase.order.impl;

import com.undercontroll.infrastructure.service.StorageService;
import com.undercontroll.domain.usecase.order.ExportOrderPort;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.exception.OrderNotFoundException;
import com.undercontroll.domain.gateway.OrderGateway;
import com.undercontroll.infrastructure.service.PdfExportService;
import com.undercontroll.application.dto.order.ExportOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportOrderImpl implements ExportOrderPort {

    private final OrderGateway orderGateway;
    private final PdfExportService pdfExportService;
    private final StorageService storageService;

    @Value("${aws.s3.export-bucket}")
    private String pdfBucket;

    @Override
    public byte[] execute(Integer orderId) {
        log.info("Exporting order {} to PDF", orderId);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Order order = orderGateway.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        String.format("Order with id %d not found", orderId)));

        List<ExportOrderRequest.ProductInfo> produtos = order.getOrderItems().stream()
                .map(item -> new ExportOrderRequest.ProductInfo(
                        item.getType() + " " + item.getBrand() + " " + item.getModel(),
                        item.getVolt(),
                        item.getSeries()
                ))
                .toList();

        List<ExportOrderRequest.PartInfo> pecas = order.getDemands().stream()
                .map(part -> new ExportOrderRequest.PartInfo(
                        part.getQuantity(),
                        part.getComponent().getName(),
                        String.format("R$ %.2f", part.getComponent().getPrice() * part.getQuantity())
                ))
                .toList();

        final var receivedAt = order.getReceived_at() != null ? dtf.format(order.getReceived_at()) : null;
        final var completedAt = order.getCompletedTime() != null ? dtf.format(order.getCompletedTime()) : null;

        ExportOrderRequest exportRequest = new ExportOrderRequest(
                order.getId().toString(),
                order.getId().toString(),
                order.getNf(),
                receivedAt,
                "Loja",
                produtos,
                order.getUser().getEmail(),
                order.getUser().getAddress(),
                order.getUser().getPhone(),
                receivedAt,
                pecas,
                String.format("R$ %.2f", order.calculateTotal()),
                completedAt,
                "Técnico",
                order.isFabricGuarantee(),
                null, // orcamento - adicionar este campo ao Order se necessário
                order.isReturnGuarantee()
        );

        byte[] pdfData = pdfExportService.exportOS(exportRequest);

        final var key = String.format(order.getId().toString(), order.getNf(), ".pdf");

        storageService.putObject(pdfBucket, key, pdfData, Optional.of("application/pdf"));

        return pdfData;
    }
}
