package com.teste.assembleia.controller;


import com.teste.assembleia.dto.CreateVotingSessionDTO;
import com.teste.assembleia.dto.VotingSessionResponseDTO;
import com.teste.assembleia.model.Agenda;
import com.teste.assembleia.model.VotingSession;
import com.teste.assembleia.service.AgendaService;
import com.teste.assembleia.service.VotingSessionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/agendas")
@AllArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;
    private final VotingSessionService votingSessionService;

    @PostMapping
    public ResponseEntity<Agenda> createAgenda(@RequestBody Agenda agenda) {
        try {
            Agenda newAgenda = agendaService.create(agenda);

            URI agendaURI = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(newAgenda.getId())
                    .toUri();

            return ResponseEntity.created(agendaURI).body(newAgenda);
        } catch (Exception e) {
//            todo: exceptions
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agenda> getAgendaById(@PathVariable Long id) {
        Optional<Agenda> agenda = agendaService.findById(id);
        return agenda.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("{id}/voting-session")
    public ResponseEntity<VotingSessionResponseDTO> openVotingSession(
            @PathVariable Long id,
            @RequestBody(required = false) CreateVotingSessionDTO dto) {

        VotingSession session = votingSessionService.create(id, dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new VotingSessionResponseDTO(session));
    }

    @GetMapping("{id}/voting-session")
    public ResponseEntity<List<VotingSessionResponseDTO>> listVotingSessions(@PathVariable Long id) {
        List<VotingSession> sessions = votingSessionService.listAllByAgendaId(id);
        List<VotingSessionResponseDTO> result = sessions.stream()
                .map(VotingSessionResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("{id}/voting-session/{votingSessionId}")
    public ResponseEntity<VotingSessionResponseDTO> getVotingSessionById(@PathVariable Long id,
                                                                         @PathVariable Long votingSessionId) {
        Optional<VotingSession> sessionOpt = votingSessionService.findByIdAndAgendaId(votingSessionId, id);

        return sessionOpt
                .map(session -> ResponseEntity.ok(new VotingSessionResponseDTO(session)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("{id}/voting-session/{votingSessionId}/close")
    public ResponseEntity<VotingSessionResponseDTO> closeVotingSession(
            @PathVariable Long id,
            @PathVariable Long votingSessionId) {

        VotingSession session = votingSessionService.close(votingSessionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new VotingSessionResponseDTO(session));
    }


}
