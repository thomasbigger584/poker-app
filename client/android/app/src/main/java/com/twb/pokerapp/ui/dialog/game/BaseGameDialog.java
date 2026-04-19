package com.twb.pokerapp.ui.dialog.game;

import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public abstract class BaseGameDialog extends DialogFragment {
    private static final float DIM_AMOUNT = 0.5f;

    @Override
    public void onStart() {
        super.onStart();
        var window = getWindow();
        if (window != null) {
            var windowParams = window.getAttributes();
            windowParams.dimAmount = DIM_AMOUNT;
            windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(windowParams);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setFullScreen();
    }

    protected void setFullScreen() {
        if (getView() == null) return;
        var window = getWindow();
        if (window != null) {
            var controller = new WindowInsetsControllerCompat(window, getView());
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        if (manager.isStateSaved()) return;
        super.show(manager, tag);
    }

    @Nullable
    private Window getWindow() {
        return getDialog() != null ? getDialog().getWindow() : null;
    }
}
