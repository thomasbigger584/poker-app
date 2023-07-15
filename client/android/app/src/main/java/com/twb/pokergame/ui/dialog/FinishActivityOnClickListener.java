package com.twb.pokergame.ui.dialog;

import android.app.Activity;

public class FinishActivityOnClickListener implements AlertModalDialog.OnAlertClickListener {
    private final Activity activity;

    public FinishActivityOnClickListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onSuccessClick() {
        activity.finish();
    }

    @Override
    public void onCancelClick() {
        activity.finish();
    }
}
