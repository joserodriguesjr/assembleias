package com.teste.assembleia.domain.repository;

import com.teste.assembleia.domain.entity.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
}
