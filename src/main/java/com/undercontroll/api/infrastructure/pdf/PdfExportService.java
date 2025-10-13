package com.undercontroll.api.infrastructure.pdf;

import com.undercontroll.api.application.dto.ServiceOrderDto;

public interface PdfExportService {

    void exportOS(ServiceOrderDto serviceOrder);

}
