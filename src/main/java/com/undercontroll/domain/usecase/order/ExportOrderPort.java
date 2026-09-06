package com.undercontroll.domain.usecase.order;

public interface ExportOrderPort {
    byte[] execute(Integer orderId);
}
