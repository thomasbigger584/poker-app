// SoundController.java
package com.twb.pokerapp.util;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;

public class SoundController {
    private SoundPool soundPool;
    private int cardFlickSound;

    public SoundController() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

        cardFlickSound = soundPool.load("card_flick.mp3", 1);
    }

    public void playCardFlick() {
        soundPool.play(cardFlickSound, 1, 1, 0, 0, 1);
    }

    public void release() {
        soundPool.release();
    }
}