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
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
@AllArgsConstructor
public class VotingService {

    private final VotingSessionRepository votingSessionRepository;
    private final VoteRepository voteRepository;

    private final AgendaService agendaService;

    public VotingSession openSession(Long agendaId, CreateVotingSession createVotingSession) {
        Optional<VotingSession> sessionOpt = votingSessionRepository.findByAgendaId(agendaId);
        if (sessionOpt.isPresent()) {
            throw new VotingSessionAlreadyExistsException("Já existe uma sessão de votação para essa pauta.");
        }

        Agenda agenda = agendaService.findById(agendaId);

//        todo: check logic to:
//         add 1 min by default
//         accept endTime and elapsedSeconds, minutes

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now;
        LocalDateTime endTime;

        if (createVotingSession == null) {
            endTime = now.plusMinutes(1);
        } else {
            startTime = createVotingSession.getStartTime() != null ? createVotingSession.getStartTime() : now;
            endTime = createVotingSession.getEndTime() != null ? createVotingSession.getEndTime() : startTime.plusMinutes(1);
        }

        validateSessionTimes(now, startTime, endTime);

        VotingSession session = new VotingSession();
        session.setAgenda(agenda);
        session.setStartTime(startTime);
        session.setEndTime(endTime);

        return votingSessionRepository.save(session);
    }

    public Vote submiteVote(Long agendaId, CreateVoteRequest createVoteRequest) {
        VotingSession session = findByAgendaId(agendaId);

        if (LocalDateTime.now().isAfter(session.getEndTime())) {
            throw new VotingSessionEndedException("A sessão de votação já foi encerrada.");
        }

//        todo: check:
//        has user voted?

        LocalDateTime now = LocalDateTime.now();

        Vote vote = new Vote();
        vote.setVotingSession(session);
        vote.setAssociateId(createVoteRequest.getAssociateId());
        vote.setChoice(createVoteRequest.getChoice());
        vote.setTimestamp(now);

        return voteRepository.save(vote);
    }

    public VotingSession getResults(Long agendaId) {
        VotingSession session = findByAgendaId(agendaId);

        if (LocalDateTime.now().isBefore(session.getEndTime())) {
            throw new VotingSessionStillRunningException(session.getEndTime());
        }

        if (session.getProcessed().equals(false)) {
            Long yesVotes = voteRepository.countByVotingSessionIdAndChoice(session.getId(), VoteChoice.SIM);
            Long noVotes = voteRepository.countByVotingSessionIdAndChoice(session.getId(), VoteChoice.NAO);

            session.setYesVotes(yesVotes);
            session.setNoVotes(noVotes);
            session.setProcessed(true);
            votingSessionRepository.save(session);
        }

        return session;
    }

    private VotingSession findByAgendaId(Long agendaId) {
        return votingSessionRepository.findByAgendaId(agendaId).orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada para pauta com ID: " + agendaId));
    }

    private void validateSessionTimes(LocalDateTime now, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isBefore(now)) {
            throw new BusinessException("A data de início não pode estar no passado");
        }

        if (endTime.isBefore(startTime)) {
            throw new BusinessException("A data de término deve ser posterior à de início");
        }

        Duration duration = Duration.between(startTime, endTime);
        if (duration.toMinutes() < 1) {
            throw new BusinessException("A sessão deve durar no mínimo 1 minuto");
        }

        if (duration.toHours() > 1) {
            throw new BusinessException("A sessão deve durar no máximo 1 hora");
        }
    }
}
