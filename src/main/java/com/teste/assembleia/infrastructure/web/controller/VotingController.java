package com.teste.assembleia.infrastructure.web.controller;

import com.teste.assembleia.application.dto.CreateVoteRequest;
import com.teste.assembleia.application.dto.CreateVotingSession;
import com.teste.assembleia.application.service.VotingService;
import com.teste.assembleia.domain.entity.Vote;
import com.teste.assembleia.domain.entity.VotingSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agendas/{agendaId}")
@AllArgsConstructor
public class VotingController {

    private final VotingService votingService;

    @PostMapping("/session")
    @ResponseStatus(HttpStatus.CREATED)
    public VotingSession openSession(
            @PathVariable Long agendaId,
            @RequestBody(required = false) CreateVotingSession createVotingSession) {
        return votingService.openSession(agendaId, createVotingSession);
    }

    @PostMapping("/votes")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Vote submitVote(
            @PathVariable Long agendaId,
            @RequestBody CreateVoteRequest createVoteRequest) {
        return votingService.submitVote(agendaId, createVoteRequest);
    }

    @GetMapping("/results")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public VotingSession getResults(@PathVariable Long agendaId) {
        return votingService.getResults(agendaId);
    }

}
