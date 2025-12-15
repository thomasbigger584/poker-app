package com.twb.pokerapp.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.twb.pokerapp.R;

public class AlertModalDialog extends DialogFragment {
    private static final String KEY_ALERT_MODAL_TYPE = "com.twb.pokerapp.AlertModalType";
    private static final String KEY_ALERT_SUBTITLE = "com.twb.pokerapp.Subtitle";
    private OnAlertClickListener listener;

    public static AlertModalDialog newInstance(
            AlertModalType alertModalType, String subtitle, OnAlertClickListener listener) {
        var fragment = new AlertModalDialog();
        fragment.setListener(listener);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        fragment.setCancelable(false);
        var bundle = new Bundle();
        bundle.putSerializable(KEY_ALERT_MODAL_TYPE, alertModalType);
        bundle.putString(KEY_ALERT_SUBTITLE, subtitle);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void setListener(OnAlertClickListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        var args = getArguments();
        if (args == null) {
            return null;
        }
        var inflatedView = inflater.inflate(R.layout.fragment_alert_dialog, container, false);

        var alertModalType = (AlertModalType) args.getSerializable(KEY_ALERT_MODAL_TYPE);

        if (alertModalType == null) {
            alertModalType = AlertModalType.INFO;
        }

        var iconImageView = (ImageView) inflatedView.findViewById(R.id.iconImageView);
        var titleTextView = (TextView) inflatedView.findViewById(R.id.titleTextView);

        var successButtonClickText = getString(R.string.confirm);
        var shouldShowCancelButton = false;

        switch (alertModalType) {
            case WARNING: {
                iconImageView.setImageResource(R.drawable.ic_warning);
                titleTextView.setText(R.string.warning);
                shouldShowCancelButton = true;
                break;
            }
            case CONFIRM: {
                iconImageView.setImageResource(R.drawable.ic_confirm);
                titleTextView.setText(R.string.are_you_sure);
                shouldShowCancelButton = true;
                break;
            }
            case INFO: {
                iconImageView.setImageResource(R.drawable.ic_info);
                titleTextView.setText(R.string.info);
                successButtonClickText = getString(R.string.ok_got_it);
                break;
            }
            case SUCCESS: {
                iconImageView.setImageResource(R.drawable.ic_success);
                titleTextView.setText(R.string.success);
                successButtonClickText = getString(R.string.close);
                break;
            }
            case ERROR: {
                iconImageView.setImageResource(R.drawable.ic_error);
                titleTextView.setText(R.string.error_occurred);
                successButtonClickText = getString(R.string.close);
                break;
            }
        }

        var subtitle = args.getString(KEY_ALERT_SUBTITLE);
        subtitle = capitalize(subtitle);
        var subtitleTextView = (TextView) inflatedView.findViewById(R.id.subtitleTextView);
        subtitleTextView.setText(subtitle);

        var cancelButton = inflatedView.findViewById(R.id.cancelButton);
        if (shouldShowCancelButton) {
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelClick();
                }
                dismissAllowingStateLoss();
            });
        } else {
            cancelButton.setVisibility(View.GONE);
        }

        var successButton = (Button) inflatedView.findViewById(R.id.successButton);
        successButton.setText(successButtonClickText);
        successButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSuccessClick();
            }
            dismissAllowingStateLoss();
        });

        return inflatedView;
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
