package com.teste.assembleia.application.scheduler;

import com.teste.assembleia.application.service.VotingService;
import com.teste.assembleia.domain.entity.VotingSession;
import com.teste.assembleia.domain.repository.VotingSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test - Scheduler - ResultProcessingScheduler")
class ResultProcessingSchedulerTest {

    @Mock
    private VotingSessionRepository votingSessionRepository;

    @Mock
    private VotingService votingService;

    @InjectMocks
    private ResultProcessingScheduler scheduler;

    @Test
    @DisplayName("Deve processar sessoes encerradas e nao processadas")
    void shouldProcessClosedSessions() {
        // Arrange
        VotingSession session1 = new VotingSession();
        session1.setId(1L);
        VotingSession session2 = new VotingSession();
        session2.setId(2L);
        List<VotingSession> sessionsToProcess = List.of(session1, session2);

        when(votingSessionRepository.findAllByEndTimeBeforeAndProcessedIsFalse(any(LocalDateTime.class)))
                .thenReturn(sessionsToProcess);

        // Act
        scheduler.processVotingResults();

        // Assert
        verify(votingService, times(2)).processResults(any(VotingSession.class));
        verify(votingService).processResults(session1);
        verify(votingService).processResults(session2);
    }

    @Test
    @DisplayName("Nao deve fazer nada quando nao houver sessoes para processar")
    void shouldDoNothingWhenTheresNoSessions() {
        // Arrange
        when(votingSessionRepository.findAllByEndTimeBeforeAndProcessedIsFalse(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        scheduler.processVotingResults();

        // Assert
        verify(votingService, never()).processResults(any(VotingSession.class));
    }

    @Test
    @DisplayName("Deve continuar processando outras sessoes mesmo que uma falhe")
    void shouldContinueProcessingEvenWithFailure() {
        // Arrange
        VotingSession session1 = new VotingSession();
        session1.setId(1L);
        VotingSession session2_fails = new VotingSession(); // Esta sessão causará um erro
        session2_fails.setId(2L);
        VotingSession session3 = new VotingSession();
        session3.setId(3L);
        List<VotingSession> sessionsToProcess = List.of(session1, session2_fails, session3);

        when(votingSessionRepository.findAllByEndTimeBeforeAndProcessedIsFalse(any(LocalDateTime.class)))
                .thenReturn(sessionsToProcess);

        doThrow(new RuntimeException("Erro simulado no serviço"))
                .when(votingService).processResults(session2_fails);

        // Act
        scheduler.processVotingResults();

        // Assert
        verify(votingService).processResults(session1);
        verify(votingService).processResults(session3);
        verify(votingService).processResults(session2_fails);
    }
}