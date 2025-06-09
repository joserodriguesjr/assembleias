package com.teste.assembleia.application.dto;

import com.teste.assembleia.domain.entity.Vote;
import com.teste.assembleia.domain.valueObject.VoteChoice;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VoteDTO {

    private Long id;
    private Long votingSessionId;
    private String associateId;
    private VoteChoice choice;
    private LocalDateTime timestamp;

    public VoteDTO(Vote vote) {
        this.id = vote.getId();
        this.votingSessionId = vote.getVotingSession().getId();
        this.associateId = vote.getAssociateId();
        this.choice = vote.getChoice();
        this.timestamp = vote.getTimestamp();
    }
}

