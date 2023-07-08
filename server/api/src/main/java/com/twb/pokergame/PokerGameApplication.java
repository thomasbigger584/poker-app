package com.twb.pokergame;

import com.twb.pokergame.old.Tables;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PokerGameApplication {

    public static void main(String[] args) {
        Tables.loadTables();
        SpringApplication.run(PokerGameApplication.class, args);
    }
}
