package com.teste.assembleia.service;

import com.teste.assembleia.dto.CreateVoteRequest;
import com.teste.assembleia.exception.NotFoundException;
import com.teste.assembleia.model.Vote;
import com.teste.assembleia.model.VotingSession;
import com.teste.assembleia.repository.VoteRepository;
import com.teste.assembleia.repository.VotingSessionRepository;
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
