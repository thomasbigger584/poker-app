package com.twb.pokerapp.web.rest;

import com.twb.pokerapp.mapper.TableMapper;
import com.twb.pokerapp.proto.AvailableTableListResponse;
import com.twb.pokerapp.proto.CreateTableDTO;
import com.twb.pokerapp.proto.TableDTO;
import com.twb.pokerapp.service.PaginationService;
import com.twb.pokerapp.service.table.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/poker-table")
@RequiredArgsConstructor
public class TableResource {
    private final TableService service;
    private final TableMapper tableMapper;
    private final PaginationService paginationService;

    @PostMapping
    public ResponseEntity<TableDTO> create(@RequestBody CreateTableDTO dto) {
        var table = service.create(dto);
        var response = tableMapper.modelToDto(table);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<AvailableTableListResponse> getAll(Pageable pageable, Principal principal) {
        var page = service.getAllAvailable(pageable, principal.getName());
        var headers = paginationService.createHeaders(page);
        var response = AvailableTableListResponse.newBuilder()
                .addAllTables(page.getContent())
                .build();
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
}
