package com.teste.assembleia.application.dto;

import com.teste.assembleia.domain.valueObject.VoteChoice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateVoteRequest {

    @NotBlank
    private String associateId;

    @NotNull
    private VoteChoice choice;

}