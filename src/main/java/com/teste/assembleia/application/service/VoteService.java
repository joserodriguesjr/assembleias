package com.teste.assembleia.application.service;

import com.teste.assembleia.application.dto.CreateVoteRequest;
import com.teste.assembleia.domain.exception.NotFoundException;
import com.teste.assembleia.domain.entity.Vote;
import com.teste.assembleia.domain.entity.VotingSession;
import com.teste.assembleia.domain.repository.VoteRepository;
import com.teste.assembleia.domain.repository.VotingSessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VotingSessionRepository votingSessionRepository;

    public Vote newVote(Long sessionId, CreateVoteRequest createVoteRequest) {
        VotingSession session = votingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Sessão não encontrada"));

        LocalDateTime now = LocalDateTime.now();

        Vote vote = new Vote();
        vote.setVotingSession(session);
        vote.setAssociateId(createVoteRequest.getAssociateId());
        vote.setChoice(createVoteRequest.getChoice());
        vote.setTimestamp(now);
        return voteRepository.save(vote);
    }
}
