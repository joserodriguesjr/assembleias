package com.teste.assembleia.repository;

import com.teste.assembleia.model.Vote;
import com.teste.assembleia.model.VoteChoice;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VoteRepository extends JpaRepository<Vote, Long> {

    long countByVotingSessionIdAndChoice(Long votingSessionId, VoteChoice choice);
}
