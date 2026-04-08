package com.twb.pokerapp.ui.layout.texas;

import static com.twb.pokerapp.ui.util.ViewUtil.applyScaleRecursive;
import static com.twb.pokerapp.ui.util.ViewUtil.setInvisible;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.twb.pokerapp.R;
import com.twb.pokerapp.databinding.CardPairBinding;
import com.twb.pokerapp.data.model.dto.card.CardDTO;
import com.twb.pokerapp.data.model.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.ui.util.CardDrawableUtil;

public class CardPairLayout extends ConstraintLayout {
    private CardPairBinding binding;
    private PlayerSessionDTO playerSession;

    public CardPairLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardPairLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public CardPairLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        binding = CardPairBinding.inflate(LayoutInflater.from(context), this);
        setAttributes(context, attrs);
        reset();
        if (isInEditMode()) {
            binding.displayNameTextView.setText("Player 1");
            binding.fundsTextView.setText("$10000.00");
            updateCardImageView(R.drawable.sa);
            updateCardImageView(R.drawable.sk);
        }
    }

    private void setAttributes(Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) return;
        try (TypedArray cardPairLayoutAttributes = context.obtainStyledAttributes(attrs, R.styleable.CardPairLayout)) {
            var layoutScale = cardPairLayoutAttributes.getFloat(R.styleable.CardPairLayout_layout_scale, 1f);
            var textSizeScale = cardPairLayoutAttributes.getFloat(R.styleable.CardPairLayout_textsize_scale, 1f);
            applyScaleRecursive(this, layoutScale, textSizeScale);
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
        if (binding.leftCardImageView.getVisibility() != INVISIBLE
                && binding.rightCardImageView.getVisibility() != INVISIBLE) {
            reset();
        }
        if (binding.leftCardImageView.getVisibility() == INVISIBLE) {
            binding.leftCardImageView.setImageResource(cardDrawResId);
            binding.leftCardImageView.setVisibility(VISIBLE);
        } else if (binding.rightCardImageView.getVisibility() == INVISIBLE) {
            binding.rightCardImageView.setImageResource(cardDrawResId);
            binding.rightCardImageView.setVisibility(VISIBLE);
        }
    }

    public void updateDetails(PlayerSessionDTO playerSession) {
        this.playerSession = playerSession;

        var user = playerSession.getUser();
        binding.displayNameTextView.setText(user.getUsername());

        var funds = playerSession.getFunds();
        if (funds != null) {
            binding.fundsTextView.setText(getContext().getString(R.string.currency_format, funds));
        }
    }

    public void deleteDetails() {
        reset();
        var notConnectedText = getContext().getString(R.string.not_connected_text);
        binding.displayNameTextView.setText(notConnectedText);
        binding.fundsTextView.setText(notConnectedText);
    }

    public void updateDealerChip(boolean dealer) {
        var visibility = (dealer) ? VISIBLE : GONE;
        binding.dealerChipLayout.setVisibility(visibility);
    }

    public void updateTurnPlayer(boolean playerTurn) {
        if (playerTurn) {
            setBackgroundResource(R.drawable.player_turn_border);
        } else {
            setBackground(null);
        }
    }

    public void fold() {
        setInvisible(binding.leftCardImageView);
        setInvisible(binding.rightCardImageView);
    }

    public String getUsername() {
        if (playerSession != null) {
            return playerSession.getUser().getUsername();
        }
        return binding.displayNameTextView.getText().toString();
    }

    public PlayerSessionDTO getPlayerSession() {
        return playerSession;
    }
}
