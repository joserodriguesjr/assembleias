package com.teste.assembleia.application.service;

import com.teste.assembleia.domain.entity.Agenda;
import com.teste.assembleia.domain.exception.ResourceNotFoundException;
import com.teste.assembleia.domain.repository.AgendaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;

    public Agenda create(Agenda agenda) {
        return agendaRepository.save(agenda);
    }

    public Agenda findById(Long agendaId) {
        return agendaRepository.findById(agendaId).orElseThrow(() -> new ResourceNotFoundException("Agenda não encontrada com ID: " + agendaId));
    }

}
