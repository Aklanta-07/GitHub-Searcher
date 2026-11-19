package com.example.githubsearcher.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.githubsearcher.dto.response.ApiErrorResponse;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
	        MethodArgumentNotValidException ex) {

	    String message = ex.getBindingResult()
	            .getFieldErrors()
	            .stream()
	            .findFirst()
	            .map(error -> error.getDefaultMessage())
	            .orElse("Invalid request");

	    return ResponseEntity.badRequest()
	            .body(new ApiErrorResponse(message));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
	        ConstraintViolationException ex) {

	    String message = ex.getConstraintViolations()
	            .stream()
	            .findFirst()
	            .map(violation -> violation.getMessage())
	            .orElse("Invalid request");

	    return ResponseEntity.badRequest()
	            .body(new ApiErrorResponse(message));
	}

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex) {

        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        String message = "Invalid value for parameter: " + ex.getName();

        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(message));
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedJson(
            HttpMessageNotReadableException ex) {

        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse("Malformed or invalid request body"));
    }
    
    @ExceptionHandler(GitHubApiException.class)
    public ResponseEntity<ApiErrorResponse> handleGitHubApiException(
            GitHubApiException ex) {

        return ResponseEntity
                .status(ex.getStatus())
                .body(new ApiErrorResponse(ex.getMessage()));
    }
}