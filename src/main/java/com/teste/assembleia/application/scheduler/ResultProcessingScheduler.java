package com.teste.assembleia.application.scheduler;

import com.teste.assembleia.application.service.VotingService;
import com.teste.assembleia.domain.entity.VotingSession;
import com.teste.assembleia.domain.repository.VotingSessionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Component
@Slf4j
@AllArgsConstructor
public class ResultProcessingScheduler {

    private final VotingSessionRepository votingSessionRepository;
    private final VotingService votingService;

    // fixedRate = 60000ms (1 min)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void processVotingResults() {
        List<VotingSession> sessionsToProcess = votingSessionRepository
                .findAllByEndTimeBeforeAndProcessedIsFalse(LocalDateTime.now());

        if (sessionsToProcess.isEmpty()) {
            return;
        }

        for (VotingSession session : sessionsToProcess) {
            try {
                votingService.processResults(session);
            } catch (Exception e) {
                log.error("Erro ao processar sessão ID: {}", session.getId(), e);
            }
        }
    }
}
