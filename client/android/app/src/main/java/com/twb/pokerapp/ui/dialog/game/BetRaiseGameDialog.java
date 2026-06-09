package com.twb.pokerapp.ui.dialog.game;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.gridlayout.widget.GridLayout;

import com.twb.pokerapp.R;
import com.twb.pokerapp.proto.ActionType;
import com.twb.pokerapp.databinding.FragmentBetRaiseDialogBinding;
import com.twb.pokerapp.ui.util.SeekBarChangeListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Deque;

public class BetRaiseGameDialog extends BaseGameDialog {

    // Casino chip denominations and their classic colours.
    private static final int[] CHIP_VALUES = {5, 10, 20, 50, 100, 500, 1000};
    private static final int[] CHIP_COLORS = {
            0xFFD32F2F, // 5    red
            0xFF1976D2, // 10   blue
            0xFF388E3C, // 20   green
            0xFFF57C00, // 50   orange
            0xFF212121, // 100  black
            0xFF7B1FA2, // 500  purple
            0xFFC8961E, // 1000 gold
    };
    private static final int CHIP_GOLD = 0xFFC8961E;
    private static final int CHIP_DARK_TEXT = 0xFF3E2723;

    // Coarse, "round" slider increments so the custom slider lands on clean numbers fast.
    private static final int[] NICE_STEPS = {5, 10, 25, 50, 100, 250, 500, 1000, 2500, 5000};
    private static final int TARGET_SLIDER_NOTCHES = 40;

    private BetRaiseClickListener betRaiseListener;
    private ActionType type;
    private FragmentBetRaiseDialogBinding binding;
    private double maximumBet;
    private double minimumBet;
    private double amountSelected;

    // Deltas actually applied per chip tap, so "−" can peel the last chip back off
    // (a tap clamped at the stack ceiling pushes only the partial amount it added).
    private final Deque<Double> chipDeltas = new ArrayDeque<>();

    private double sliderStep = 5d;
    private boolean updatingSlider = false;

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

        var titleResId = (type == ActionType.ACTION_TYPE_BET) ? R.string.bet_title_bet : R.string.bet_title_raise;
        binding.titleTextView.setText(titleResId);
        binding.rangeTextView.setText(getString(R.string.bet_range_format, minimumBet, maximumBet));

        buildChipRack();
        configureSlider();

        binding.undoButton.setOnClickListener(v -> undoLastChip());
        binding.minButton.setOnClickListener(v -> setAbsoluteAmount(minimumBet));
        binding.allInButton.setOnClickListener(v -> setAbsoluteAmount(maximumBet));
        binding.customButton.setOnClickListener(v -> toggleSlider());

