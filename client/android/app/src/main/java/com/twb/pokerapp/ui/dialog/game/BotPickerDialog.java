package com.twb.pokerapp.ui.dialog.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.gridlayout.widget.GridLayout;

import com.twb.pokerapp.data.model.dto.appuser.AppUserDTO;
import com.twb.pokerapp.databinding.DialogBotPickerBinding;
import com.twb.pokerapp.databinding.ItemBotPickerBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Felt-and-gold themed picker for adding a bot to the table. Rows are built directly from the bot
 * list (kept small) rather than via a RecyclerView adapter.
 */
public class BotPickerDialog extends BaseGameDialog {

    private DialogBotPickerBinding binding;
    private List<AppUserDTO> bots = new ArrayList<>();
    private OnBotSelectedListener listener;

    public static BotPickerDialog newInstance(List<AppUserDTO> bots, OnBotSelectedListener listener) {
        var fragment = new BotPickerDialog();
        fragment.bots = bots;
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DialogBotPickerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        var inflater = LayoutInflater.from(requireContext());
        var density = getResources().getDisplayMetrics().density;
        var cardWidth = Math.round(230 * density);
        var gap = Math.round(5 * density);

        for (var bot : bots) {
            var row = ItemBotPickerBinding.inflate(inflater, binding.botList, false);

            var displayName = (bot.getPersona() != null) ? bot.getPersona() : bot.getUsername();
            row.botNameTextView.setText(displayName);
            row.botUsernameTextView.setText(bot.getUsername());
            row.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBotSelected(bot);
                }
                dismiss();
            });

            var params = new GridLayout.LayoutParams();
            params.width = cardWidth;
            params.setMargins(gap, gap, gap, gap);
            binding.botList.addView(row.getRoot(), params);
        }

        binding.cancelButton.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public interface OnBotSelectedListener {
        void onBotSelected(AppUserDTO bot);
    }
}
