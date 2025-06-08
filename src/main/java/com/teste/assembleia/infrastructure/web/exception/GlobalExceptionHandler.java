package com.teste.assembleia.infrastructure.web.exception;

import com.teste.assembleia.domain.exception.AssociateAlreadyVotedException;
import com.teste.assembleia.domain.exception.ResourceNotFoundException;
import com.teste.assembleia.domain.exception.VotingSessionStillRunningException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Recurso Não Encontrado");
        problemDetail.setDetail(ex.getMessage());
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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Throwable rootCause = ex.getCause();

        if (rootCause instanceof ConstraintViolationException &&
                Objects.equals(((ConstraintViolationException) rootCause).getConstraintName(), "uk_vote_session_associate")) {
            AssociateAlreadyVotedException associateAlreadyVotedException = new AssociateAlreadyVotedException("Este associado já votou nesta pauta. O voto não foi computado novamente.");
            return handleAssociateAlreadyVoted(associateAlreadyVotedException);
        }

        log.error("Ocorreu uma violação de integridade de dados não esperada.", ex);
        return handleGenericException(ex);
    }

    @ExceptionHandler(AssociateAlreadyVotedException.class)
    public ProblemDetail handleAssociateAlreadyVoted(AssociateAlreadyVotedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Voto Duplicado");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("timestamp", Instant.now());

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
