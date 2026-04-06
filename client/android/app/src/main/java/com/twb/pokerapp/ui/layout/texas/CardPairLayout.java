package com.twb.pokerapp.ui.layout.texas;

import static com.twb.pokerapp.ui.util.ViewUtil.applyScaleRecursive;
import static com.twb.pokerapp.ui.util.ViewUtil.setInvisible;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.twb.pokerapp.R;
import com.twb.pokerapp.data.model.dto.card.CardDTO;
import com.twb.pokerapp.data.model.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.ui.util.CardDrawableUtil;

public class CardPairLayout extends ConstraintLayout {
    private final ImageView[] cardImageViews = new ImageView[2];
    private TextView displayNameTextView;
    private TextView fundsTextView;
    private View dealerChipLayout;
    private View inflatedView;
    private PlayerSessionDTO playerSession;

    public CardPairLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardPairLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public CardPairLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        inflatedView = inflate(getContext(), R.layout.card_pair, this);
        cardImageViews[0] = inflatedView.findViewById(R.id.leftCardImageView);
        cardImageViews[1] = inflatedView.findViewById(R.id.rightCardImageView);
        displayNameTextView = inflatedView.findViewById(R.id.displayNameTextView);
        fundsTextView = inflatedView.findViewById(R.id.fundsTextView);
        dealerChipLayout = inflatedView.findViewById(R.id.dealerChipLayout);
        setAttributes(context, attrs);
        reset();
        if (isInEditMode()) {
            displayNameTextView.setText("Player 1");
            fundsTextView.setText("$10000.00");
            updateCardImageView(R.drawable.sa);
            updateCardImageView(R.drawable.sk);
        }
    }

    private void setAttributes(Context context, AttributeSet attrs) {
        if (attrs == null) return;
        try (TypedArray cardPairLayoutAttributes = context.obtainStyledAttributes(attrs, R.styleable.CardPairLayout)) {
            var layoutScale = cardPairLayoutAttributes.getFloat(R.styleable.CardPairLayout_layout_scale, 1f);
            var textSizeScale = cardPairLayoutAttributes.getFloat(R.styleable.CardPairLayout_textsize_scale, 1f);
            applyScaleRecursive(inflatedView, layoutScale, textSizeScale);
        }
    }

    public void reset() {
        fold();
        updateDealerChip(false);
        updateTurnPlayer(false);
    }

    public void updateCardImageView(CardDTO card) {
        var cardDrawResId = CardDrawableUtil.getDrawable(getContext(), card);
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
        this.playerSession = playerSession;

        var user = playerSession.getUser();
        displayNameTextView.setText(user.getUsername());

        var funds = playerSession.getFunds();
        if (funds != null) {
            fundsTextView.setText(getContext().getString(R.string.currency_format, funds));
        }
    }

    public void deleteDetails() {
        reset();
        var notConnectedText = getContext().getString(R.string.not_connected_text);
        displayNameTextView.setText(notConnectedText);
        fundsTextView.setText(notConnectedText);
    }

    public void updateDealerChip(boolean dealer) {
        var visibility = (dealer) ? VISIBLE : GONE;
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
        for (var cardImageView : cardImageViews) {
            setInvisible(cardImageView);
        }
    }

    public String getUsername() {
        if (playerSession != null) {
            return playerSession.getUser().getUsername();
        }
        return displayNameTextView.getText().toString();
    }

    public PlayerSessionDTO getPlayerSession() {
        return playerSession;
    }
}
