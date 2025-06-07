package com.teste.assembleia.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agenda_id")
    private Long id;

    @Column(nullable = false)
    private String name;
}
