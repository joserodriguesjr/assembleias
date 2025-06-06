package com.teste.assembleia.controller;


import com.teste.assembleia.model.Agenda;
import com.teste.assembleia.service.AgendaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Controller
@RequestMapping("/agendas")
@AllArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @PostMapping
    public ResponseEntity<Agenda> create(@RequestBody Agenda agenda) {
        try {
            Agenda newAgenda = agendaService.create(agenda);

            URI agendaURI = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(newAgenda.getId())
                    .toUri();

            return ResponseEntity.created(agendaURI).body(newAgenda);
        } catch (Exception e) {
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

}
