package com.twb.pokergame.ui.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.DrawableRes;

import com.twb.pokergame.data.model.Card;

public class CardDrawableUtil {
    private static final String DEF_TYPE = "drawable";

    @DrawableRes
    //required to dynamically get card from drawables
    @SuppressLint("DiscouragedApi")
    public static int getDrawableResFromCard(Context context, Card card) {
        String cardDrawRes = card.getDrawable();
        Resources resources = context.getResources();
        return resources.getIdentifier(cardDrawRes, DEF_TYPE, context.getPackageName());
    }
}
