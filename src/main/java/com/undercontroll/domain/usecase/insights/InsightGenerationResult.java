package com.undercontroll.domain.usecase.insights;

public record InsightGenerationResult(Status status, String bucketKey, String message) {

    public enum Status {
        SUCCESS,
        FAILED,
        SKIPPED_NO_BUCKET,
        SKIPPED_NO_LLM,
        SKIPPED_ALREADY_EXISTS
    }

    public static InsightGenerationResult success(String bucketKey) {
        return new InsightGenerationResult(Status.SUCCESS, bucketKey, "Insights generated");
    }

    public static InsightGenerationResult failed(String bucketKey, String message) {
        return new InsightGenerationResult(Status.FAILED, bucketKey, message);
    }

    public static InsightGenerationResult noBucket() {
        return new InsightGenerationResult(Status.SKIPPED_NO_BUCKET, null, "Market bucket is not available yet");
    }

    public static InsightGenerationResult noLlm(String bucketKey) {
        return new InsightGenerationResult(Status.SKIPPED_NO_LLM, bucketKey, "LLM provider is not configured");
    }

    public static InsightGenerationResult alreadyExists(String bucketKey) {
        return new InsightGenerationResult(Status.SKIPPED_ALREADY_EXISTS, bucketKey, "Insights already generated for bucket");
    }
}
