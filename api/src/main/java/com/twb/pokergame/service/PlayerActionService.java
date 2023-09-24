package com.twb.pokergame.service;

import com.twb.pokergame.mapper.PlayerActionMapper;
import com.twb.pokergame.repository.PlayerActionRepository;
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
