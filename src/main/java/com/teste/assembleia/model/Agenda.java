package com.teste.assembleia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "agendas")
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @NotNull(message = "is required")
    private String name;

//    @Column(name = "open")
//    @NotNull(message = "is required")
//    private Boolean open;

//    @Column(name = "remaining_time")
//    @NotNull(message = "is required")
//    private Integer remainingTime;
}
