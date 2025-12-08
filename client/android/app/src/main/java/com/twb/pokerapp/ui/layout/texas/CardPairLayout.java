package com.twb.pokerapp.ui.layout.texas;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.twb.pokerapp.R;
import com.twb.pokerapp.data.model.dto.appuser.AppUserDTO;
import com.twb.pokerapp.data.model.dto.card.CardDTO;
import com.twb.pokerapp.data.model.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.ui.util.CardDrawableUtil;

import java.util.Locale;

public class CardPairLayout extends FrameLayout {
    private static final String PLAYER_NOT_CONNECTED_TEXT = "--";
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
        updateDealerChip(false);
    }

    public void updateCardImageView(CardDTO card) {
        int cardDrawResId = CardDrawableUtil.getDrawable(getContext(), card);
        updateCardImageView(cardDrawResId);
    }

    public void updateCardImageView() {
        updateCardImageView(R.drawable.back);
    }

    private void updateCardImageView(@DrawableRes int cardDrawResId) {
        if (cardImageViews[0].getVisibility() != INVISIBLE
                && cardImageViews[1].getVisibility() != INVISIBLE) {
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

    public void updateDetails(PlayerSessionDTO playerSession) {
        AppUserDTO user = playerSession.getUser();
        displayNameTextView.setText(user.getUsername());
        if (playerSession.getFunds() != null) {
            fundsTextView.setText(String.format(Locale.getDefault(), "%.2f", playerSession.getFunds()));
        }
    }

    public void deleteDetails() {
        reset();
        displayNameTextView.setText(PLAYER_NOT_CONNECTED_TEXT);
        fundsTextView.setText(PLAYER_NOT_CONNECTED_TEXT);
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

    public String getUsername() {
        return displayNameTextView.getText().toString();
    }
}
