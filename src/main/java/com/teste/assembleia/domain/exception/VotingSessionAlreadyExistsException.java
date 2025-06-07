package com.teste.assembleia.domain.exception;

public class VotingSessionAlreadyExistsException extends RuntimeException {
    public VotingSessionAlreadyExistsException(String message) {
        super(message);
    }
}
