package com.teste.assembleia.infrastructure.web.controller;

import com.teste.assembleia.application.dto.*;
import com.teste.assembleia.application.service.AgendaService;
import com.teste.assembleia.application.service.VotingService;
import com.teste.assembleia.domain.entity.Agenda;
import com.teste.assembleia.domain.entity.Vote;
import com.teste.assembleia.domain.entity.VotingSession;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/agendas")
@Tag(name = "Pautas", description = "Endpoints para gerenciar o processo de votação em uma pauta")
@AllArgsConstructor
@Slf4j
public class AgendaController {

    private final AgendaService agendaService;
    private final VotingService votingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar nova pauta", description = "Informe o nome da pauta a ser registrada.")
    public Agenda createAgenda(@RequestBody @Valid CreateAgenda createAgenda) {
        log.info("Recebida requisição POST para /v1/agendas com o nome: '{}'", createAgenda.getName());

        Agenda agenda = agendaService.create(createAgenda.getName());
        log.info("Pauta ID {} criada com sucesso.", agenda.getId());

        return agenda;
    }

    @GetMapping("/{agendaId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Encontrar pauta pelo ID", description = "Busca os detalhes de uma pauta e sua sessão.")
    public AgendaDetailsDTO getAgendaById(@PathVariable Long agendaId) {
        log.info("Recebida requisição GET para /v1/agendas/{}", agendaId);

        return agendaService.getAgendaWithSessionDetails(agendaId);
    }

    @PostMapping("/{agendaId}/session")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Abrir sessão de votação", description = "Abre uma nova sessão de votação para uma pauta pela duração especificada. Padrão é 1 minuto.")
    public VotingSessionDTO openSession(
            @PathVariable Long agendaId,
            @RequestBody(required = false) CreateVotingSession createVotingSession) {
        log.info("Recebida requisição POST para /v1/agendas/{}/session", agendaId);

        VotingSession session = votingService.openSession(agendaId, createVotingSession);
        log.info("Sessão ID {} criada com sucesso.", session.getId());

        return new VotingSessionDTO(session);
    }

    @PostMapping("/{agendaId}/votes")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Submeter voto", description = "Submete um voto (SIM/NAO) para uma pauta com sessão aberta. Cada associado só pode votar uma vez por " +
            "pauta.")
    public void submitVote(
            @PathVariable Long agendaId,
            @RequestBody CreateVoteRequest createVoteRequest) {
        log.info("Recebida requisição POST para /v1/agendas/{}/votes", agendaId);

        Vote vote = votingService.submitVote(agendaId, createVoteRequest);
        log.info("Voto ID {} registrado com sucesso.", vote.getId());
    }

    @GetMapping("/{agendaId}/results")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Obter resultados da votação", description = "Obtém os resultados da votação para uma pauta. Só será carregado após o término da sessão.")
    public VotingSessionDTO getResults(@PathVariable Long agendaId) {
        log.info("Recebida requisição GET para /v1/agendas/{}/results", agendaId);

        VotingSession session = votingService.getResults(agendaId);
        return new VotingSessionDTO(session);
    }

}
