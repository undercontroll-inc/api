package com.undercontroll.infrastructure.service;

import com.undercontroll.application.dto.order.ExportOrderRequest;

public interface PdfExportService {

    byte[] exportOS(ExportOrderRequest request);

}
