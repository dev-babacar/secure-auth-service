package com.babacar.secureauthservice.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validation échouée (@Email, @NotBlank, @Size)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        "Données invalides"
                );
        problem.setType(URI.create("https://auth.local/errors/validation"));
        problem.setTitle("Validation échouée");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("errors", ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + " : " + e.getDefaultMessage())
                .toList()
        );
        return problem;
    }

    // Email déjà utilisé
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        HttpStatus status = ex.getMessage().contains("already registered")
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;

        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(status, ex.getMessage());
        problem.setType(URI.create("https://auth.local/errors/business"));
        problem.setTitle(status == HttpStatus.CONFLICT
                ? "Conflit"
                : "Requête invalide");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // Erreur inattendue
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        ProblemDetail problem = ProblemDetail
                .forStatusAndDetail(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Une erreur inattendue s'est produite"
                );
        problem.setType(URI.create("https://auth.local/errors/internal"));
        problem.setTitle("Erreur interne");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}