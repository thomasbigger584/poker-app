package com.twb.pokerapp.web.rest;

import com.twb.pokerapp.dto.pokertable.CreateTableDTO;
import com.twb.pokerapp.dto.pokertable.TableDTO;
import com.twb.pokerapp.mapper.TableMapper;
import com.twb.pokerapp.service.PaginationService;
import com.twb.pokerapp.service.table.TableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/poker-table")
@RequiredArgsConstructor
public class TableResource {
    private final TableService service;
    private final TableMapper tableMapper;
    private final PaginationService paginationService;

    @PostMapping
    public ResponseEntity<TableDTO> create(@Valid @RequestBody CreateTableDTO dto) {
        var table = service.create(dto);
        var response = tableMapper.modelToDto(table);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TableDTO>> getAll(Pageable pageable) {
        var page = service.getAll(pageable);
        var headers = paginationService.createHeaders(page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
