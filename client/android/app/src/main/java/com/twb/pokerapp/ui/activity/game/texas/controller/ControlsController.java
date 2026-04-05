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
        hide(); // This sets all buttons to GONE

        for (String action : playerTurn.getNextActions()) {
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
                case "ALL_IN":
                    setVisible(allInButton);
                    break;
                case "FOLD":
                    setVisible(foldButton);
                    break;
            }
        }

        updateGridSpan();
        startSecondsLeftProgressBar(playerTurn);
    }

    private void updateGridSpan() {
        // 1. Identify which buttons need to be shown
        List<Button> visibleButtons = new ArrayList<>();
        for (Button btn : allButtons) {
            if (btn.getVisibility() == View.VISIBLE) {
                visibleButtons.add(btn);
            }
        }

        int count = visibleButtons.size();
        if (count == 0) return;

        // 2. Remove all buttons from the grid to reset internal indices
        actionButtonsContainer.removeAllViews();

        // 3. Configure the Grid
        int columns = (count == 4) ? 2 : count;
        actionButtonsContainer.setColumnCount(columns);
        actionButtonsContainer.setOrientation(GridLayout.HORIZONTAL);

        // 4. Re-add only the visible buttons with fresh LayoutParams
        for (Button btn : visibleButtons) {
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f), // Row spec
                    GridLayout.spec(GridLayout.UNDEFINED, 1f)  // Column spec + Weight
            );

            params.width = 0; // Required for the 1f weight to work
            params.height = dpToPx(50);

            int margin = dpToPx(4);
            params.setMargins(margin, margin, margin, margin);

            // Re-add the button to the layout
            actionButtonsContainer.addView(btn, params);
        }
    }

    private int dpToPx(int dp) {
        float density = actionButtonsContainer.getContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void startSecondsLeftProgressBar(PlayerTurnDTO playerTurn) {
        int max = 100;
        secondsLeftProgressBar.setMax(max);
        ValueAnimator animator = ValueAnimator.ofInt(max, 0);
        double countdownTimeInMs = playerTurn.getPlayerTurnWaitMs() * 0.95;
        animator.setDuration((long) countdownTimeInMs);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            int progress = (int) animation.getAnimatedValue();
            secondsLeftProgressBar.setProgress(progress);
        });
        animator.start();
        setVisible(secondsLeftProgressBar);
    }

    public void hide() {
        for (Button btn : allButtons) {
            setGone(btn);
        }
        setInvisible(secondsLeftProgressBar);
    }
}
