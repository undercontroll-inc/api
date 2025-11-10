package com.undercontroll.api.service;

import com.undercontroll.api.dto.ServiceOrderDto;

public interface PdfExportService {

    void exportOS(ServiceOrderDto serviceOrder);

}
