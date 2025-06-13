package com.teste.assembleia.infrastructure.web.exception;

import com.teste.assembleia.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


@RestControllerAdvice
@Order(0)
@Slf4j
public class VotingExceptionHandler {

    @ExceptionHandler(VotingSessionAlreadyExistsException.class)
    public ProblemDetail handleSessionAlreadyExists(VotingSessionAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Sessão de Votação Existente");
        problemDetail.setDetail("Já existe uma sessão de votação para pauta com ID: " + ex.getAgendaId().toString());
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(VotingSessionEndedException.class)
    public ProblemDetail handleSessionEnded(VotingSessionEndedException ex) {
        LocalDateTime endTime = ex.getEndTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "dd/MM/yyyy 'às' HH:mm:ss",
                Locale.of("pt", "BR")
        );
        String formattedEndTime = endTime.format(formatter);
        String detailMessage = "A votação não pôde ser computada pois a sessão foi encerrada em " + formattedEndTime + ".";

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Sessão de Votação Encerrada");
        problemDetail.setDetail(detailMessage);
        problemDetail.setProperty("endedAt", endTime.toString());
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(VotingSessionStillRunningException.class)
    public ProblemDetail handleSessionStillRunning(VotingSessionStillRunningException ex) {
        String formattedTime = formatRemainingTime(ex.getEndTime());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Sessão de Votação em Andamento");
        problemDetail.setDetail("A votação ainda não foi encerrada. Tente novamente mais tarde.");
        problemDetail.setProperty("tempoRestante", formattedTime);
        problemDetail.setProperty("horarioEncerramento", ex.getEndTime());
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(VotingSessionTimeViolationException.class)
    public ProblemDetail handleVotingSessionTimeViolation(VotingSessionTimeViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Sessão de Votação Não Pode Ser Iniciada");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(AssociateAlreadyVotedException.class)
    public ProblemDetail handleAssociateAlreadyVoted(AssociateAlreadyVotedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Voto Duplicado");
        problemDetail.setDetail("O associado (" + ex.getMessage() + ") já votou nesta pauta.");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    private String formatRemainingTime(LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration remainingTime = Duration.between(now, endTime);

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

        return formattedTime.toString();
    }
}