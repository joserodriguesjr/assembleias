package com.teste.assembleia.application.dto;

import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class CreateVotingSessionDTO {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}
