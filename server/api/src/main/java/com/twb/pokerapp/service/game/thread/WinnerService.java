package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.service.eval.dto.EvalPlayerHandDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WinnerService {
    private final GameLogService gameLogService;

    public void handleWinners(PokerTable table, List<EvalPlayerHandDTO> winners) {
        if (winners.size() == 1) {
            handleSinglePlayerWin(table, winners.getFirst());
        } else {
            handleMultiplePlayerWin(table, winners);
        }
    }

    private void handleSinglePlayerWin(PokerTable table, EvalPlayerHandDTO winningPlayerHandDTO) {
        var playerSession = winningPlayerHandDTO.getPlayerSession();
        var username = playerSession.getUser().getUsername();
        var handTypeStr = winningPlayerHandDTO.getHandType().getValue();

        gameLogService.sendLogMessage(table, "%s wins round with a %s".formatted(username, handTypeStr));
    }

    private void handleMultiplePlayerWin(PokerTable table, List<EvalPlayerHandDTO> winners) {
        var winnerNames = getReadableWinners(winners);
        var handTypeStr = winners.getFirst().getHandType().getValue();

        gameLogService.sendLogMessage(table, "%s draws round with a %s".formatted(winnerNames, handTypeStr));
    }

    private String getReadableWinners(List<EvalPlayerHandDTO> winners) {
        var sb = new StringBuilder();
        for (var index = 0; index < winners.size(); index++) {
            var eval = winners.get(index);
            var user = eval.getPlayerSession().getUser();
            sb.append(user.getUsername());
            if (index < winners.size() - 3) {
                sb.append(", ");
            } else if (index == winners.size() - 2) {
                sb.append(" & ");
            }
        }
        return sb.toString();
    }
}
