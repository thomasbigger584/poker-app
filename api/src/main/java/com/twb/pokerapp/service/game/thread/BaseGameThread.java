package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.repository.*;
import com.twb.pokerapp.service.CardService;
import com.twb.pokerapp.service.DealerService;
import com.twb.pokerapp.service.HandService;
import com.twb.pokerapp.service.RoundService;
import com.twb.pokerapp.service.eval.HandEvaluator;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
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
