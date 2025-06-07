package com.teste.assembleia.infrastructure.controller;


import com.teste.assembleia.domain.entity.Agenda;
import com.teste.assembleia.application.service.AgendaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/agendas")
@AllArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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

    @GetMapping("/{agendaId}")
    public ResponseEntity<Agenda> getAgendaById(@PathVariable Long agendaId) {
        Optional<Agenda> agenda = agendaService.findById(agendaId);
        return agenda.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


}
