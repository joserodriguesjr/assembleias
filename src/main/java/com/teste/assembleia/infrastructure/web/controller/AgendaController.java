package com.teste.assembleia.infrastructure.web.controller;

import com.teste.assembleia.application.dto.AgendaDetailsDTO;
import com.teste.assembleia.application.dto.CreateAgenda;
import com.teste.assembleia.application.dto.CreateVoteRequest;
import com.teste.assembleia.application.dto.CreateVotingSession;
import com.teste.assembleia.application.service.AgendaService;
import com.teste.assembleia.application.service.VotingService;
import com.teste.assembleia.domain.entity.Agenda;
import com.teste.assembleia.domain.entity.Vote;
import com.teste.assembleia.domain.entity.VotingSession;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/agendas")
@Tag(name = "Pautas", description = "Endpoints para gerenciar o processo de votação em uma pauta")
@AllArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;
    private final VotingService votingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra uma nova pauta", description = "Informe o nome da pauta a ser registrada.")
    public Agenda createAgenda(@RequestBody @Valid CreateAgenda createAgenda) {
        return agendaService.create(createAgenda.getName());
    }

    @GetMapping("/{agendaId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Encontra pauta pelo ID", description = "Retorna pauta com ID informado.")
    public AgendaDetailsDTO getAgendaById(@PathVariable Long agendaId) {
        return agendaService.getAgendaWithSessionDetails(agendaId);
    }

    @PostMapping("/{agendaId}/session")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Abre sessão de votação", description = "Abre uma sessão de votação para a pauta pela duração especificada. Padrão é 1 minuto.")
    public VotingSession openSession(
            @PathVariable Long agendaId,
            @RequestBody(required = false) CreateVotingSession createVotingSession) {
        return votingService.openSession(agendaId, createVotingSession);
    }

    @PostMapping("/{agendaId}/votes")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Registra o voto de um associado", description = "Submete um voto (SIM/NAO) para uma pauta com sessão aberta. Cada associado só pode votar uma vez por pauta.")
    public Vote submitVote(
            @PathVariable Long agendaId,
            @RequestBody CreateVoteRequest createVoteRequest) {
        return votingService.submitVote(agendaId, createVoteRequest);
    }

    @GetMapping("/{agendaId}/results")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Carrega resultados da votação", description = "Carrega os resultados da sessão de votação. Só será carregado após o término da sessão.")
    public VotingSession getResults(@PathVariable Long agendaId) {
        return votingService.getResults(agendaId);
    }

}
