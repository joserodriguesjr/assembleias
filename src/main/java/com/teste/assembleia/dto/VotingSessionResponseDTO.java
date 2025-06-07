package com.teste.assembleia.dto;

import com.teste.assembleia.model.VotingSession;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class VotingSessionResponseDTO {

    private Long id;
    private Long agendaId;
    //    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public VotingSessionResponseDTO(VotingSession session) {
        this.id = session.getId();
        this.agendaId = session.getAgenda().getId();
//        boolean isOpen = session.getIsOpen();
//        this.status = isOpen ? "OPEN" : "CLOSED"; // ex: "OPEN", "CLOSED"
        this.startTime = session.getStartTime();
        this.endTime = session.getEndTime();
    }

//    tempo restante até o fim
//    número de votos
//    resultado parcial da votação (caso esteja aplicando isso)
}
