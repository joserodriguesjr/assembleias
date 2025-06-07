package com.teste.assembleia.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class CreateVotingSessionDTO {

    @NotNull(message = "A data de início é obrigatória")
    private LocalDateTime startTime;

    @NotNull(message = "A data de término é obrigatória")
    private LocalDateTime endTime;

//    Além do @NotNull, você pode aplicar regras adicionais no Service como:
//    startTime não pode ser no passado
//    endTime deve ser depois de startTime
//    duração mínima ou máxima (ex: entre 1 minuto e 1 hora)
}
