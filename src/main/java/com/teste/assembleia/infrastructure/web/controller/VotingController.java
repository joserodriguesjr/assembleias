package com.teste.assembleia.infrastructure.web.controller;

import com.teste.assembleia.application.dto.CreateVoteRequest;
import com.teste.assembleia.application.dto.CreateVotingSessionDTO;
import com.teste.assembleia.application.dto.VotingSessionResponseDTO;
import com.teste.assembleia.domain.entity.Vote;
import com.teste.assembleia.domain.entity.VotingSession;
import com.teste.assembleia.application.service.VoteService;
import com.teste.assembleia.application.service.VotingSessionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/agendas/{agendaId}")
@AllArgsConstructor
public class VotingController {

    private final VotingSessionService votingSessionService;
    private final VoteService voteService;

    @GetMapping("/session")
    public ResponseEntity<List<VotingSessionResponseDTO>> listVotingSessions(@PathVariable Long agendaId) {
        List<VotingSessionResponseDTO> sessions = votingSessionService.listAllByAgendaId(agendaId);

        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<VotingSessionResponseDTO> getVotingSessionById(@PathVariable Long agendaId,
                                                                         @PathVariable Long sessionId) {
        Optional<VotingSession> sessionOpt = votingSessionService.findByIdAndAgendaId(sessionId, agendaId);

        return sessionOpt
                .map(session -> ResponseEntity.ok(new VotingSessionResponseDTO(session)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/session")
    public ResponseEntity<VotingSessionResponseDTO> openVotingSession(
            @PathVariable Long agendaId,
            @RequestBody(required = false) CreateVotingSessionDTO dto) {

        VotingSession session = votingSessionService.create(agendaId, dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new VotingSessionResponseDTO(session));
    }

    @PatchMapping("/session/{sessionId}/close")
    public ResponseEntity<VotingSessionResponseDTO> closeVotingSession(
            @PathVariable Long agendaId,
            @PathVariable Long sessionId) {

        VotingSession session = votingSessionService.close(sessionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new VotingSessionResponseDTO(session));
    }

    @PostMapping("/session/{sessionId}/votes")
    public ResponseEntity<Vote> submitVote(
            @PathVariable Long agendaId,
            @PathVariable Long sessionId,
            @RequestBody CreateVoteRequest createVoteRequest) {

        Vote vote = voteService.newVote(sessionId, createVoteRequest);

        return ResponseEntity.status(HttpStatus.OK).body(vote);
    }

//    @GetMapping("/session/{sessionId}/results")
//    public ResponseEntity obterResultado(@PathVariable String pautaId) {
//        return votacaoService.apurarResultado(pautaId);
//    }
}
