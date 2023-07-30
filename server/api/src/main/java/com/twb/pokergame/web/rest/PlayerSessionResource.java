package com.twb.pokergame.web.rest;

import com.twb.pokergame.dto.playersession.PlayerSessionDTO;
import com.twb.pokergame.service.PaginationService;
import com.twb.pokergame.service.PlayerSessionService;
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
@RequestMapping("/public/player-session")
@RequiredArgsConstructor
public class PlayerSessionResource {
    private final PlayerSessionService service;
    private final PaginationService paginationService;

    @GetMapping("/{tableId}")
    public ResponseEntity<List<PlayerSessionDTO>> getByTableId(@PathVariable("tableId") UUID tableId) {
        List<PlayerSessionDTO> result = service.getByTableId(tableId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
