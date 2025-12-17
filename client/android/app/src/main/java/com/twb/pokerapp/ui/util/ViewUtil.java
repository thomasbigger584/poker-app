package com.twb.pokerapp.ui.util;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewUtil {

    public static void setInvisible(View view) {
        if (view.getVisibility() != View.INVISIBLE) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public static void setGone(View view) {
        if (view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
        }
    }

    public static void setVisible(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void applyScaleRecursive(View view, float layoutScale, float textScale) {
        var layoutParams = view.getLayoutParams();
        if (layoutParams != null && layoutScale != 1f) {
            if (layoutParams.width > 0) {
                layoutParams.width = (int) (layoutParams.width * layoutScale);
            }
            if (layoutParams.height > 0) {
                layoutParams.height = (int) (layoutParams.height * layoutScale);
            }
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                var marginParams = (ViewGroup.MarginLayoutParams) layoutParams;
                marginParams.leftMargin = (int) (marginParams.leftMargin * layoutScale);
                marginParams.rightMargin = (int) (marginParams.rightMargin * layoutScale);
                marginParams.topMargin = (int) (marginParams.topMargin * layoutScale);
                marginParams.bottomMargin = (int) (marginParams.bottomMargin * layoutScale);
            }
            view.setLayoutParams(layoutParams);
        }
        if (textScale != 1f)
            if (view instanceof TextView) {
                var textView = (TextView) view;
                float textSizeInPx = textView.getTextSize();
                textView.setTextSize(COMPLEX_UNIT_PX, textSizeInPx * textScale);
            }
        if (view instanceof ViewGroup) {
            var viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                applyScaleRecursive(viewGroup.getChildAt(i), layoutScale, textScale);
            }
        }
    }
}
