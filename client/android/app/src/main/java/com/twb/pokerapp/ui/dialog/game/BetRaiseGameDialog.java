package com.twb.pokerapp.ui.dialog.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.twb.pokerapp.R;
import com.twb.pokerapp.data.model.enumeration.ActionType;
import com.twb.pokerapp.ui.util.SeekBarChangeListener;

import java.util.Locale;

public class BetRaiseGameDialog extends BaseGameDialog {
    private BetRaiseClickListener betRaiseListener;
    private ActionType type;
    private TextView titleTextView;
    private SeekBar betRaiseSeekBar;
    private double playerCurrentFunds;
    private double minimumBet;
    private double amountSelected;

    public static BetRaiseGameDialog newInstance(ActionType type,
                                                 double playerCurrentFunds, double minimumBet,
                                                 BetRaiseClickListener listener) {
        var fragment = new BetRaiseGameDialog();
        fragment.betRaiseListener = listener;
        fragment.type = type;
        fragment.minimumBet = minimumBet;
        fragment.playerCurrentFunds = playerCurrentFunds;
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        var bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_bet_raise_dialog, container, false);

        titleTextView = inflatedView.findViewById(R.id.titleTextView);
        betRaiseSeekBar = inflatedView.findViewById(R.id.betRaiseSeekBar);
        betRaiseSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListener() {
            @Override
            public void onProgressChanged(int progress) {
                setTitleTextView(((float) progress) / 100f);
            }
        });

        var successButton = inflatedView.findViewById(R.id.successButton);
        successButton.setOnClickListener(v -> {
            if (betRaiseListener != null) {
                betRaiseListener.onBetSelected(type, amountSelected);
            }
            dismissAllowingStateLoss();
        });

        setSeekBar(playerCurrentFunds);
        return inflatedView;
    }

    private void setTitleTextView(double amount) {
        amountSelected = round(amount);
        switch (type) {
            case BET: {
                titleTextView.setText(String.format(Locale.getDefault(),
                        "Bet: %.2f", amountSelected));
                break;
            }
            case RAISE: {
                titleTextView.setText(String.format(Locale.getDefault(),
                        "Raise: %.2f", amountSelected));
                break;
            }
        }
    }

    private void setSeekBar(double amount) {
        var seekbarAmount = amount * 100;
        betRaiseSeekBar.setMax((int) seekbarAmount);

        var thisMinimumBet = minimumBet * 100;
        betRaiseSeekBar.setProgress((int) thisMinimumBet);

        setTitleTextView(thisMinimumBet / 100);
    }

    private double round(double amount) {
        var amountStr = String.format(Locale.getDefault(), "%.2f", amount);
        return Double.parseDouble(amountStr);
    }

    public interface BetRaiseClickListener {
        void onBetSelected(ActionType type, double betAmount);
    }
}
