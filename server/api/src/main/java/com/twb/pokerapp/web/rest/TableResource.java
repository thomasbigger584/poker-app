package com.twb.pokerapp.web.rest;

import com.twb.pokerapp.dto.pokertable.TableDTO;
import com.twb.pokerapp.service.PaginationService;
import com.twb.pokerapp.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/poker-table")
@RequiredArgsConstructor
public class TableResource {
    private final TableService service;
    private final PaginationService paginationService;

    @GetMapping
    public ResponseEntity<List<TableDTO>> getAll(Pageable pageable) {
        var page = service.getAll(pageable);
        var headers = paginationService.createHeaders(page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
