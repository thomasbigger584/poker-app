package com.twb.pokerapp.web.rest;

import com.twb.pokerapp.dto.appuser.AppUserDTO;
import com.twb.pokerapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/app-user")
@RequiredArgsConstructor
public class AppUserResource {
    private final UserService service;

    @GetMapping("/current")
    public ResponseEntity<AppUserDTO> getCurrent(Principal principal) {
        return service.getCurrentUser(principal)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/reset-funds")
    public ResponseEntity<AppUserDTO> resetFunds(Principal principal) {
        return service.resetFunds(principal)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
