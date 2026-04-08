package com.twb.pokerapp.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.twb.pokerapp.R;
import com.twb.pokerapp.databinding.FragmentAlertDialogBinding;

public class AlertModalDialog extends DialogFragment {
    private static final String KEY_ALERT_MODAL_TYPE = "com.twb.pokerapp.AlertModalType";
    private static final String KEY_ALERT_SUBTITLE = "com.twb.pokerapp.Subtitle";
    private OnAlertClickListener listener;
    private FragmentAlertDialogBinding binding;

    public static AlertModalDialog newInstance(
            AlertModalType alertModalType, String subtitle, OnAlertClickListener listener) {
        var fragment = new AlertModalDialog();
        fragment.listener = listener;
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        fragment.setCancelable(false);
        var bundle = new Bundle();
        bundle.putSerializable(KEY_ALERT_MODAL_TYPE, alertModalType);
        bundle.putString(KEY_ALERT_SUBTITLE, subtitle);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAlertDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        var args = getArguments();
        if (args == null) return;

        var alertModalType = (AlertModalType) args.getSerializable(KEY_ALERT_MODAL_TYPE);
        if (alertModalType == null) {
            alertModalType = AlertModalType.INFO;
        }

        var successButtonClickText = getString(R.string.confirm);
        var shouldShowCancelButton = false;

        switch (alertModalType) {
            case WARNING: {
                binding.iconImageView.setImageResource(R.drawable.ic_warning);
                binding.titleTextView.setText(R.string.warning);
                shouldShowCancelButton = true;
                break;
            }
            case CONFIRM: {
                binding.iconImageView.setImageResource(R.drawable.ic_confirm);
                binding.titleTextView.setText(R.string.are_you_sure);
                shouldShowCancelButton = true;
                break;
            }
            case INFO: {
                binding.iconImageView.setImageResource(R.drawable.ic_info);
                binding.titleTextView.setText(R.string.info);
                successButtonClickText = getString(R.string.ok_got_it);
                break;
            }
            case SUCCESS: {
                binding.iconImageView.setImageResource(R.drawable.ic_success);
                binding.titleTextView.setText(R.string.success);
                successButtonClickText = getString(R.string.close);
                break;
            }
            case ERROR: {
                binding.iconImageView.setImageResource(R.drawable.ic_error);
                binding.titleTextView.setText(R.string.error_occurred);
                successButtonClickText = getString(R.string.close);
                break;
            }
        }

        var subtitle = args.getString(KEY_ALERT_SUBTITLE);
        subtitle = capitalize(subtitle);
        binding.subtitleTextView.setText(subtitle);

        if (shouldShowCancelButton) {
            binding.cancelButton.setVisibility(View.VISIBLE);
            binding.cancelButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelClick();
                }
                dismiss();
            });
        } else {
            binding.cancelButton.setVisibility(View.GONE);
        }

        binding.successButton.setText(successButtonClickText);
        binding.successButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSuccessClick();
            }
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private String capitalize(String string) {
        if (string == null || string.isEmpty()) return string;
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public enum AlertModalType {
        WARNING, CONFIRM, INFO, SUCCESS, ERROR
    }

    public interface OnAlertClickListener {
        void onSuccessClick();

        void onCancelClick();
    }
}
