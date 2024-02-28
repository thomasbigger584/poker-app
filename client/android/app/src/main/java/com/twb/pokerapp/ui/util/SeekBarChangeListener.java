package com.twb.pokerapp.ui.util;

import android.widget.SeekBar;

public abstract class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        onProgressChanged(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //left blank intentionally
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //left blank intentionally
    }

    public abstract void onProgressChanged(int progress);
}
