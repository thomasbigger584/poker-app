package com.twb.pokergame.service.game.thread;

import com.twb.pokergame.repository.*;
import com.twb.pokergame.service.CardService;
import com.twb.pokergame.service.DealerService;
import com.twb.pokergame.service.HandService;
import com.twb.pokergame.service.RoundService;
import com.twb.pokergame.service.eval.HandEvaluator;
import com.twb.pokergame.web.websocket.message.MessageDispatcher;
import com.twb.pokergame.web.websocket.message.server.ServerMessageFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseGameThread extends Thread {

    // *****************************************************************************************
    // Autowired Beans
    // *****************************************************************************************

    @Autowired
    protected GameThreadManager threadManager;

    @Autowired
    protected ServerMessageFactory messageFactory;

    @Autowired
    protected MessageDispatcher dispatcher;

    @Autowired
    protected TableRepository tableRepository;

    @Autowired
    protected RoundRepository roundRepository;

    @Autowired
    protected RoundService roundService;

    @Autowired
    protected DealerService dealerService;

    @Autowired
    protected PlayerSessionRepository playerSessionRepository;

    @Autowired
    protected HandService handService;

    @Autowired
    protected HandRepository handRepository;

    @Autowired
    protected CardService cardService;

    @Autowired
    protected CardRepository cardRepository;

    @Autowired
    protected HandEvaluator handEvaluator;
}
