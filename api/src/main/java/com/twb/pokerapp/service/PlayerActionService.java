package com.twb.pokerapp.service;

import com.twb.pokerapp.mapper.PlayerActionMapper;
import com.twb.pokerapp.repository.PlayerActionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class PlayerActionService {
    private final PlayerActionRepository repository;
    private final PlayerActionMapper mapper;

}
