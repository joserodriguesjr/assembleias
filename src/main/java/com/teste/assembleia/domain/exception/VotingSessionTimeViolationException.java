package com.teste.assembleia.domain.exception;

public class VotingSessionTimeViolationException extends RuntimeException {
    public VotingSessionTimeViolationException(String message) {
        super(message);
    }
}
