package com.teste.assembleia.application.service;

import com.teste.assembleia.application.dto.CreateVoteRequest;
import com.teste.assembleia.application.dto.CreateVotingSession;
import com.teste.assembleia.domain.entity.Agenda;
import com.teste.assembleia.domain.entity.Vote;
import com.teste.assembleia.domain.entity.VotingSession;
import com.teste.assembleia.domain.exception.AssociateAlreadyVotedException;
import com.teste.assembleia.domain.exception.ResourceNotFoundException;
import com.teste.assembleia.domain.exception.VotingSessionAlreadyExistsException;
import com.teste.assembleia.domain.exception.VotingSessionStillRunningException;
import com.teste.assembleia.domain.repository.VoteRepository;
import com.teste.assembleia.domain.repository.VotingSessionRepository;
import com.teste.assembleia.domain.valueObject.VoteChoice;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@AllArgsConstructor
public class VotingService {

    private final VotingSessionRepository votingSessionRepository;
    private final VoteRepository voteRepository;
    private final AgendaService agendaService;

    public VotingSession openSession(Long agendaId, CreateVotingSession createVotingSession) {
        if (votingSessionRepository.findByAgendaId(agendaId).isPresent()) {
            throw new VotingSessionAlreadyExistsException("Já existe uma sessão de votação para essa pauta.");
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

        if (voteRepository.findByVotingSessionIdAndAssociateId(session.getId(), createVoteRequest.getAssociateId()).isPresent()) {
            throw new AssociateAlreadyVotedException(createVoteRequest.getAssociateId());
        }

        Vote vote = session.receiveVote(createVoteRequest.getAssociateId(), createVoteRequest.getChoice());

        return voteRepository.save(vote);
    }

    public VotingSession getResults(Long agendaId) {
        VotingSession session = findByAgendaId(agendaId);

        if (LocalDateTime.now().isBefore(session.getEndTime())) {
            throw new VotingSessionStillRunningException(session.getEndTime());
        }

        return session;
    }

    public void processResults(VotingSession session) {
        if (session.getProcessed().equals(true)) {
            return;
        }

        if (LocalDateTime.now().isBefore(session.getEndTime())) {
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
