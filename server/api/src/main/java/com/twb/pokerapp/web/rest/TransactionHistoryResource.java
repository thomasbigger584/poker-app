package com.twb.pokerapp.web.rest;

import com.twb.pokerapp.dto.transactionhistory.TransactionHistoryDTO;
import com.twb.pokerapp.service.PaginationService;
import com.twb.pokerapp.service.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/transaction-history")
@RequiredArgsConstructor
public class TransactionHistoryResource {
    private final TransactionHistoryService service;
    private final PaginationService paginationService;

    @GetMapping("/current")
    public ResponseEntity<List<TransactionHistoryDTO>> getCurrent(Principal principal, Pageable pageable,
                                                                  @RequestParam(value = "type", required = false) String type) {
        var page = service.findCurrent(principal, type, pageable);
        var headers = paginationService.createHeaders(page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
