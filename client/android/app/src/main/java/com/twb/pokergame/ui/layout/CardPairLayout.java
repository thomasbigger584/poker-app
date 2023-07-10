package com.twb.pokergame.ui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.twb.pokergame.R;
import com.twb.pokergame.data.model.Card;
import com.twb.pokergame.data.model.PokerPlayer;
import com.twb.pokergame.ui.util.CardDrawableUtil;

import java.util.Locale;

public class CardPairLayout extends FrameLayout {
    private final ImageView[] cardImageViews = new ImageView[2];
    private TextView displayNameTextView;
    private TextView fundsTextView;
    private FrameLayout dealerChipLayout;
    private View inflatedView;

    public CardPairLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardPairLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflatedView = inflate(getContext(), R.layout.card_pair, this);
        cardImageViews[0] = inflatedView.findViewById(R.id.leftCardImageView);
        cardImageViews[1] = inflatedView.findViewById(R.id.rightCardImageView);
        displayNameTextView = inflatedView.findViewById(R.id.displayNameTextView);
        fundsTextView = inflatedView.findViewById(R.id.fundsTextView);
        dealerChipLayout = inflatedView.findViewById(R.id.dealerChipLayout);
        reset();
    }

    public void reset() {
        for (ImageView imageView : cardImageViews) {
            imageView.setVisibility(INVISIBLE);
        }
        dealerChipLayout.setVisibility(GONE);
    }

    public void updateCardImageView(Card card) {
        int cardDrawResId = CardDrawableUtil.getDrawableResFromCard(getContext(), card);

        if (cardImageViews[0].getVisibility() != INVISIBLE && cardImageViews[1].getVisibility() != INVISIBLE) {
            reset();
        }
        if (cardImageViews[0].getVisibility() == INVISIBLE) {
            cardImageViews[0].setImageResource(cardDrawResId);
            cardImageViews[0].setVisibility(VISIBLE);
        } else if (cardImageViews[1].getVisibility() == INVISIBLE) {
            cardImageViews[1].setImageResource(cardDrawResId);
            cardImageViews[1].setVisibility(VISIBLE);
        }
    }

    public void updateDetails(PokerPlayer pokerPlayer) {
        displayNameTextView.setText(pokerPlayer.getUsername());
        fundsTextView.setText(String.format(Locale.getDefault(), "%.2f", pokerPlayer.getFunds()));
    }

    public void updateDealerChip(boolean dealer) {
        final int visibility = (dealer) ? VISIBLE : GONE;
        dealerChipLayout.setVisibility(visibility);
    }

    public void updateTurnPlayer(boolean playerTurn) {
        if (playerTurn) {
            inflatedView.setBackgroundResource(R.drawable.player_turn_border);
        } else {
            inflatedView.setBackground(null);
        }
    }

    public void fold() {
        for (ImageView cardImageView : cardImageViews) {
            cardImageView.setVisibility(INVISIBLE);
        }
    }
}
