package com.twb.pokerapp.web.rest;

import com.twb.pokerapp.proto.PlayerStatsResponse;
import com.twb.pokerapp.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsResource {
    private final StatsService service;

    @GetMapping("/current")
    public ResponseEntity<PlayerStatsResponse> getCurrent(Principal principal) {
        return service.getCurrent(principal)
                .map(stats -> ResponseEntity.ok(
                        PlayerStatsResponse.newBuilder().setStats(stats).build()))
                .orElse(ResponseEntity.notFound().build());
    }
}
