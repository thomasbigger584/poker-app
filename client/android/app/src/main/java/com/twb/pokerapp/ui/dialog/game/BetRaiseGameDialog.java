package com.twb.pokerapp.ui.dialog.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.twb.pokerapp.R;
import com.twb.pokerapp.data.model.enumeration.ActionType;
import com.twb.pokerapp.databinding.FragmentBetRaiseDialogBinding;
import com.twb.pokerapp.ui.util.SeekBarChangeListener;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BetRaiseGameDialog extends BaseGameDialog {
    private BetRaiseClickListener betRaiseListener;
    private ActionType type;
    private FragmentBetRaiseDialogBinding binding;
    private double maximumBet;
    private double minimumBet;
    private double amountSelected;

    public static BetRaiseGameDialog newInstance(ActionType type,
                                                 double maximumBet,
                                                 double minimumBet,
                                                 BetRaiseClickListener listener) {
        var fragment = new BetRaiseGameDialog();
        fragment.betRaiseListener = listener;
        fragment.type = type;
        fragment.minimumBet = minimumBet;
        fragment.maximumBet = maximumBet;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBetRaiseDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.betRaiseSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListener() {
            @Override
            public void onProgressChanged(int progress) {
                var amount = ((double) progress) / 100.0;
                if (amount < minimumBet) {
                    amount = minimumBet;
                }
                setTitleTextView(amount);
            }
        });

        binding.successButton.setOnClickListener(v -> {
            if (betRaiseListener != null) {
                betRaiseListener.onBetSelected(type, amountSelected);
            }
            dismiss();
        });

        setSeekBar(maximumBet);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setTitleTextView(double amount) {
        amountSelected = round(amount);
        var stringResId = (type == ActionType.BET) ? R.string.bet_amount_format : R.string.raise_amount_format;
        binding.titleTextView.setText(getString(stringResId, amountSelected));
    }

    private void setSeekBar(double amount) {
        var seekbarMax = (int) Math.round(amount * 100);
        binding.betRaiseSeekBar.setMax(seekbarMax);

        var seekbarProgress = (int) Math.round(minimumBet * 100);
        binding.betRaiseSeekBar.setProgress(seekbarProgress);

        setTitleTextView((double) seekbarProgress / 100.0);
    }

    private double round(double amount) {
        return BigDecimal.valueOf(amount)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public interface BetRaiseClickListener {
        void onBetSelected(ActionType type, double betAmount);
    }
}
