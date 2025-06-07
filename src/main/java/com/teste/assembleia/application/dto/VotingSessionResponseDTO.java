package com.teste.assembleia.application.dto;

import com.teste.assembleia.domain.entity.VotingSession;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class VotingSessionResponseDTO {

    private Long id;
    private Long agendaId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Long yesVotes;
    private Long noVotes;

    public VotingSessionResponseDTO(VotingSession session) {
        this.id = session.getId();
        this.agendaId = session.getAgenda().getId();
        this.startTime = session.getStartTime();
        this.endTime = session.getEndTime();
    }

    public VotingSessionResponseDTO(VotingSession session, long yesVotes, long noVotes) {
        this(session);
        this.yesVotes = yesVotes;
        this.noVotes = noVotes;
    }

//    tempo restante até o fim
//    número de votos
//    resultado parcial da votação (caso esteja aplicando isso)
}
