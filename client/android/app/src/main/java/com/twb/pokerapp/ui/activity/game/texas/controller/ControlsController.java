package com.twb.pokerapp.ui.activity.game.texas.controller;

import static com.twb.pokerapp.ui.util.ViewUtil.setGone;
import static com.twb.pokerapp.ui.util.ViewUtil.setInvisible;
import static com.twb.pokerapp.ui.util.ViewUtil.setVisible;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.gridlayout.widget.GridLayout;

import com.twb.pokerapp.R;
import com.twb.pokerapp.data.model.enumeration.ActionType;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerTurnDTO;

import java.util.ArrayList;
import java.util.List;

public class ControlsController {
    private final GridLayout actionButtonsContainer;
    private final Button checkButton;
    private final Button callButton;
    private final Button betButton;
    private final Button raiseButton;
    private final Button allInButton;
    private final Button foldButton;
    private final ProgressBar secondsLeftProgressBar;

    private final List<Button> allButtons = new ArrayList<>();

    public ControlsController(Activity activity) {
        actionButtonsContainer = activity.findViewById(R.id.actionButtonsContainer);
        checkButton = actionButtonsContainer.findViewById(R.id.checkButton);
        callButton = actionButtonsContainer.findViewById(R.id.callButton);
        betButton = actionButtonsContainer.findViewById(R.id.betButton);
        raiseButton = actionButtonsContainer.findViewById(R.id.raiseButton);
        allInButton = actionButtonsContainer.findViewById(R.id.allInButton);
        foldButton = actionButtonsContainer.findViewById(R.id.foldButton);

        allButtons.add(checkButton);
        allButtons.add(callButton);
        allButtons.add(betButton);
        allButtons.add(raiseButton);
        allButtons.add(allInButton);
        allButtons.add(foldButton);

        secondsLeftProgressBar = activity.findViewById(R.id.secondsLeftProgressBar);
    }

    public void show(PlayerTurnDTO playerTurn) {
        hide();
        showActionTypeButtons(playerTurn);
        updateGridSpan();
        startSecondsLeftProgressBar(playerTurn);
    }

    public void hide() {
        for (var button : allButtons) {
            setGone(button);
        }
        setInvisible(secondsLeftProgressBar);
    }

    private void showActionTypeButtons(PlayerTurnDTO playerTurn) {
        try {
            for (var action : playerTurn.getNextActions()) {
                var actionType = ActionType.valueOf(action);
                switch (actionType) {
                    case CHECK:
                        setVisible(checkButton);
                        break;
                    case BET:
                        setVisible(betButton);
                        break;
                    case CALL:
                        setVisible(callButton);
                        break;
                    case RAISE:
                        setVisible(raiseButton);
                        break;
                    case ALL_IN:
                        setVisible(allInButton);
                        break;
                    case FOLD:
                        setVisible(foldButton);
                        break;
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid action type: " + e.getMessage());
        }
    }

    private void updateGridSpan() {
        var visibleButtons = new ArrayList<Button>();
        for (var button : allButtons) {
            if (button.getVisibility() == View.VISIBLE) {
                visibleButtons.add(button);
            }
        }

        var count = visibleButtons.size();
        if (count == 0) return;

        actionButtonsContainer.removeAllViews();

        var columns = (count == 4) ? 2 : count;
        actionButtonsContainer.setColumnCount(columns);
        actionButtonsContainer.setOrientation(GridLayout.HORIZONTAL);

        for (var button : visibleButtons) {
            var params = new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f)
            );
            params.width = 0;
            params.height = dpToPx(50);

            var margin = dpToPx(4);
            params.setMargins(margin, margin, margin, margin);
            actionButtonsContainer.addView(button, params);
        }
    }

    private int dpToPx(int dp) {
        var density = actionButtonsContainer.getContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
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
}
