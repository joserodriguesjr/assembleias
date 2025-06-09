package com.teste.assembleia.domain.entity;

import com.teste.assembleia.domain.exception.VotingSessionTimeViolationException;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Data
@Table
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agenda_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private static final long DEFAULT_SESSION_DURATION_MINUTES = 1;

    public VotingSession openVotingSession(LocalDateTime startTime, LocalDateTime endTime) {
        validateSessionTimes(startTime, endTime);

        VotingSession session = new VotingSession();
        session.setAgenda(this);
        session.setStartTime(startTime);
        session.setEndTime(endTime);

        return session;
    }

    public VotingSession openVotingSession(LocalDateTime startTime) {
        LocalDateTime endTime = startTime.plusMinutes(DEFAULT_SESSION_DURATION_MINUTES);

        return this.openVotingSession(startTime, endTime);
    }

    private void validateSessionTimes(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        // Adiciona uma pequena tolerância para data de início
        if (startTime.isBefore(now.minusSeconds(30))) {
            throw new VotingSessionTimeViolationException("A data de início não pode estar no passado.");
        }

        if (endTime.isBefore(startTime)) {
            throw new VotingSessionTimeViolationException("A data de término deve ser posterior à de início.");
        }

        Duration duration = Duration.between(startTime, endTime);
        if (duration.toMinutes() < 1) {
            throw new VotingSessionTimeViolationException("A sessão de votação deve durar no mínimo 1 minuto.");
        }
    }
}
