package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.service.eval.dto.EvalPlayerHandDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WinnerService {
    private final GameLogService gameLogService;

    public void handleWinners(GameThreadParams params, Round round, List<EvalPlayerHandDTO> winners) {
        if (winners.size() == 1) {
            handleSinglePlayerWin(params, round, winners.getFirst());
        } else {
            handleMultiplePlayerWin(params, round, winners);
        }
    }

    private void handleSinglePlayerWin(GameThreadParams params, Round round, EvalPlayerHandDTO winningPlayerHandDTO) {
        var playerSession = winningPlayerHandDTO.getPlayerSession();
        var username = playerSession.getUser().getUsername();
        var handTypeStr = winningPlayerHandDTO.getHandType().getValue();

        gameLogService.sendLogMessage(params.getTableId(), "%s wins round with a %s winning %.2f".formatted(username, handTypeStr, round.getPot()));
    }

    private void handleMultiplePlayerWin(GameThreadParams params, Round round, List<EvalPlayerHandDTO> winners) {
        var winnerNames = getReadableWinners(winners);
        var handTypeStr = winners.getFirst().getHandType().getValue();
        var splitPot = round.getPot() / winners.size();

        gameLogService.sendLogMessage(params.getTableId(), "%s draws round with a %s winning %.2f each".formatted(winnerNames, handTypeStr, splitPot));
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
