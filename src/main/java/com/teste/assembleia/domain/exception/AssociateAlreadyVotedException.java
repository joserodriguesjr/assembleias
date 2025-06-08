package com.teste.assembleia.domain.exception;

public class AssociateAlreadyVotedException extends RuntimeException {
    public AssociateAlreadyVotedException(String message) {
        super(message);
    }
}
