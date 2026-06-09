package com.twb.pokerapp.ui.dialog.game;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
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
    // Landscape phones are short — never let a game dialog exceed this fraction of the screen.
    private static final float MAX_HEIGHT_FRACTION = 0.8f;

    @Override
    public void onStart() {
        super.onStart();
        var window = getWindow();
        if (window != null) {
            // Make the window itself transparent so the felt panel's rounded corners don't reveal
            // the default opaque (white) window background behind them.
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            var windowParams = window.getAttributes();
            windowParams.dimAmount = DIM_AMOUNT;
            windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(windowParams);
        }
        capHeight();
    }

    /**
     * Caps the dialog at {@link #MAX_HEIGHT_FRACTION} of the screen height. If the laid-out content
     * is taller (short device / long list), the window is pinned to the cap and the content's own
     * ScrollView takes over; otherwise it keeps wrapping its content. Dialog content should be
     * wrapped in a ScrollView for this to scroll gracefully.
     */
    private void capHeight() {
        var window = getWindow();
        var view = getView();
        if (window == null || view == null) {
            return;
        }
        view.post(() -> {
            var maxHeight = (int) (getResources().getDisplayMetrics().heightPixels * MAX_HEIGHT_FRACTION);
            var height = (view.getHeight() > maxHeight)
                    ? maxHeight
                    : ViewGroup.LayoutParams.WRAP_CONTENT;
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, height);
        });
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
