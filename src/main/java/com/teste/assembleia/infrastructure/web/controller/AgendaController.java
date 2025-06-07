package com.teste.assembleia.infrastructure.web.controller;


import com.teste.assembleia.application.service.AgendaService;
import com.teste.assembleia.domain.entity.Agenda;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agendas")
@AllArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Agenda createAgenda(@RequestBody Agenda agenda) {
        return agendaService.create(agenda);
    }

    @GetMapping("/{agendaId}")
    public Agenda getAgendaById(@PathVariable Long agendaId) {
        return agendaService.findById(agendaId);
    }


}
