package com.teste.assembleia.application.service;

import com.teste.assembleia.application.dto.AgendaDetailsDTO;
import com.teste.assembleia.domain.entity.Agenda;
import com.teste.assembleia.domain.entity.VotingSession;
import com.teste.assembleia.domain.exception.ResourceNotFoundException;
import com.teste.assembleia.domain.repository.AgendaRepository;
import com.teste.assembleia.domain.repository.VotingSessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final VotingSessionRepository votingSessionRepository;

    public Agenda create(String name) {
        Agenda agenda = new Agenda();
        agenda.setName(name);
        return agendaRepository.save(agenda);
    }

    public Agenda findById(Long agendaId) {
        return agendaRepository.findById(agendaId).orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada com ID: " + agendaId));
    }

    public AgendaDetailsDTO getAgendaWithSessionDetails(Long agendaId) {
        Agenda agenda = findById(agendaId);
        Optional<VotingSession> optionalSession = votingSessionRepository.findByAgendaId(agendaId);

        return new AgendaDetailsDTO(agenda, optionalSession.orElse(null));
    }

}
