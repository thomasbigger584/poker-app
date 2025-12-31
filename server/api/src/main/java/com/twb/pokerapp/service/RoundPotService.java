package com.twb.pokerapp.service;

import com.twb.pokerapp.mapper.RoundPotMapper;
import com.twb.pokerapp.repository.RoundPotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoundPotService {
    private final RoundPotRepository repository;
    private final RoundPotMapper mapper;
}
