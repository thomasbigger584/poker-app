package com.twb.pokerapp.ui.activity.game.texas.controller;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.twb.pokerapp.R;

import java.util.List;

public class ControlsController {
    private static final String TAG = ControlsController.class.getSimpleName();
    private final Button checkButton;
    private final Button callButton;
    private final Button betButton;
    private final Button raiseButton;
    private final Button foldButton;
    private final ProgressBar secondsLeftProgressBar;

    public ControlsController(Activity activity) {
        LinearLayout controlsLinearLayout = activity.findViewById(R.id.controlsLinearLayout);
        checkButton = controlsLinearLayout.findViewById(R.id.checkButton);
        callButton = controlsLinearLayout.findViewById(R.id.callButton);
        betButton = controlsLinearLayout.findViewById(R.id.betButton);
        raiseButton = controlsLinearLayout.findViewById(R.id.raiseButton);
        foldButton = controlsLinearLayout.findViewById(R.id.foldButton);

        secondsLeftProgressBar = activity.findViewById(R.id.secondsLeftProgressBar);
    }

    public void show(List<String> actions) {
        for (String action : actions) {
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
        secondsLeftProgressBar.setVisibility(View.VISIBLE);
    }

    public void hide() {
        setGone(checkButton);
        setGone(callButton);
        setGone(betButton);
        setGone(raiseButton);
        setGone(foldButton);
        setInvisible(secondsLeftProgressBar);
    }

    private void setInvisible(View view) {
        if (view.getVisibility() != View.INVISIBLE) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    private void setGone(View view) {
        if (view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
        }
    }

    private void setVisible(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }
}
