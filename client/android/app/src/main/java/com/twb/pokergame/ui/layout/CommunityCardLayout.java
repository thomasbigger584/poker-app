package com.twb.pokergame.ui.layout;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.twb.pokergame.R;
import com.twb.pokergame.data.model.Card;
import com.twb.pokergame.data.model.enumeration.CommunityCardType;

public class CommunityCardLayout extends FrameLayout {
    private CardLayout community1CardLayout;
    private CardLayout community2CardLayout;
    private CardLayout community3CardLayout;
    private CardLayout community4CardLayout;
    private CardLayout community5CardLayout;

    public CommunityCardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public CommunityCardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View inflatedView = inflate(getContext(), R.layout.community_cards, this);
        community1CardLayout = inflatedView.findViewById(R.id.community1CardLayout);
        community2CardLayout = inflatedView.findViewById(R.id.community2CardLayout);
        community3CardLayout = inflatedView.findViewById(R.id.community3CardLayout);
        community4CardLayout = inflatedView.findViewById(R.id.community4CardLayout);
        community5CardLayout = inflatedView.findViewById(R.id.community5CardLayout);
        setInvisible();
    }

    public void reset() {
        setInvisible();
        community1CardLayout.reset();
        community2CardLayout.reset();
        community3CardLayout.reset();
        community4CardLayout.reset();
        community5CardLayout.reset();
    }

    public void dealCard(final Card card, CommunityCardType cardType) {
        switch (cardType) {
            case FLOP_1: {
                setFlopVisibility();
                community1CardLayout.update(card);
                break;
            }
            case FLOP_2: {
                community2CardLayout.update(card);
                break;
            }
            case FLOP_3: {
                community3CardLayout.update(card);
                break;
            }
            case RIVER: {
                setRiverVisibility();
                community4CardLayout.update(card);
                break;
            }
            case TURN: {
                setTurnVisibility();
                community5CardLayout.update(card);
            }
        }
    }

    public void setFlopVisibility() {
        community1CardLayout.setVisibility(VISIBLE);
        community2CardLayout.setVisibility(VISIBLE);
        community3CardLayout.setVisibility(VISIBLE);
    }

    public void setRiverVisibility() {
        community4CardLayout.setVisibility(VISIBLE);

    }

    public void setTurnVisibility() {
        community5CardLayout.setVisibility(VISIBLE);
    }

    public void setInvisible() {
        this.community1CardLayout.setVisibility(INVISIBLE);
        this.community2CardLayout.setVisibility(INVISIBLE);
        this.community3CardLayout.setVisibility(INVISIBLE);
        this.community4CardLayout.setVisibility(INVISIBLE);
        this.community5CardLayout.setVisibility(INVISIBLE);
    }
}