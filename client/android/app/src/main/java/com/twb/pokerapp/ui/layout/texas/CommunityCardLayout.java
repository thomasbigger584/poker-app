package com.twb.pokerapp.ui.layout.texas;


import static com.twb.pokerapp.ui.util.ViewUtil.applyScaleRecursive;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.twb.pokerapp.R;
import com.twb.pokerapp.databinding.CommunityCardsBinding;
import com.twb.pokerapp.data.model.dto.card.CardDTO;

public class CommunityCardLayout extends LinearLayout {
    private static final String FLOP_CARD_1 = "FLOP_CARD_1";
    private static final String FLOP_CARD_2 = "FLOP_CARD_2";
    private static final String FLOP_CARD_3 = "FLOP_CARD_3";
    private static final String TURN_CARD = "TURN_CARD";
    private static final String RIVER_CARD = "RIVER_CARD";
    private CommunityCardsBinding binding;

    public CommunityCardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommunityCardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        binding = CommunityCardsBinding.inflate(LayoutInflater.from(context), this);
        setAttributes(context, attrs);
        if (!isInEditMode()) {
            setInvisible();
        }
    }

    private void setAttributes(Context context, AttributeSet attrs) {
        if (attrs == null) return;
        try (var communityCardLayoutAttributes = context.obtainStyledAttributes(attrs, R.styleable.CommunityCardLayout)) {
            var layoutScale = communityCardLayoutAttributes.getFloat(R.styleable.CommunityCardLayout_layout_scale, 1f);
            applyScaleRecursive(this, layoutScale, 1.0f);
        }
    }

    public void reset() {
        setInvisible();
        binding.community1CardLayout.reset();
        binding.community2CardLayout.reset();
        binding.community3CardLayout.reset();
        binding.community4CardLayout.reset();
        binding.community5CardLayout.reset();
    }

    public void dealCard(CardDTO card) {
        switch (card.getCardType()) {
            case FLOP_CARD_1: {
                setFlopVisibility();
                binding.community1CardLayout.update(card);
                break;
            }
            case FLOP_CARD_2: {
                binding.community2CardLayout.update(card);
                break;
            }
            case FLOP_CARD_3: {
                binding.community3CardLayout.update(card);
                break;
            }
            case TURN_CARD: {
                setTurnVisibility();
                binding.community4CardLayout.update(card);
                break;
            }
            case RIVER_CARD: {
                setRiverVisibility();
                binding.community5CardLayout.update(card);
                break;
            }
        }
    }

    public void setFlopVisibility() {
        binding.community1CardLayout.setVisibility(VISIBLE);
        binding.community2CardLayout.setVisibility(VISIBLE);
        binding.community3CardLayout.setVisibility(VISIBLE);
    }

    public void setTurnVisibility() {
        binding.community4CardLayout.setVisibility(VISIBLE);
    }

    public void setRiverVisibility() {
        binding.community5CardLayout.setVisibility(VISIBLE);
    }

    public void setInvisible() {
        binding.community1CardLayout.setVisibility(INVISIBLE);
        binding.community2CardLayout.setVisibility(INVISIBLE);
        binding.community3CardLayout.setVisibility(INVISIBLE);
        binding.community4CardLayout.setVisibility(INVISIBLE);
        binding.community5CardLayout.setVisibility(INVISIBLE);
    }
}
