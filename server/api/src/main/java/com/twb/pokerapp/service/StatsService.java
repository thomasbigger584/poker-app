package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.PhysicalUser;
import com.twb.pokerapp.mapper.ProtoConvert;
import com.twb.pokerapp.proto.ActionType;
import com.twb.pokerapp.proto.HandType;
import com.twb.pokerapp.proto.PlayerStatsDTO;
import com.twb.pokerapp.repository.StatsRepository;
import com.twb.pokerapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Assembles a player's lifetime statistics from a set of read-only aggregate
 * queries (counts, sums, maxes, group-bys) over the gameplay tables. There is no
 * backing entity — this is a pure aggregation, so the proto message is built
 * here rather than by a model→DTO mapper.
 */
@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsRepository statsRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<PlayerStatsDTO> getCurrent(Principal principal) {
        if (principal == null) {
            return Optional.empty();
        }
        var username = principal.getName();
        var userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        var user = userOpt.get();

        var winnings = nz(statsRepository.sumWinnings(username));
        var biggestPot = nz(statsRepository.maxWinning(username));
        var wagered = nz(statsRepository.sumWagered(username));
        var buyIns = nz(statsRepository.sumBuyIns(username));
        var cashOuts = nz(statsRepository.sumCashOuts(username));
        var netTableProfit = cashOuts.subtract(buyIns);
        var currentFunds = (user instanceof PhysicalUser pu) ? nz(pu.getTotalFunds()) : BigDecimal.ZERO;

        long handsPlayed = statsRepository.countHandsPlayed(username);
        long roundsWon = statsRepository.countRoundsWon(username);
        long tablesJoined = statsRepository.countTablesJoined(username);
        double winRate = handsPlayed == 0 ? 0d : (double) roundsWon / (double) handsPlayed;

        var actions = actionCounts(username);
        long checks = actions.getOrDefault(ActionType.ACTION_TYPE_CHECK, 0L);
        long bets = actions.getOrDefault(ActionType.ACTION_TYPE_BET, 0L);
        long calls = actions.getOrDefault(ActionType.ACTION_TYPE_CALL, 0L);
        long raises = actions.getOrDefault(ActionType.ACTION_TYPE_RAISE, 0L);
        long folds = actions.getOrDefault(ActionType.ACTION_TYPE_FOLD, 0L);
        long allIns = actions.getOrDefault(ActionType.ACTION_TYPE_ALL_IN, 0L);
        // Classic aggression factor; when the player has never called, fall back
        // to the aggressive-action count so it isn't a divide-by-zero.
        double aggressionFactor =
                calls == 0 ? (double) (bets + raises) : (double) (bets + raises) / (double) calls;

        var builder = PlayerStatsDTO.newBuilder()
                .setCurrentFunds(ProtoConvert.money(currentFunds))
                .setTotalWinnings(ProtoConvert.money(winnings))
                .setBiggestPotWon(ProtoConvert.money(biggestPot))
                .setTotalWagered(ProtoConvert.money(wagered))
                .setTotalBuyIns(ProtoConvert.money(buyIns))
                .setTotalCashOuts(ProtoConvert.money(cashOuts))
                .setNetTableProfit(ProtoConvert.money(netTableProfit))
                .setHandsPlayed((int) handsPlayed)
                .setRoundsWon((int) roundsWon)
                .setWinRate(winRate)
                .setTablesJoined((int) tablesJoined)
                .setChecks((int) checks)
                .setBets((int) bets)
                .setCalls((int) calls)
                .setRaises((int) raises)
                .setFolds((int) folds)
                .setAllIns((int) allIns)
                .setAggressionFactor(aggressionFactor)
                .setBestHand(bestHand(username))
                .setFavoriteAction(favoriteAction(actions))
                .setMemberSince(ProtoConvert.dateTime(user.getCreatedDateTime()));
        return Optional.of(builder.build());
    }

    private Map<ActionType, Long> actionCounts(String username) {
        var counts = new EnumMap<ActionType, Long>(ActionType.class);
        for (Object[] row : statsRepository.actionBreakdown(username)) {
            counts.put((ActionType) row[0], (Long) row[1]);
        }
        return counts;
    }

    /** Best (lowest-numbered) real hand the player has made; royal flush = 1. */
    private HandType bestHand(String username) {
        return statsRepository.distinctHandTypes(username).stream()
                .filter(h -> h.getNumber() >= HandType.HAND_TYPE_ROYAL_FLUSH_VALUE
                        && h.getNumber() <= HandType.HAND_TYPE_HIGH_CARD_VALUE)
                .min(Comparator.comparingInt(HandType::getNumber))
                .orElse(HandType.HAND_TYPE_UNSPECIFIED);
    }

    /** Most-used action; UNSPECIFIED when the player has taken no actions. */
    private ActionType favoriteAction(Map<ActionType, Long> actions) {
        return actions.entrySet().stream()
                .filter(e -> e.getKey().getNumber() > 0)
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(ActionType.ACTION_TYPE_UNSPECIFIED);
    }

    private static BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
