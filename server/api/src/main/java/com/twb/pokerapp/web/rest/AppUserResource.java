package com.twb.pokerapp.web.rest;

import com.twb.pokerapp.dto.appuser.AppUserDTO;
import com.twb.pokerapp.dto.appuser.UserAmountDTO;
import com.twb.pokerapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/deposit")
    public ResponseEntity<AppUserDTO> deposit(Principal principal, @Valid @RequestBody UserAmountDTO amountDto) {
        return service.deposit(principal, amountDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/withdraw")
    public ResponseEntity<AppUserDTO> withdraw(Principal principal, @Valid @RequestBody UserAmountDTO amountDto) {
        return service.withdraw(principal, amountDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
