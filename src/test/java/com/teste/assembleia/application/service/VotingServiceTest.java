package com.teste.assembleia.application.service;

import com.teste.assembleia.application.dto.CreateVoteRequest;
import com.teste.assembleia.domain.entity.Agenda;
import com.teste.assembleia.domain.entity.Vote;
import com.teste.assembleia.domain.entity.VotingSession;
import com.teste.assembleia.domain.exception.VotingSessionAlreadyExistsException;
import com.teste.assembleia.domain.exception.VotingSessionStillRunningException;
import com.teste.assembleia.domain.repository.VoteRepository;
import com.teste.assembleia.domain.repository.VotingSessionRepository;
import com.teste.assembleia.domain.valueObject.VoteChoice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test - Service - VotingService")
class VotingServiceTest {

    @Mock
    private VotingSessionRepository votingSessionRepository;
    @Mock
    private VoteRepository voteRepository;
    @Mock
    private AgendaService agendaService;

    @InjectMocks
    private VotingService votingService;

    @Test
    @DisplayName("Deve abrir a sessão com duração padrão quando o fim de endTime não for fornecido")
    void openSession_shouldCreateSessionWithDefaultDuration() {
        // Arrange
        Long agendaId = 1L;
        Agenda mockAgenda = mock(Agenda.class);
        VotingSession mockSession = new VotingSession();

        when(votingSessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.empty());
        when(agendaService.findById(agendaId)).thenReturn(mockAgenda);
        when(mockAgenda.openVotingSession(any(LocalDateTime.class))).thenReturn(mockSession);
        when(votingSessionRepository.save(mockSession)).thenReturn(mockSession);

        // Act
        VotingSession createdSession = votingService.openSession(agendaId, null);

        // Assert
        assertNotNull(createdSession);
        verify(mockAgenda).openVotingSession(any(LocalDateTime.class));
        verify(mockAgenda, never()).openVotingSession(any(LocalDateTime.class), any(LocalDateTime.class));
        verify(votingSessionRepository).save(mockSession);
    }

    @Test
    @DisplayName("Deve jogar VotingSessionAlreadyExistsException quando sessão já existe")
    void openSession_shouldThrowException_whenSessionExists() {
        // Arrange
        Long agendaId = 1L;
        when(votingSessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(new VotingSession()));

        // Act & Assert
        assertThrows(VotingSessionAlreadyExistsException.class, () -> {
            votingService.openSession(agendaId, null);
        });
        verify(agendaService, never()).findById(anyLong());
        verify(votingSessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve salvar voto com sucesso para uma sessão aberta")
    void submitVote_shouldSucceed_whenSessionIsOpen() {
        // Arrange
        Long agendaId = 1L;
        VotingSession mockSession = mock(VotingSession.class);
        Vote mockVote = new Vote();
        CreateVoteRequest voteRequest = new CreateVoteRequest();
        voteRequest.setAssociateId("associate-123");
        voteRequest.setChoice(VoteChoice.SIM);

        when(votingSessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(mockSession));
        when(mockSession.receiveVote(voteRequest.getAssociateId(), voteRequest.getChoice())).thenReturn(mockVote);
        when(voteRepository.save(mockVote)).thenReturn(mockVote);

        // Act
        Vote submittedVote = votingService.submitVote(agendaId, voteRequest);

        // Assert
        assertNotNull(submittedVote);
        verify(voteRepository).save(mockVote);
    }

    @Test
    @DisplayName("Deve retornar os resultados quando a sessão de votação terminar")
    void getResults_shouldReturnSession_whenEnded() {
        // Arrange
        Long agendaId = 1L;
        VotingSession endedSession = new VotingSession();
        endedSession.setEndTime(LocalDateTime.now().minusMinutes(1));

        when(votingSessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(endedSession));

        // Act
        VotingSession result = votingService.getResults(agendaId);

        // Assert
        assertNotNull(result);
        assertEquals(endedSession, result);
    }

    @Test
    @DisplayName("Deve jogar VotingSessionStillRunningException quando sessão ainda está aberta")
    void getResults_shouldThrowException_whenRunning() {
        // Arrange
        Long agendaId = 1L;
        VotingSession runningSession = new VotingSession();
        runningSession.setEndTime(LocalDateTime.now().plusHours(1));

        when(votingSessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(runningSession));

        // Act & Assert
        assertThrows(VotingSessionStillRunningException.class, () -> {
            votingService.getResults(agendaId);
        });
    }

    @Test
    @DisplayName("Deve processar os resultados para uma sessão encerrada e não processada")
    void processResults_shouldSucceed_forEndedUnprocessedSession() {
        // Arrange
        VotingSession sessionToProcess = new VotingSession();
        sessionToProcess.setId(1L);
        sessionToProcess.setProcessed(false);
        sessionToProcess.setEndTime(LocalDateTime.now().minusSeconds(10));

        when(voteRepository.countByVotingSessionIdAndChoice(1L, VoteChoice.SIM)).thenReturn(15L);
        when(voteRepository.countByVotingSessionIdAndChoice(1L, VoteChoice.NAO)).thenReturn(5L);

        // Act
        votingService.processResults(sessionToProcess);

        // Assert
        verify(voteRepository, times(2)).countByVotingSessionIdAndChoice(anyLong(), any(VoteChoice.class));
        verify(votingSessionRepository).save(sessionToProcess);
        assertTrue(sessionToProcess.getProcessed());
        assertEquals(15L, sessionToProcess.getYesVotes());
    }

    @Test
    @DisplayName("NÃO deve fazer nada quando a sessão já estiver processada")
    void processResults_shouldDoNothing_whenAlreadyProcessed() {
        // Arrange
        VotingSession processedSession = new VotingSession();
        processedSession.setProcessed(true);

        // Act
        votingService.processResults(processedSession);

        // Assert
        verify(voteRepository, never()).countByVotingSessionIdAndChoice(anyLong(), any(VoteChoice.class));
        verify(votingSessionRepository, never()).save(any(VotingSession.class));
    }
}