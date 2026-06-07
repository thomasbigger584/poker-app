package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.domain.AppUser;
import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.enumeration.CardType;
import com.twb.pokerapp.domain.enumeration.GameType;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.dto.table.CreateTableDTO;
import com.twb.pokerapp.dto.table.TableDTO;
import com.twb.pokerapp.testutils.TestEnvironment;
import com.twb.pokerapp.testutils.game.GameLatches;
import com.twb.pokerapp.testutils.game.player.TestUserParams;
import com.twb.pokerapp.testutils.game.player.impl.TestGameListenerUser;
import com.twb.pokerapp.testutils.game.player.impl.TestTexasHoldemPlayerUser;
import com.twb.pokerapp.testutils.game.turn.impl.OptimisticTurnHandler;
import com.twb.pokerapp.testutils.keycloak.KeycloakClients;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validates that bot players can connect to a running game and play a hand to completion.
 * <p>
 * A single human player (driven by {@link OptimisticTurnHandler}, so it bets/calls) starts the
 * game; two seeded bots then join the running table via {@code sendBotConnected}. The table's
 * min-player count is set so the round cannot start — and the betting rounds cannot complete —
 * unless the bots both join and take their own turns. Reaching a FINISHED round therefore proves
 * the bots fully played the hand.
 */
@Slf4j
class TexasBotGameIT {
    private static final TestEnvironment env = new TestEnvironment();

    private static final boolean FIXED_SCENARIO = false;
    private static final String HUMAN_USERNAME = "user1";
    private static final List<String> BOT_USERNAMES = List.of("stone_cold", "sticky_stan");
    private static final BigDecimal BUY_IN = BigDecimal.valueOf(5_000);
    private static final long BOT_CONNECT_STAGGER_MS = 500L;
    private static final int GAME_TIMEOUT_SECS = 180;

    // *****************************************************************************************
    // Lifecycle Methods
    // *****************************************************************************************

    @BeforeAll
    static void beforeAll() {
        env.start(FIXED_SCENARIO);
    }

    @AfterEach
    void afterEach() {
        env.afterEach();
    }

    @AfterAll
    static void afterAll() {
        env.close();
    }

    // *****************************************************************************************
    // Test Methods
    // *****************************************************************************************

    @Test
    void botsPlayFullHandToCompletion() throws Exception {
        // given
        var sqlClient = env.getSqlClient();
        var keycloakClients = env.getKeycloakClients();

        var bots = BOT_USERNAMES.stream()
                .map(username -> sqlClient.getAppUserByUsername(username)
                        .orElseThrow(() -> new IllegalStateException("Seeded bot user not found: " + username)))
                .toList();

        // The human needs total funds to buy in; bots intentionally do not.
        sqlClient.updateUsersTotalFunds(HUMAN_USERNAME, BUY_IN);

        var minPlayers = 1 + bots.size();
        var table = createTable(minPlayers);

        var latches = GameLatches.create();

        var listener = new TestGameListenerUser(TestUserParams.builder()
                .table(table)
                .keycloak(keycloakClients.getViewerKeycloak())
                .username(KeycloakClients.VIEWER_USERNAME)
                .latches(latches)
                .build());

        var human = new TestTexasHoldemPlayerUser(TestUserParams.builder()
                .table(table)
                .keycloak(keycloakClients.get(HUMAN_USERNAME))
                .username(HUMAN_USERNAME)
                .latches(latches)
                .turnHandler(new OptimisticTurnHandler())
                .build());

        // when
        listener.connect();
        human.connect(BUY_IN);

        // The human's connection started the game thread; connect the bots to the running game.
        for (var bot : bots) {
            Thread.sleep(BOT_CONNECT_STAGGER_MS);
            log.debug("Connecting bot {} to table {}", bot.getUsername(), table.getId());
            human.sendBotConnected(bot.getId(), BUY_IN);
        }

        var finished = latches.gameLatch().await(GAME_TIMEOUT_SECS, TimeUnit.SECONDS);

        human.disconnect();
        listener.disconnect();

        // then
        assertNull(human.getExceptionThrown().get(), "Human player encountered an error during the game");
        assertNull(listener.getExceptionThrown().get(), "Listener encountered an error during the game");
        assertTrue(finished, "Game did not finish within " + GAME_TIMEOUT_SECS + " seconds");

        var rounds = sqlClient.getRounds();
        assertFalse(rounds.isEmpty(), "Expected at least one round to be played");
        assertTrue(rounds.stream().anyMatch(round -> round.getRoundState() == RoundState.FINISHED),
                "Expected a round to reach the FINISHED state");

        // The round can only progress if the bots took their turns, so actions must have been recorded.
        assertFalse(sqlClient.getPlayerActions().isEmpty(), "Expected player actions to have been recorded");

        // The bots were seated as players on the table.
        var connectedBotIds = bots.stream().map(AppUser::getId).toList();
        var seatedBotSessions = sqlClient.getPlayerSessions().stream()
                .filter(session -> session.getUser() != null && connectedBotIds.contains(session.getUser().getId()))
                .toList();
        assertEquals(bots.size(), seatedBotSessions.size(), "Expected both bots to have been seated at the table");

        // Each seated bot must have been dealt its two hole cards (PLAYER_CARD_1 + PLAYER_CARD_2),
        // proving the bots were dealt into the hand rather than merely seated.
        var hands = sqlClient.getHands();
        for (var botSession : seatedBotSessions) {
            var botUsername = botSession.getUser().getUsername();
            var botHands = hands.stream()
                    .filter(hand -> hand.getPlayerSession() != null
                            && botSession.getId().equals(hand.getPlayerSession().getId()))
                    .toList();
            assertFalse(botHands.isEmpty(), "Expected bot " + botUsername + " to have been dealt a hand");
            for (var hand : botHands) {
                var holeCardTypes = hand.getCards().stream()
                        .map(Card::getCardType)
                        .collect(Collectors.toSet());
                assertEquals(Set.of(CardType.PLAYER_CARD_1, CardType.PLAYER_CARD_2), holeCardTypes,
                        "Expected bot " + botUsername + " to be dealt exactly two hole cards");
            }
        }
    }

    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private TableDTO createTable(int minPlayers) throws Exception {
        var createDto = new CreateTableDTO();
        createDto.setName(UUID.randomUUID().toString());
        createDto.setGameType(GameType.TEXAS_HOLDEM);
        createDto.setSpeedMultiplier(2.0);
        createDto.setTotalRounds(1);
        createDto.setMinPlayers(minPlayers);
        createDto.setMaxPlayers(6);
        createDto.setMinBuyin(BigDecimal.valueOf(1_000));
        createDto.setMaxBuyin(BigDecimal.valueOf(10_000));

        var response = env.getAdminRestClient().post(TableDTO.class, createDto, "/poker-table");
        assertEquals(HttpStatus.CREATED.value(), response.httpResponse().statusCode());
        return response.resultBody();
    }
}
