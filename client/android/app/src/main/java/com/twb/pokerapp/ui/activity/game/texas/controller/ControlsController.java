package com.twb.pokerapp.ui.activity.game.texas.controller;

import static com.twb.pokerapp.ui.util.ViewUtil.setGone;
import static com.twb.pokerapp.ui.util.ViewUtil.setInvisible;
import static com.twb.pokerapp.ui.util.ViewUtil.setVisible;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import androidx.gridlayout.widget.GridLayout;

import com.twb.pokerapp.databinding.ActivityGameTexasBinding;
import com.twb.pokerapp.data.model.enumeration.ActionType;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerTurnDTO;

import java.util.ArrayList;
import java.util.List;

public class ControlsController {
    private final ActivityGameTexasBinding binding;
    private final List<Button> allButtons = new ArrayList<>();

    private ValueAnimator animator;

    public ControlsController(ActivityGameTexasBinding binding) {
        this.binding = binding;

        allButtons.add(binding.checkButton);
        allButtons.add(binding.callButton);
        allButtons.add(binding.betButton);
        allButtons.add(binding.raiseButton);
        allButtons.add(binding.allInButton);
        allButtons.add(binding.foldButton);
    }

    public void show(PlayerTurnDTO playerTurn) {
        hide();
        showActionTypeButtons(playerTurn);
        updateGridSpan();
        startSecondsLeftProgressBar(playerTurn);
    }

    public void hide() {
        if (animator != null) {
            animator.cancel();
        }
        for (var button : allButtons) {
            setGone(button);
        }
        setInvisible(binding.secondsLeftProgressBar);
    }

    private void showActionTypeButtons(PlayerTurnDTO playerTurn) {
        try {
            for (var action : playerTurn.getNextActions()) {
                var actionType = ActionType.valueOf(action);
                switch (actionType) {
                    case CHECK:
                        setVisible(binding.checkButton);
                        break;
                    case BET:
                        setVisible(binding.betButton);
                        break;
                    case CALL:
                        setVisible(binding.callButton);
                        break;
                    case RAISE:
                        setVisible(binding.raiseButton);
                        break;
                    case ALL_IN:
                        setVisible(binding.allInButton);
                        break;
                    case FOLD:
                        setVisible(binding.foldButton);
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

        binding.actionButtonsContainer.removeAllViews();

        int columns;
        if (count <= 3) {
            columns = count;
        } else {
            columns = (count == 4) ? 2 : 3;
        }
        binding.actionButtonsContainer.setColumnCount(columns);

        for (var button : visibleButtons) {
            var params = new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f)
            );
            params.width = 0;
            params.height = dpToPx(50);

            var margin = dpToPx(4);
            params.setMargins(margin, margin, margin, margin);
            binding.actionButtonsContainer.addView(button, params);
        }
    }

    private int dpToPx(int dp) {
        var density = binding.getRoot().getContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void startSecondsLeftProgressBar(PlayerTurnDTO playerTurn) {
        if (animator != null) {
            animator.cancel();
        }
        var max = 100;
        binding.secondsLeftProgressBar.setMax(max);
        animator = ValueAnimator.ofInt(max, 0);
        var countdownTimeInMs = playerTurn.getPlayerTurnWaitMs() * 0.95;
        animator.setDuration((long) countdownTimeInMs);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            var progress = (int) animation.getAnimatedValue();
            binding.secondsLeftProgressBar.setProgress(progress);
        });
        animator.start();
        setVisible(binding.secondsLeftProgressBar);
    }
}
