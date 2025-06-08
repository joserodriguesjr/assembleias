package com.teste.assembleia.application.dto;

import com.teste.assembleia.domain.entity.Agenda;
import com.teste.assembleia.domain.entity.VotingSession;
import lombok.Data;

@Data
public class AgendaDetailsDTO {

    private Long agendaId;
    private String name;
    private VotingSession session;

    public AgendaDetailsDTO(Agenda agenda, VotingSession votingSession) {
        this.agendaId = agenda.getId();
        this.name = agenda.getName();
        this.session = votingSession;
    }
}