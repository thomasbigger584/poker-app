package com.twb.pokerapp.web.rest;

import com.twb.pokerapp.proto.AppUserDTO;
import com.twb.pokerapp.proto.AppUserListResponse;
import com.twb.pokerapp.proto.UserAmountDTO;
import com.twb.pokerapp.service.user.BotUserService;
import com.twb.pokerapp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/app-user")
@RequiredArgsConstructor
public class AppUserResource {
    private final UserService service;
    private final BotUserService botUserService;

    @GetMapping("/current")
    public ResponseEntity<AppUserDTO> getCurrent(Principal principal) {
        return service.getCurrentUser(principal)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bots")
    public ResponseEntity<AppUserListResponse> getBots() {
        var response = AppUserListResponse.newBuilder()
                .addAllUsers(botUserService.listBots())
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-funds")
    public ResponseEntity<AppUserDTO> resetFunds(Principal principal) {
        return service.resetFunds(principal)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/deposit")
    public ResponseEntity<AppUserDTO> deposit(Principal principal, @RequestBody UserAmountDTO amountDto) {
        return service.deposit(principal, amountDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/withdraw")
    public ResponseEntity<AppUserDTO> withdraw(Principal principal, @RequestBody UserAmountDTO amountDto) {
        return service.withdraw(principal, amountDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
