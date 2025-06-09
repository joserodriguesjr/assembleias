package com.teste.assembleia.domain.repository;

import com.teste.assembleia.domain.entity.Vote;
import com.teste.assembleia.domain.valueObject.VoteChoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    long countByVotingSessionIdAndChoice(Long votingSessionId, VoteChoice choice);

}
