package com.twb.pokergame.service.game;

import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.repository.PokerTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class PokerGameRunnable implements Runnable {
    private final String pokerTableId;

    @Autowired
    private PokerTableRepository pokerTableRepository;

    @Override
    public void run() {


        for (int index = 0; index < 30; index++) {
            System.out.println("index = " + index);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            List<PokerTable> allPokerTables = pokerTableRepository.findAll();
            System.out.println(index + " - allPokerTables.size() = " + allPokerTables.size());
        }


    }
}
