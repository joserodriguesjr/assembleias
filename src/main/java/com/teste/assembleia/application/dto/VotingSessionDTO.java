package com.teste.assembleia.application.dto;

import com.teste.assembleia.domain.entity.VotingSession;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VotingSessionDTO {

    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long yesVotes;
    private Long noVotes;
    private Boolean processed;

    public VotingSessionDTO(VotingSession votingSession) {
        this.id = votingSession.getId();
        this.startTime = votingSession.getStartTime();
        this.endTime = votingSession.getEndTime();
        this.yesVotes = votingSession.getYesVotes();
        this.noVotes = votingSession.getNoVotes();
        this.processed = votingSession.getProcessed();
    }
}

