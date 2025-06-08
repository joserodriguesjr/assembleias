package com.teste.assembleia.infrastructure.web.exception;

import com.teste.assembleia.domain.exception.ResourceNotFoundException;
import com.teste.assembleia.domain.exception.VotingSessionStillRunningException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Recurso não Encontrado");
//        problemDetail.setType(URI.create("https://seusite.com/errors/nao-encontrado"));

        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(VotingSessionStillRunningException.class)
    public ProblemDetail handleSessionStillRunning(VotingSessionStillRunningException ex) {
        LocalDateTime now = LocalDateTime.now();
        Duration remainingTime = Duration.between(now, ex.getEndTime());

        long minutes = remainingTime.toMinutesPart();
        long seconds = remainingTime.toSecondsPart();

        StringBuilder formattedTime = new StringBuilder();
        if (minutes > 0) {
            formattedTime.append(minutes).append(minutes == 1 ? " minuto" : " minutos");
        }
        if (minutes > 0 && seconds > 0) {
            formattedTime.append(" e ");
        }
        if (seconds > 0) {
            formattedTime.append(seconds).append(seconds == 1 ? " segundo" : " segundos");
        }
        if (formattedTime.isEmpty() && remainingTime.toMillis() > 0) {
            formattedTime.append("menos de 1 segundo");
        }

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Sessão de Votação em Andamento");
        problemDetail.setDetail("A votação ainda não foi encerrada. Tente novamente mais tarde.");

        problemDetail.setProperty("tempoRestante", formattedTime.toString());
        problemDetail.setProperty("horarioEncerramento", ex.getEndTime());

        return problemDetail;
    }

    /**
     * Handler que traduz violações de constraint do banco de dados em respostas de erro HTTP claras.
     * Especificamente, ele identifica quando um associado tenta votar duas vezes.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Throwable rootCause = ex.getMostSpecificCause();

        if (rootCause.getMessage() != null &&
                rootCause.getLocalizedMessage().contains("uk_vote_session_associate")) {

            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                    HttpStatus.CONFLICT,
                    "Este associado já votou nesta pauta. O voto não foi computado novamente.");
            problemDetail.setTitle("Voto Duplicado");
            problemDetail.setProperty("timestamp", Instant.now());

            return problemDetail;
        }

        log.error("Ocorreu uma violação de integridade de dados não esperada.", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno ao processar a requisição. Verifique os dados enviados.");
        problemDetail.setTitle("Erro de Integridade de Dados");
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
