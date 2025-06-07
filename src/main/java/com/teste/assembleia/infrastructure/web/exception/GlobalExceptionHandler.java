package com.teste.assembleia.infrastructure.web.exception;

import com.teste.assembleia.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Recurso não Encontrado");
//        problemDetail.setType(URI.create("https://seusite.com/errors/nao-encontrado"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

//@ExceptionHandler(BusinessException.class)
//    public ProblemDetail handleRegraDeNegocio(BusinessException ex) {
//        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
//        problemDetail.setTitle("Conflito de Regra de Negócio");
//        problemDetail.setType(URI.create("https://seusite.com/errors/regra-de-negocio"));
//        problemDetail.setProperty("timestamp", Instant.now());
//        return problemDetail;
//    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setTitle("Erro inesperado no servidor");
//        problemDetail.setType(URI.create("https://seusite.com/errors/regra-de-negocio"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
