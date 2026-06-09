package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.enumeration.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Central conversion between the behaviour-rich Java domain enums / scalar types and the generated
 * protobuf wire enums / scalar encodings. Kept in one place so the prefixed proto enum value names
 * (e.g. {@code GAME_TYPE_TEXAS_HOLDEM}) never leak into mapper or service code.
 *
 * <p>Scalar conventions on the wire: UUID/BigDecimal/char/LocalDateTime are all encoded as strings;
 * an absent value is the empty string (proto3 string default).
 */
public final class ProtoConvert {

    private ProtoConvert() {
    }

    // ---------------------------------------------------------------
    // Scalars
    // ---------------------------------------------------------------

    public static String uuidStr(UUID value) {
        return value == null ? "" : value.toString();
    }

    public static UUID uuid(String value) {
        return (value == null || value.isEmpty()) ? null : UUID.fromString(value);
    }

    public static String money(BigDecimal value) {
        return value == null ? "" : value.toPlainString();
    }

    public static BigDecimal bigDecimal(String value) {
        return (value == null || value.isEmpty()) ? null : new BigDecimal(value);
    }

    public static String ch(char value) {
        return String.valueOf(value);
    }

    /** Null-safe text: protobuf string setters reject null, so coalesce to the proto3 default "". */
    public static String text(String value) {
        return value == null ? "" : value;
    }

    public static String dateTime(LocalDateTime value) {
        return value == null ? "" : value.toString();
    }

    public static LocalDateTime dateTime(String value) {
        return (value == null || value.isEmpty()) ? null : LocalDateTime.parse(value);
    }

    // ---------------------------------------------------------------
    // Enums — Java -> proto
    // ---------------------------------------------------------------

    public static com.twb.pokerapp.proto.GameType toProto(GameType v) {
        if (v == null) return com.twb.pokerapp.proto.GameType.GAME_TYPE_UNSPECIFIED;
        return switch (v) {
            case TEXAS_HOLDEM -> com.twb.pokerapp.proto.GameType.GAME_TYPE_TEXAS_HOLDEM;
            case BLACKJACK -> com.twb.pokerapp.proto.GameType.GAME_TYPE_BLACKJACK;
        };
    }

    public static com.twb.pokerapp.proto.ConnectionType toProto(ConnectionType v) {
        if (v == null) return com.twb.pokerapp.proto.ConnectionType.CONNECTION_TYPE_UNSPECIFIED;
        return switch (v) {
            case PLAYER -> com.twb.pokerapp.proto.ConnectionType.CONNECTION_TYPE_PLAYER;
            case LISTENER -> com.twb.pokerapp.proto.ConnectionType.CONNECTION_TYPE_LISTENER;
        };
    }

    public static com.twb.pokerapp.proto.SessionState toProto(SessionState v) {
        if (v == null) return com.twb.pokerapp.proto.SessionState.SESSION_STATE_UNSPECIFIED;
        return switch (v) {
            case CONNECTED -> com.twb.pokerapp.proto.SessionState.SESSION_STATE_CONNECTED;
            case DISCONNECTED -> com.twb.pokerapp.proto.SessionState.SESSION_STATE_DISCONNECTED;
        };
    }

    public static com.twb.pokerapp.proto.TransactionHistoryType toProto(TransactionHistoryType v) {
        if (v == null) return com.twb.pokerapp.proto.TransactionHistoryType.TRANSACTION_HISTORY_TYPE_UNSPECIFIED;
        return switch (v) {
            case DEPOSIT -> com.twb.pokerapp.proto.TransactionHistoryType.TRANSACTION_HISTORY_TYPE_DEPOSIT;
            case WITHDRAW -> com.twb.pokerapp.proto.TransactionHistoryType.TRANSACTION_HISTORY_TYPE_WITHDRAW;
            case BUYIN -> com.twb.pokerapp.proto.TransactionHistoryType.TRANSACTION_HISTORY_TYPE_BUYIN;
            case CASHOUT -> com.twb.pokerapp.proto.TransactionHistoryType.TRANSACTION_HISTORY_TYPE_CASHOUT;
            case RESET -> com.twb.pokerapp.proto.TransactionHistoryType.TRANSACTION_HISTORY_TYPE_RESET;
        };
    }

