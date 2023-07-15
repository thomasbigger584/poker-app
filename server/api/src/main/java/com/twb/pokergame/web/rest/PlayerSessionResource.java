package com.twb.pokergame.web.rest;

import com.twb.pokergame.service.PlayerSessionService;
import com.twb.pokergame.web.rest.util.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/player-session")
@RequiredArgsConstructor
public class PlayerSessionResource {
    private final PlayerSessionService service;
    private final PaginationService paginationService;

}
