package com.teste.assembleia.domain.exception;

import lombok.Getter;


@Getter
public class VotingSessionAlreadyExistsException extends RuntimeException {

    private final Long agendaId;

    public VotingSessionAlreadyExistsException(Long agendaId) {
        super("Tentativa de criar sessão já existente.");
        this.agendaId = agendaId;
    }
}
