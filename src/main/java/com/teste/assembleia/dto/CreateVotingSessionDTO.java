package com.teste.assembleia.dto;

import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class CreateVotingSessionDTO {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}