    public static com.twb.pokerapp.proto.CardType toProto(CardType v) {
        if (v == null) return com.twb.pokerapp.proto.CardType.CARD_TYPE_UNSPECIFIED;
        return switch (v) {
            case PLAYER_CARD_1 -> com.twb.pokerapp.proto.CardType.CARD_TYPE_PLAYER_CARD_1;
            case PLAYER_CARD_2 -> com.twb.pokerapp.proto.CardType.CARD_TYPE_PLAYER_CARD_2;
            case FLOP_CARD_1 -> com.twb.pokerapp.proto.CardType.CARD_TYPE_FLOP_CARD_1;
            case FLOP_CARD_2 -> com.twb.pokerapp.proto.CardType.CARD_TYPE_FLOP_CARD_2;
            case FLOP_CARD_3 -> com.twb.pokerapp.proto.CardType.CARD_TYPE_FLOP_CARD_3;
            case TURN_CARD -> com.twb.pokerapp.proto.CardType.CARD_TYPE_TURN_CARD;
            case RIVER_CARD -> com.twb.pokerapp.proto.CardType.CARD_TYPE_RIVER_CARD;
        };
    }

    public static com.twb.pokerapp.proto.RankType toProto(RankType v) {
        if (v == null) return com.twb.pokerapp.proto.RankType.RANK_TYPE_UNSPECIFIED;
        return switch (v) {
            case DEUCE -> com.twb.pokerapp.proto.RankType.RANK_TYPE_DEUCE;
            case TREY -> com.twb.pokerapp.proto.RankType.RANK_TYPE_TREY;
            case FOUR -> com.twb.pokerapp.proto.RankType.RANK_TYPE_FOUR;
            case FIVE -> com.twb.pokerapp.proto.RankType.RANK_TYPE_FIVE;
            case SIX -> com.twb.pokerapp.proto.RankType.RANK_TYPE_SIX;
            case SEVEN -> com.twb.pokerapp.proto.RankType.RANK_TYPE_SEVEN;
            case EIGHT -> com.twb.pokerapp.proto.RankType.RANK_TYPE_EIGHT;
            case NINE -> com.twb.pokerapp.proto.RankType.RANK_TYPE_NINE;
            case TEN -> com.twb.pokerapp.proto.RankType.RANK_TYPE_TEN;
            case JACK -> com.twb.pokerapp.proto.RankType.RANK_TYPE_JACK;
            case QUEEN -> com.twb.pokerapp.proto.RankType.RANK_TYPE_QUEEN;
            case KING -> com.twb.pokerapp.proto.RankType.RANK_TYPE_KING;
            case ACE -> com.twb.pokerapp.proto.RankType.RANK_TYPE_ACE;
        };
    }

    public static com.twb.pokerapp.proto.SuitType toProto(SuitType v) {
        if (v == null) return com.twb.pokerapp.proto.SuitType.SUIT_TYPE_UNSPECIFIED;
        return switch (v) {
            case CLUBS -> com.twb.pokerapp.proto.SuitType.SUIT_TYPE_CLUBS;
            case DIAMONDS -> com.twb.pokerapp.proto.SuitType.SUIT_TYPE_DIAMONDS;
            case HEARTS -> com.twb.pokerapp.proto.SuitType.SUIT_TYPE_HEARTS;
            case SPADES -> com.twb.pokerapp.proto.SuitType.SUIT_TYPE_SPADES;
        };
    }

    public static com.twb.pokerapp.proto.HandType toProto(HandType v) {
        if (v == null) return com.twb.pokerapp.proto.HandType.HAND_TYPE_UNSPECIFIED;
        return switch (v) {
            case ROYAL_FLUSH -> com.twb.pokerapp.proto.HandType.HAND_TYPE_ROYAL_FLUSH;
            case STRAIGHT_FLUSH -> com.twb.pokerapp.proto.HandType.HAND_TYPE_STRAIGHT_FLUSH;
            case FOUR_OF_A_KIND -> com.twb.pokerapp.proto.HandType.HAND_TYPE_FOUR_OF_A_KIND;
            case FULL_HOUSE -> com.twb.pokerapp.proto.HandType.HAND_TYPE_FULL_HOUSE;
            case FLUSH -> com.twb.pokerapp.proto.HandType.HAND_TYPE_FLUSH;
            case STRAIGHT -> com.twb.pokerapp.proto.HandType.HAND_TYPE_STRAIGHT;
            case THREE_OF_A_KIND -> com.twb.pokerapp.proto.HandType.HAND_TYPE_THREE_OF_A_KIND;
            case TWO_PAIR -> com.twb.pokerapp.proto.HandType.HAND_TYPE_TWO_PAIR;
            case PAIR -> com.twb.pokerapp.proto.HandType.HAND_TYPE_PAIR;
            case HIGH_CARD -> com.twb.pokerapp.proto.HandType.HAND_TYPE_HIGH_CARD;
            case EMPTY_HAND -> com.twb.pokerapp.proto.HandType.HAND_TYPE_EMPTY_HAND;
        };
    }

