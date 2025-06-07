package com.teste.assembleia.domain.exception;

public class VotingSessionEndedException extends RuntimeException {
    public VotingSessionEndedException(String message) {
        super(message);
    }
}
