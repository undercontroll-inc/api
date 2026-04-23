package com.undercontroll.domain.model;

import java.util.List;

public record PaginatedResult<T>(List<T> content, long totalElements) {}
