package com.twb.pokerapp.ui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.twb.pokerapp.R;
import com.twb.pokerapp.data.model.dto.card.CardDTO;
import com.twb.pokerapp.ui.util.CardDrawableUtil;


public class CardLayout extends FrameLayout {
    private ImageView cardImageView;

    public CardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        var inflatedView = inflate(getContext(), R.layout.card, this);
        cardImageView = inflatedView.findViewById(R.id.cardImageView);
        reset();
    }

    public void update(CardDTO card) {
        post(() -> {
            var cardDrawResId = CardDrawableUtil.getDrawable(getContext(), card);
            cardImageView.setImageResource(cardDrawResId);
            cardImageView.setVisibility(VISIBLE);
        });
    }

    public void reset() {
        post(() -> {
            cardImageView.setImageDrawable(null);
            cardImageView.setVisibility(INVISIBLE);
        });
    }
}
