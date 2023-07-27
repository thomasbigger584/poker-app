package com.twb.pokergame.web.rest;

import com.twb.pokergame.dto.pokertable.TableDTO;
import com.twb.pokergame.service.TableService;
import com.twb.pokergame.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
        Page<TableDTO> page = service.getAll(pageable);
        HttpHeaders headers = paginationService.createHeaders(page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
