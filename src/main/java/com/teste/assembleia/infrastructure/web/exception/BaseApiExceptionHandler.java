package com.teste.assembleia.infrastructure.web.exception;

import com.teste.assembleia.domain.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Order(1)
@Slf4j
public class BaseApiExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Recurso Não Encontrado");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Erro de Validação");
        problemDetail.setDetail("Um ou mais campos falharam na validação. Verifique os erros abaixo.");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("validationErrors", errors);

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Erro não tratado capturado pelo handler genérico.", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Erro Interno do Servidor");
        problemDetail.setDetail("Ocorreu um erro inesperado no servidor.");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }
}