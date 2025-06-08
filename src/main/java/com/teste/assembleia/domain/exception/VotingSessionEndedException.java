package com.teste.assembleia.domain.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class VotingSessionEndedException extends RuntimeException {

    private final LocalDateTime endTime;

    public VotingSessionEndedException(LocalDateTime endTime) {
        super("Tentativa de ação em sessão já encerrada.");
        this.endTime = endTime;
    }

}