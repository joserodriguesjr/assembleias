package com.teste.assembleia.domain.entity;

import com.teste.assembleia.domain.exception.VotingSessionTimeViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test - Entity - Agenda")
class AgendaTest {

    private Agenda agenda;

    @BeforeEach
    void setUp() {
        agenda = new Agenda();
        agenda.setId(1L);
        agenda.setName("Pauta de Teste");
    }

    @Test
    @DisplayName("Deve abrir uma sessão de votação com duração padrão de 1 minuto")
    void openVotingSession_shouldOpenSessionWithStandardDurationOf1Minute() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now();

        // Act
        VotingSession session = agenda.openVotingSession(startTime);

        // Assert
        assertNotNull(session, "A sessão não deveria ser nula.");
        assertEquals(agenda, session.getAgenda(), "A sessão deve pertencer à pauta correta.");
        assertEquals(startTime, session.getStartTime(), "A hora de início deve ser a informada.");
        assertEquals(startTime.plusMinutes(1), session.getEndTime(), "A hora de fim deve ser exatamente 1 minuto após o início.");
    }

    @Test
    @DisplayName("Deve abrir uma sessão de votação com duração customizada válida")
    void openVotingSession_shouldOpenSessionWithCustomDuration() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(30);

        // Act
        VotingSession session = agenda.openVotingSession(startTime, endTime);

        // Assert
        assertNotNull(session);
        assertEquals(endTime, session.getEndTime(), "A hora de fim deve ser a customizada que foi informada.");
    }

    @Test
    @DisplayName("NÃO deve abrir sessão com duração menor que 1 minuto")
    void openVotingSession_shouldNotOpenSessionWithDurationLessThan1Minute() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusSeconds(59);

        // Act & Assert
        // Verifica se a exceção correta é lançada E se a mensagem está correta.
        VotingSessionTimeViolationException exception = assertThrows(VotingSessionTimeViolationException.class, () -> {
            agenda.openVotingSession(startTime, endTime);
        });

        assertEquals("A sessão de votação deve durar no mínimo 1 minuto.", exception.getMessage());
    }

    @Test
    @DisplayName("NÃO deve abrir sessão com data de início no passado")
    void openVotingSession_shouldNotOpenSessionWithPastStartTime() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(5);

        // Act & Assert
        VotingSessionTimeViolationException exception = assertThrows(VotingSessionTimeViolationException.class, () -> {
            agenda.openVotingSession(startTime);
        });

        assertEquals("A data de início não pode estar no passado.", exception.getMessage());
    }

    @Test
    @DisplayName("NÃO deve abrir sessão com data de fim anterior à data de início")
    void openVotingSession_shouldNotOpenSessionWithEndTimeBeforeStartTime() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.minusMinutes(10);

        // Act & Assert
        VotingSessionTimeViolationException exception = assertThrows(VotingSessionTimeViolationException.class, () -> {
            agenda.openVotingSession(startTime, endTime);
        });

        assertEquals("A data de término deve ser posterior à de início.", exception.getMessage());
    }
}