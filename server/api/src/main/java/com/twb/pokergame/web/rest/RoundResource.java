package com.twb.pokergame.web.rest;

import com.twb.pokergame.dto.round.RoundDTO;
import com.twb.pokergame.service.RoundService;
import com.twb.pokergame.web.rest.util.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/round")
@RequiredArgsConstructor
public class RoundResource {
    private final RoundService service;
    private final PaginationService paginationService;

    @GetMapping("/current/{tableId}")
    public ResponseEntity<RoundDTO> getCurrentRound(@PathVariable("tableId") UUID tableId) {
        RoundDTO result = service.getCurrent(tableId);
        return ResponseEntity.ok(result);
    }
}