        binding.betRaiseSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListener() {
            @Override
            public void onProgressChanged(int progress) {
                if (updatingSlider) return;
                // Slider sets an absolute amount, so the chip-undo stack no longer applies.
                chipDeltas.clear();
                amountSelected = clamp(minimumBet + (progress * sliderStep));
                refresh(false);
            }
        });

        binding.successButton.setOnClickListener(v -> {
            if (betRaiseListener != null) {
                betRaiseListener.onBetSelected(type, round(amountSelected));
            }
            dismiss();
        });

        amountSelected = clamp(minimumBet);
        refresh(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // -- chip rack -----------------------------------------------------------------

    private void buildChipRack() {
        for (var index = 0; index < CHIP_VALUES.length; index++) {
            var value = CHIP_VALUES[index];
            var chip = createChipView(value, CHIP_COLORS[index]);
            // A chip larger than the whole stack can never be added — show it greyed out.
            if (value > maximumBet) {
                chip.setEnabled(false);
                chip.setAlpha(0.3f);
            } else {
                chip.setOnClickListener(v -> addChip(value));
            }
            binding.chipRack.addView(chip);
        }
    }

    private TextView createChipView(int value, int fillColor) {
        var chip = new TextView(requireContext());
        chip.setText(chipLabel(value));
        chip.setGravity(Gravity.CENTER);
        chip.setTypeface(Typeface.DEFAULT_BOLD);
        chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        chip.setTextColor(fillColor == CHIP_GOLD ? CHIP_DARK_TEXT : 0xFFFFFFFF);
        chip.setBackground(createChipDrawable(fillColor));
        chip.setElevation(dp(3));

        var size = (int) dp(52);
        var margin = (int) dp(5);
        var params = new GridLayout.LayoutParams();
        params.width = size;
        params.height = size;
        params.setMargins(margin, margin, margin, margin);
        chip.setLayoutParams(params);
        return chip;
    }

    private GradientDrawable createChipDrawable(int fillColor) {
        var drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(fillColor);
        // Dashed white rim — the classic poker-chip edge.
        drawable.setStroke((int) dp(3), 0xFFFFFFFF, dp(5), dp(5));
        return drawable;
    }

    private String chipLabel(int value) {
        if (value >= 1000) {
            return getString(R.string.bet_chip_thousands_format, value / 1000);
        }
        return String.valueOf(value);
    }

    // -- amount changes ------------------------------------------------------------

    private void addChip(int value) {
        var delta = clamp(amountSelected + value) - amountSelected;
        if (delta <= 0) return; // already at the stack ceiling
        amountSelected += delta;
        chipDeltas.push(delta);
        refresh(true);
    }

    private void undoLastChip() {
        if (!chipDeltas.isEmpty()) {
            amountSelected = clamp(amountSelected - chipDeltas.pop());
        } else {
            amountSelected = clamp(minimumBet);
        }
        refresh(true);
    }

    private void setAbsoluteAmount(double amount) {
        chipDeltas.clear();
        amountSelected = clamp(amount);
        refresh(true);
    }

    /**
     * Pushes current state to the UI. When {@code syncSlider} is true the slider thumb is moved to
     * match — skipped when the change came from the slider itself to avoid a feedback loop.
     */
    private void refresh(boolean syncSlider) {
        amountSelected = round(amountSelected);

        binding.amountTextView.setText(getString(R.string.currency_format, amountSelected));

        var confirmFormat = (type == ActionType.ACTION_TYPE_BET)
                ? R.string.bet_confirm_format : R.string.raise_confirm_format;
        binding.successButton.setText(getString(confirmFormat, amountSelected));

        if (syncSlider) {
            updatingSlider = true;
            binding.betRaiseSeekBar.setProgress(progressForAmount(amountSelected));
            updatingSlider = false;
        }
    }

    // -- slider --------------------------------------------------------------------

    private void configureSlider() {
        var range = maximumBet - minimumBet;
        if (range <= 0) {
            sliderStep = 1d;
            binding.betRaiseSeekBar.setMax(0);
            binding.betRaiseSeekBar.setEnabled(false);
            return;
        }
        var rawStep = range / TARGET_SLIDER_NOTCHES;
        sliderStep = NICE_STEPS[NICE_STEPS.length - 1];
        for (var nice : NICE_STEPS) {
            if (nice >= rawStep) {
                sliderStep = nice;
                break;
            }
        }
        binding.betRaiseSeekBar.setMax((int) Math.ceil(range / sliderStep));
    }

    private void toggleSlider() {
        var seekBar = binding.betRaiseSeekBar;
        if (seekBar.getVisibility() == View.VISIBLE) {
            seekBar.setVisibility(View.GONE);
        } else {
            seekBar.setVisibility(View.VISIBLE);
            updatingSlider = true;
            seekBar.setProgress(progressForAmount(amountSelected));
            updatingSlider = false;
        }
    }

    private int progressForAmount(double amount) {
        var progress = (int) Math.round((amount - minimumBet) / sliderStep);
        return Math.max(0, Math.min(progress, binding.betRaiseSeekBar.getMax()));
    }

    // -- helpers -------------------------------------------------------------------

    private double clamp(double amount) {
        if (amount < minimumBet) return minimumBet;
        if (amount > maximumBet) return maximumBet;
        return amount;
    }

    private double round(double amount) {
        return BigDecimal.valueOf(amount)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    public interface BetRaiseClickListener {
        void onBetSelected(ActionType type, double betAmount);
    }
}