    public static com.twb.pokerapp.proto.BettingRoundType toProto(BettingRoundType v) {
        if (v == null) return com.twb.pokerapp.proto.BettingRoundType.BETTING_ROUND_TYPE_UNSPECIFIED;
        return switch (v) {
            case DEAL -> com.twb.pokerapp.proto.BettingRoundType.BETTING_ROUND_TYPE_DEAL;
            case FLOP -> com.twb.pokerapp.proto.BettingRoundType.BETTING_ROUND_TYPE_FLOP;
            case TURN -> com.twb.pokerapp.proto.BettingRoundType.BETTING_ROUND_TYPE_TURN;
            case RIVER -> com.twb.pokerapp.proto.BettingRoundType.BETTING_ROUND_TYPE_RIVER;
        };
    }

    public static com.twb.pokerapp.proto.BettingRoundState toProto(BettingRoundState v) {
        if (v == null) return com.twb.pokerapp.proto.BettingRoundState.BETTING_ROUND_STATE_UNSPECIFIED;
        return switch (v) {
            case IN_PROGRESS -> com.twb.pokerapp.proto.BettingRoundState.BETTING_ROUND_STATE_IN_PROGRESS;
            case FINISHED -> com.twb.pokerapp.proto.BettingRoundState.BETTING_ROUND_STATE_FINISHED;
            case FAILED -> com.twb.pokerapp.proto.BettingRoundState.BETTING_ROUND_STATE_FAILED;
        };
    }

    public static com.twb.pokerapp.proto.RoundState toProto(RoundState v) {
        if (v == null) return com.twb.pokerapp.proto.RoundState.ROUND_STATE_UNSPECIFIED;
        return switch (v) {
            case WAITING_FOR_PLAYERS -> com.twb.pokerapp.proto.RoundState.ROUND_STATE_WAITING_FOR_PLAYERS;
            case INIT_DEAL -> com.twb.pokerapp.proto.RoundState.ROUND_STATE_INIT_DEAL;
            case INIT_DEAL_BET -> com.twb.pokerapp.proto.RoundState.ROUND_STATE_INIT_DEAL_BET;
            case FLOP_DEAL -> com.twb.pokerapp.proto.RoundState.ROUND_STATE_FLOP_DEAL;
            case FLOP_DEAL_BET -> com.twb.pokerapp.proto.RoundState.ROUND_STATE_FLOP_DEAL_BET;
            case TURN_DEAL -> com.twb.pokerapp.proto.RoundState.ROUND_STATE_TURN_DEAL;
            case TURN_DEAL_BET -> com.twb.pokerapp.proto.RoundState.ROUND_STATE_TURN_DEAL_BET;
            case RIVER_DEAL -> com.twb.pokerapp.proto.RoundState.ROUND_STATE_RIVER_DEAL;
            case RIVER_DEAL_BET -> com.twb.pokerapp.proto.RoundState.ROUND_STATE_RIVER_DEAL_BET;
            case EVAL -> com.twb.pokerapp.proto.RoundState.ROUND_STATE_EVAL;
            case FINISHED -> com.twb.pokerapp.proto.RoundState.ROUND_STATE_FINISHED;
            case FAILED -> com.twb.pokerapp.proto.RoundState.ROUND_STATE_FAILED;
        };
    }

    public static com.twb.pokerapp.proto.ActionType toProto(ActionType v) {
        if (v == null) return com.twb.pokerapp.proto.ActionType.ACTION_TYPE_UNSPECIFIED;
        return switch (v) {
            case CHECK -> com.twb.pokerapp.proto.ActionType.ACTION_TYPE_CHECK;
            case BET -> com.twb.pokerapp.proto.ActionType.ACTION_TYPE_BET;
            case CALL -> com.twb.pokerapp.proto.ActionType.ACTION_TYPE_CALL;
            case RAISE -> com.twb.pokerapp.proto.ActionType.ACTION_TYPE_RAISE;
            case FOLD -> com.twb.pokerapp.proto.ActionType.ACTION_TYPE_FOLD;
            case ALL_IN -> com.twb.pokerapp.proto.ActionType.ACTION_TYPE_ALL_IN;
        };
    }

    // ---------------------------------------------------------------
    // Enums — proto -> Java (UNSPECIFIED / UNRECOGNIZED -> null)
    // ---------------------------------------------------------------

    public static ActionType toModel(com.twb.pokerapp.proto.ActionType v) {
        if (v == null) return null;
        return switch (v) {
            case ACTION_TYPE_CHECK -> ActionType.CHECK;
            case ACTION_TYPE_BET -> ActionType.BET;
            case ACTION_TYPE_CALL -> ActionType.CALL;
            case ACTION_TYPE_RAISE -> ActionType.RAISE;
            case ACTION_TYPE_FOLD -> ActionType.FOLD;
            case ACTION_TYPE_ALL_IN -> ActionType.ALL_IN;
            case ACTION_TYPE_UNSPECIFIED, UNRECOGNIZED -> null;
        };
    }

