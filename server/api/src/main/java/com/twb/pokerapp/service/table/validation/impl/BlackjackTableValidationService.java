package com.twb.pokerapp.service.table.validation.impl;

import com.twb.pokerapp.dto.table.CreateTableDTO;
import com.twb.pokerapp.service.table.validation.TableValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component("blackjackTableValidationService")
public class BlackjackTableValidationService extends TableValidationService {
    @Override
    public void onValidate(CreateTableDTO dto) {
        throw new NotImplementedException("Blackjack not implemented yet");
    }
}
