package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.repository.*;
import com.twb.pokerapp.service.*;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract base class for game threads.
 * Provides common functionality and dependencies for game threads.
 */
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
    protected GameLogService gameLogService;

    @Autowired
    protected TableRepository tableRepository;

    @Autowired
    protected RoundRepository roundRepository;

    @Autowired
    protected RoundService roundService;

    @Autowired
    protected BettingRoundService bettingRoundService;

    @Autowired
    protected BettingRoundRepository bettingRoundRepository;

    @Autowired
    protected PlayerSessionRepository playerSessionRepository;

    @Autowired
    protected HandService handService;

    @Autowired
    protected CardService cardService;

    @Autowired
    protected PlayerActionService playerActionService;

    @Autowired
    protected PlayerActionRepository playerActionRepository;
}