    public static GameType toModel(com.twb.pokerapp.proto.GameType v) {
        if (v == null) return null;
        return switch (v) {
            case GAME_TYPE_TEXAS_HOLDEM -> GameType.TEXAS_HOLDEM;
            case GAME_TYPE_BLACKJACK -> GameType.BLACKJACK;
            case GAME_TYPE_UNSPECIFIED, UNRECOGNIZED -> null;
        };
    }

    public static ConnectionType toModel(com.twb.pokerapp.proto.ConnectionType v) {
        if (v == null) return null;
        return switch (v) {
            case CONNECTION_TYPE_PLAYER -> ConnectionType.PLAYER;
            case CONNECTION_TYPE_LISTENER -> ConnectionType.LISTENER;
            case CONNECTION_TYPE_UNSPECIFIED, UNRECOGNIZED -> null;
        };
    }

    public static SessionState toModel(com.twb.pokerapp.proto.SessionState v) {
        if (v == null) return null;
        return switch (v) {
            case SESSION_STATE_CONNECTED -> SessionState.CONNECTED;
            case SESSION_STATE_DISCONNECTED -> SessionState.DISCONNECTED;
            case SESSION_STATE_UNSPECIFIED, UNRECOGNIZED -> null;
        };
    }

    public static CardType toModel(com.twb.pokerapp.proto.CardType v) {
        if (v == null) return null;
        return switch (v) {
            case CARD_TYPE_PLAYER_CARD_1 -> CardType.PLAYER_CARD_1;
            case CARD_TYPE_PLAYER_CARD_2 -> CardType.PLAYER_CARD_2;
            case CARD_TYPE_FLOP_CARD_1 -> CardType.FLOP_CARD_1;
            case CARD_TYPE_FLOP_CARD_2 -> CardType.FLOP_CARD_2;
            case CARD_TYPE_FLOP_CARD_3 -> CardType.FLOP_CARD_3;
            case CARD_TYPE_TURN_CARD -> CardType.TURN_CARD;
            case CARD_TYPE_RIVER_CARD -> CardType.RIVER_CARD;
            case CARD_TYPE_UNSPECIFIED, UNRECOGNIZED -> null;
        };
    }

    public static RankType toModel(com.twb.pokerapp.proto.RankType v) {
        if (v == null) return null;
        return switch (v) {
            case RANK_TYPE_DEUCE -> RankType.DEUCE;
            case RANK_TYPE_TREY -> RankType.TREY;
            case RANK_TYPE_FOUR -> RankType.FOUR;
            case RANK_TYPE_FIVE -> RankType.FIVE;
            case RANK_TYPE_SIX -> RankType.SIX;
            case RANK_TYPE_SEVEN -> RankType.SEVEN;
            case RANK_TYPE_EIGHT -> RankType.EIGHT;
            case RANK_TYPE_NINE -> RankType.NINE;
            case RANK_TYPE_TEN -> RankType.TEN;
            case RANK_TYPE_JACK -> RankType.JACK;
            case RANK_TYPE_QUEEN -> RankType.QUEEN;
            case RANK_TYPE_KING -> RankType.KING;
            case RANK_TYPE_ACE -> RankType.ACE;
            case RANK_TYPE_UNSPECIFIED, UNRECOGNIZED -> null;
        };
    }

    public static SuitType toModel(com.twb.pokerapp.proto.SuitType v) {
        if (v == null) return null;
        return switch (v) {
            case SUIT_TYPE_CLUBS -> SuitType.CLUBS;
            case SUIT_TYPE_DIAMONDS -> SuitType.DIAMONDS;
            case SUIT_TYPE_HEARTS -> SuitType.HEARTS;
            case SUIT_TYPE_SPADES -> SuitType.SPADES;
            case SUIT_TYPE_UNSPECIFIED, UNRECOGNIZED -> null;
        };
    }

    public static BettingRoundType toModel(com.twb.pokerapp.proto.BettingRoundType v) {
        if (v == null) return null;
        return switch (v) {
            case BETTING_ROUND_TYPE_DEAL -> BettingRoundType.DEAL;
            case BETTING_ROUND_TYPE_FLOP -> BettingRoundType.FLOP;
            case BETTING_ROUND_TYPE_TURN -> BettingRoundType.TURN;
            case BETTING_ROUND_TYPE_RIVER -> BettingRoundType.RIVER;
            case BETTING_ROUND_TYPE_UNSPECIFIED, UNRECOGNIZED -> null;
        };
    }
}
