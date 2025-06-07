package com.teste.assembleia.domain.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class VotingSessionStillRunningException extends RuntimeException {

    private final LocalDateTime endTime;

    public VotingSessionStillRunningException(LocalDateTime endTime) {
        super("A sessão de votação ainda não foi encerrada.");
        this.endTime = endTime;
    }

}