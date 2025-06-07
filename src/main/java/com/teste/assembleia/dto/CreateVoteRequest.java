package com.teste.assembleia.dto;

import com.teste.assembleia.model.VoteChoice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateVoteRequest {

    @NotBlank
    private String associateId;

    @NotNull
    private VoteChoice choice;

    // Getters e Setters
}