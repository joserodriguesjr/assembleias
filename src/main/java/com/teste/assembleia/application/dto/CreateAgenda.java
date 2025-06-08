package com.teste.assembleia.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAgenda {
    @NotBlank
    private String name;
}
