package com.twb.pokerapp.service;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;


@Component
public class PaginationService {

    public HttpHeaders createHeaders(Page<?> page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", Long.toString(page.getTotalElements()));
        headers.add("X-Total-Pages", Integer.toString(page.getTotalPages()));
        headers.add("X-Current-Page", Integer.toString(page.getNumber()));
        headers.add("X-Size", Integer.toString(page.getSize()));
        return headers;
    }
}
