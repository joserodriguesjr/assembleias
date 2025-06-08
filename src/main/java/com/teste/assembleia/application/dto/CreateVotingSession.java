package com.teste.assembleia.application.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateVotingSession {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}
