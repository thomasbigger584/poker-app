package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.repository.BettingRoundRepository;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.repository.RoundRepository;
import com.twb.pokerapp.repository.TableRepository;
import com.twb.pokerapp.service.BettingRoundService;
import com.twb.pokerapp.service.CardService;
import com.twb.pokerapp.service.HandService;
import com.twb.pokerapp.service.RoundService;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.support.TransactionTemplate;

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
    protected BettingRoundRepository bettingRoundRepository;

    @Autowired
    protected RoundRepository roundRepository;

    @Autowired
    protected RoundService roundService;

    @Autowired
    protected BettingRoundService bettingRoundService;

    @Autowired
    protected PlayerSessionRepository playerSessionRepository;

    @Autowired
    protected HandService handService;

    @Autowired
    protected CardService cardService;

    @Autowired
    protected TransactionTemplate writeTx;

    @Autowired
    @Qualifier("readTx")
    protected TransactionTemplate readTx;
}
