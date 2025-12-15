package com.twb.pokerapp.ui.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.DrawableRes;

import com.twb.pokerapp.data.model.dto.card.CardDTO;

public class CardDrawableUtil {
    private static final String DEF_TYPE = "drawable";

    @DrawableRes
    //required to dynamically get card from drawables
    @SuppressLint("DiscouragedApi")
    public static int getDrawable(Context context, CardDTO card) {
        var cardDrawRes = getDrawable(card);
        var resources = context.getResources();
        return resources.getIdentifier(cardDrawRes, DEF_TYPE, context.getPackageName());
    }

    private static String getDrawable(CardDTO card) {
        var suitChar = card.getSuitChar();
        var rankChar = card.getRankChar();
        var drawableName = String.valueOf(suitChar) + rankChar;
        return drawableName.toLowerCase();
    }
}
