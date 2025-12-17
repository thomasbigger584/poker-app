package com.twb.pokerapp.ui.activity.game.texas.controller;

import static com.twb.pokerapp.ui.util.ViewUtil.setGone;
import static com.twb.pokerapp.ui.util.ViewUtil.setInvisible;
import static com.twb.pokerapp.ui.util.ViewUtil.setVisible;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;

import com.twb.pokerapp.R;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerTurnDTO;

public class ControlsController {
    private static final String TAG = ControlsController.class.getSimpleName();
    private final Button checkButton;
    private final Button callButton;
    private final Button betButton;
    private final Button raiseButton;
    private final Button foldButton;
    private final ProgressBar secondsLeftProgressBar;

    public ControlsController(Activity activity) {
        var actionButtonsContainer = activity.findViewById(R.id.actionButtonsContainer);
        checkButton = actionButtonsContainer.findViewById(R.id.checkButton);
        callButton = actionButtonsContainer.findViewById(R.id.callButton);
        betButton = actionButtonsContainer.findViewById(R.id.betButton);
        raiseButton = actionButtonsContainer.findViewById(R.id.raiseButton);
        foldButton = actionButtonsContainer.findViewById(R.id.foldButton);

        secondsLeftProgressBar = activity.findViewById(R.id.secondsLeftProgressBar);
    }

    public void show(PlayerTurnDTO playerTurn) {
        for (var action : playerTurn.getNextActions()) {
            switch (action) {
                case "CHECK":
                    setVisible(checkButton);
                    break;
                case "BET":
                    setVisible(betButton);
                    break;
                case "CALL":
                    setVisible(callButton);
                    break;
                case "RAISE":
                    setVisible(raiseButton);
                    break;
                case "FOLD":
                    setVisible(foldButton);
                    break;
            }
        }
        startSecondsLeftProgressBar(playerTurn);
    }

    private void startSecondsLeftProgressBar(PlayerTurnDTO playerTurn) {
        var max = 100;
        secondsLeftProgressBar.setMax(max);
        var animator = ValueAnimator.ofInt(max, 0);
        var countdownTimeInMs = playerTurn.getPlayerTurnWaitMs() * 0.95;
        animator.setDuration((long) countdownTimeInMs);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            var progress = (int) animation.getAnimatedValue();
            secondsLeftProgressBar.setProgress(progress);
        });
        animator.start();
        setVisible(secondsLeftProgressBar);
    }

    public void hide() {
        setGone(checkButton);
        setGone(callButton);
        setGone(betButton);
        setGone(raiseButton);
        setGone(foldButton);
        setInvisible(secondsLeftProgressBar);
    }
}
