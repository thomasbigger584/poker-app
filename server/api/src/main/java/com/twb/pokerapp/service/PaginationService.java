package com.twb.pokerapp.service;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;


@Component
public class PaginationService {
    private static final String HEADER_TOTAL_COUNT = "X-Total-Count";
    private static final String HEADER_TOTAL_PAGES = "X-Total-Pages";
    private static final String HEADER_CURRENT_PAGE = "X-Current-Page";
    private static final String HEADER_SIZE = "X-Size";

    public HttpHeaders createHeaders(Page<?> page) {
        var headers = new HttpHeaders();
        headers.add(HEADER_TOTAL_COUNT, Long.toString(page.getTotalElements()));
        headers.add(HEADER_TOTAL_PAGES, Integer.toString(page.getTotalPages()));
        headers.add(HEADER_CURRENT_PAGE, Integer.toString(page.getNumber()));
        headers.add(HEADER_SIZE, Integer.toString(page.getSize()));
        return headers;
    }
}
