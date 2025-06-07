package com.teste.assembleia.application.service;

import com.teste.assembleia.domain.entity.Agenda;
import com.teste.assembleia.domain.repository.AgendaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;

    public Agenda create(Agenda agenda) {
        return agendaRepository.save(agenda);
    }

    public Optional<Agenda> findById(Long agendaId) {
        return agendaRepository.findById(agendaId);
    }

}
