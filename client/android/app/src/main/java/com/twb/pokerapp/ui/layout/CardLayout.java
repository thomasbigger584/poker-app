package com.twb.pokerapp.ui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;

import com.twb.pokerapp.R;
import com.twb.pokerapp.databinding.CardBinding;
import com.twb.pokerapp.data.model.dto.card.CardDTO;
import com.twb.pokerapp.ui.util.CardDrawableUtil;

public class CardLayout extends FrameLayout {
    private CardBinding binding;

    public CardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        if (isInEditMode()) {
            binding.cardImageView.setImageResource(R.drawable.da);
            binding.cardImageView.setVisibility(VISIBLE);
        }
    }

    private void init() {
        binding = CardBinding.inflate(LayoutInflater.from(getContext()), this, true);
        reset();
    }

    public void update(CardDTO card) {
        var cardDrawResId = CardDrawableUtil.getDrawable(getContext(), card);
        binding.cardImageView.setImageResource(cardDrawResId);
        binding.cardImageView.setVisibility(VISIBLE);
    }

    public void reset() {
        binding.cardImageView.setImageDrawable(null);
        binding.cardImageView.setVisibility(INVISIBLE);
    }
}
