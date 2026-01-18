package com.qburst.blog_application.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleUserNameAlreadyExistsException(UserNameAlreadyExistsException ex, WebRequest request) {
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Username Conflict")
                .message(ex.getMessage()) // This works now because we called super(message)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
    }

    @ExceptionHandler(InvalidUserNameOrPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleInvalidUserNameOrPasswordException(InvalidUserNameOrPasswordException ex, WebRequest request) {
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        // We cast to Object to match the parent signature
        return new ResponseEntity<>(
                ApiError.builder()
                        .timestamp(LocalDateTime.now())
                        .status(status.value())
                        .error("Validation Failed")
                        .message(errorMessage)
                        .path(request.getDescription(false).replace("uri=", ""))
                        .build(),
                status
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleOthers(Exception ex, WebRequest request) {
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
    }

    @ExceptionHandler(TokenProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleTokenProcessingException(TokenProcessingException ex, WebRequest request) {
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleUserNameAlreadyExistsException(UserNotFoundException ex, WebRequest request) {
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("User not found")
                .message(ex.getMessage()) // This works now because we called super(message)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You do not have permission to access this resource.");
    }
}
