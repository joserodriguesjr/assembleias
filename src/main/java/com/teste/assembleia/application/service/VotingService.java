package com.teste.assembleia.application.service;

import com.teste.assembleia.application.dto.CreateVoteRequest;
import com.teste.assembleia.application.dto.CreateVotingSession;
import com.teste.assembleia.domain.entity.Agenda;
import com.teste.assembleia.domain.entity.Vote;
import com.teste.assembleia.domain.entity.VotingSession;
import com.teste.assembleia.domain.exception.*;
import com.teste.assembleia.domain.repository.VoteRepository;
import com.teste.assembleia.domain.repository.VotingSessionRepository;
import com.teste.assembleia.domain.valueObject.VoteChoice;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@AllArgsConstructor
@Slf4j
public class VotingService {

    private final VotingSessionRepository votingSessionRepository;
    private final VoteRepository voteRepository;
    private final AgendaService agendaService;

    public VotingSession openSession(Long agendaId, CreateVotingSession createVotingSession) {
        if (votingSessionRepository.findByAgendaId(agendaId).isPresent()) {
            log.warn("Falha ao abrir sessão para pauta ID: {}. Sessão já existente.", agendaId);
            throw new VotingSessionAlreadyExistsException(agendaId);
        }

        Agenda agenda = agendaService.findById(agendaId);

        VotingSession session;
        LocalDateTime startTime = LocalDateTime.now();

        if (createVotingSession != null && createVotingSession.getEndTime() != null) {
            session = agenda.openVotingSession(startTime, createVotingSession.getEndTime());
        } else {
            session = agenda.openVotingSession(startTime);
        }

        return votingSessionRepository.save(session);
    }

    public Vote submitVote(Long agendaId, CreateVoteRequest createVoteRequest) {
        VotingSession session = findByAgendaId(agendaId);

        Vote vote = session.receiveVote(createVoteRequest.getAssociateId(), createVoteRequest.getChoice());

        try {
            return voteRepository.save(vote);
        } catch (DataIntegrityViolationException e) {
            log.warn("Falha ao registrar voto para associado ID {}. Associado já votou.", createVoteRequest.getAssociateId());
            throw new AssociateAlreadyVotedException(createVoteRequest.getAssociateId());
        }
    }

    public VotingSession getResults(Long agendaId) {
        VotingSession session = findByAgendaId(agendaId);

        if (LocalDateTime.now().isBefore(session.getEndTime())) {
            log.warn("Falha ao coletar resultados de sessão com pauta ID: {}. Sessão ainda está aberta.", agendaId);
            throw new VotingSessionStillRunningException(session.getEndTime());
        }

//      Caso scheduler ainda não executou, processa os votos
        if (session.getProcessed().equals(false)) {
            processResults(session);
        }

        return session;
    }

    public void processResults(VotingSession session) {
        if (session.getProcessed().equals(true)) {
            return;
        }

        if (LocalDateTime.now().isBefore(session.getEndTime())) {
            log.warn("Falha ao coletar resultados de sessão com pauta ID: {}. Sessão ainda está aberta.", session.getAgenda().getId());
            throw new VotingSessionStillRunningException(session.getEndTime());
        }

        Long yesVotes = voteRepository.countByVotingSessionIdAndChoice(session.getId(), VoteChoice.SIM);
        Long noVotes = voteRepository.countByVotingSessionIdAndChoice(session.getId(), VoteChoice.NAO);
        session.processResults(yesVotes, noVotes);

        votingSessionRepository.save(session);
    }

    private VotingSession findByAgendaId(Long agendaId) {
        return votingSessionRepository.findByAgendaId(agendaId).orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada para pauta com ID: " + agendaId));
    }
}
