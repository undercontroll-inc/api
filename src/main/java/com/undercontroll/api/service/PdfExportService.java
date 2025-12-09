package com.undercontroll.api.service;

import com.undercontroll.api.dto.ExportOrderRequest;

public interface PdfExportService {

    byte[] exportOS(ExportOrderRequest request);

}
