package com.undercontroll.infrastructure.config;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ApiResponseDocumentation {


    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(
            responseCode = "200",
            description = "Request processed successfully"
    )
    public @interface SuccessResponse {
    }


    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(
            responseCode = "201",
            description = "Resource created successfully"
    )
    public @interface CreatedResponse {
    }


    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(
            responseCode = "204",
            description = "Request processed successfully with no content to return"
    )
    public @interface NoContentResponse {
    }


    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(
            responseCode = "400",
            description = "Bad request - the provided data is incorrect or incomplete",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "Validation error example",
                            value = """
                                    {
                                        "timestamp": "2025-11-23T14:30:00",
                                        "status": 400,
                                        "error": "Bad Request",
                                        "message": "Request validation failed",
                                        "path": "/v1/api/components",
                                        "code": "VALIDATION_ERROR",
                                        "errors": [
                                            { "field": "item", "message": "must not be blank" }
                                        ]
                                    }
                                    """
                    )
            )
    )
    public @interface BadRequestResponse {
    }


    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - JWT token is invalid, expired, or missing",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "Authentication error example",
                            value = """
                                    {
                                        "timestamp": "2025-11-23T14:30:00",
                                        "status": 401,
                                        "error": "Unauthorized",
                                        "message": "Access token has expired",
                                        "path": "/v1/api/components",
                                        "code": "TOKEN_EXPIRED"
                                    }
                                    """
                    )
            )
    )
    public @interface UnauthorizedResponse {
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden - the user does not have permission to access this resource",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "Authorization error example",
                            value = """
                                    {
                                        "timestamp": "2025-11-23T14:30:00",
                                        "status": 403,
                                        "error": "Forbidden",
                                        "message": "The user does not have permission to access this resource",
                                        "path": "/v1/api/components",
                                        "code": "ORDER_UNAUTHORIZED"
                                    }
                                    """
                    )
            )
    )
    public @interface ForbiddenResponse {
    }


    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(
            responseCode = "404",
            description = "Not found - the requested resource does not exist",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "Resource not found example",
                            value = """
                                    {
                                        "timestamp": "2025-11-23T14:30:00",
                                        "status": 404,
                                        "error": "Not Found",
                                        "message": "Component with id 123 was not found",
                                        "path": "/v1/api/components/123",
                                        "code": "COMPONENT_NOT_FOUND"
                                    }
                                    """
                    )
            )
    )
    public @interface NotFoundResponse {
    }


    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(
            responseCode = "409",
            description = "Conflict - the resource already exists or there is a data conflict",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "Conflict example",
                            value = """
                                    {
                                        "timestamp": "2025-11-23T14:30:00",
                                        "status": 409,
                                        "error": "Conflict",
                                        "message": "A component with this name already exists",
                                        "path": "/v1/api/components",
                                        "code": "COMPONENT_CREATE_INVALID"
                                    }
                                    """
                    )
            )
    )
    public @interface ConflictResponse {
    }


    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error - an unexpected error occurred",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "Internal error example",
                            value = """
                                    {
                                        "timestamp": "2025-11-23T14:30:00",
                                        "status": 500,
                                        "error": "Internal Server Error",
                                        "message": "An unexpected error occurred while processing the request",
                                        "path": "/v1/api/components",
                                        "code": "INTERNAL_SERVER_ERROR"
                                    }
                                    """
                    )
            )
    )
    public @interface InternalServerErrorResponse {
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request processed successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or expired token",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public @interface StandardApiResponses {
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request processed successfully"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Resource not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public @interface GetApiResponses {
    }


    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Resource created successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - the resource already exists",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public @interface PostApiResponses {
    }


    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource updated successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Resource not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public @interface PutApiResponses {
    }


    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource updated successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Resource not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public @interface PatchApiResponses {
    }


    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Resource deleted successfully"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Resource not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public @interface DeleteApiResponses {
    }


    @Schema(description = "Standard error response body")
    public static class ErrorResponse {
        @Schema(description = "Error timestamp", example = "2025-11-23T14:30:00")
        private String timestamp;

        @Schema(description = "HTTP status code", example = "400")
        private Integer status;

        @Schema(description = "HTTP reason phrase", example = "Bad Request")
        private String error;

        @Schema(description = "Human-readable error message", example = "Request validation failed")
        private String message;

        @Schema(description = "Request path that caused the error", example = "/v1/api/components")
        private String path;

        @Schema(description = "Stable machine-readable error code", example = "VALIDATION_ERROR")
        private String code;

        @Schema(description = "Field-level validation errors, present only for validation failures")
        private java.util.List<FieldError> errors;

        @Schema(description = "Field-level validation error")
        public static class FieldError {
            @Schema(description = "Invalid field name", example = "item")
            private String field;

            @Schema(description = "Validation message", example = "must not be blank")
            private String message;
        }
    }
}
