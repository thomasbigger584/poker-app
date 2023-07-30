package com.twb.pokergame.web.rest;

import com.twb.pokergame.dto.round.RoundDTO;
import com.twb.pokergame.service.PaginationService;
import com.twb.pokergame.service.RoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/public/round")
@RequiredArgsConstructor
public class RoundResource {
    private final RoundService service;
    private final PaginationService paginationService;

    @GetMapping("/current/{tableId}")
    public ResponseEntity<RoundDTO> getCurrentByTableId(@PathVariable("tableId") UUID tableId) {
        RoundDTO result = service.getCurrent(tableId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{tableId}")
    public ResponseEntity<List<RoundDTO>> getByTableId(@PathVariable("tableId") UUID tableId) {
        List<RoundDTO> result = service.getByTableId(tableId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
