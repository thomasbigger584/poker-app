package com.twb.pokerapp.ui.activity.table.create;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.twb.pokerapp.R;
import com.twb.pokerapp.proto.CreateTableDTO;
import com.twb.pokerapp.proto.TableDTO;
import com.twb.pokerapp.data.repository.TableRepository;
import com.twb.pokerapp.util.Protos;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TableCreateViewModel extends ViewModel {
    private final TableRepository repository;

    private final MutableLiveData<Integer> _validationErrorResId = new MutableLiveData<>();
    public final LiveData<Integer> validationErrorResId = _validationErrorResId;

    public final LiveData<Throwable> errors;
    public final LiveData<TableDTO> createdTableLiveData;

    @Inject
    public TableCreateViewModel(TableRepository repository) {
        this.repository = repository;
        this.errors = repository.errorLiveData;
        this.createdTableLiveData = repository.createTableLiveData;
    }

    public void clearError() {
        repository.clearError();
    }

    public void validateAndCreate(String name,
                                  String gameType,
                                  String speedMultiplierStr,
                                  String totalRoundsStr,
                                  String minPlayersStr,
                                  String maxPlayersStr,
                                  String minBuyInStr,
                                  String maxBuyInStr) {

        if (name == null || name.isBlank()) {
            _validationErrorResId.setValue(R.string.error_blank_table_name);
            return;
        }

        double speedMultiplier;
        try {
            speedMultiplier = Double.parseDouble(speedMultiplierStr.trim());
        } catch (NumberFormatException e) {
            _validationErrorResId.setValue(R.string.error_invalid_speed_multiplier);
            return;
        }

        Integer totalRounds = null;
        try {
            if (totalRoundsStr != null && !totalRoundsStr.isBlank()) {
                totalRounds = Integer.parseInt(totalRoundsStr.trim());
            }
        } catch (NumberFormatException e) {
            _validationErrorResId.setValue(R.string.error_invalid_total_rounds);
            return;
        }

        int minPlayers;
        try {
            minPlayers = Integer.parseInt(minPlayersStr.trim());
        } catch (NumberFormatException e) {
            _validationErrorResId.setValue(R.string.error_invalid_min_players);
            return;
        }

        int maxPlayers;
        try {
            maxPlayers = Integer.parseInt(maxPlayersStr.trim());
        } catch (NumberFormatException e) {
            _validationErrorResId.setValue(R.string.error_invalid_max_players);
            return;
        }

        double minBuyIn;
        try {
            minBuyIn = Double.parseDouble(minBuyInStr.trim());
        } catch (NumberFormatException e) {
            _validationErrorResId.setValue(R.string.error_invalid_min_buyin);
            return;
        }

        double maxBuyIn;
        try {
            maxBuyIn = Double.parseDouble(maxBuyInStr.trim());
        } catch (NumberFormatException e) {
            _validationErrorResId.setValue(R.string.error_invalid_max_buyin);
            return;
        }

        var builder = CreateTableDTO.newBuilder()
                .setName(name)
                .setGameType(Protos.gameType(gameType))
                .setSpeedMultiplier(speedMultiplier)
                .setMinPlayers(minPlayers)
                .setMaxPlayers(maxPlayers)
                .setMinBuyin(Protos.moneyStr(minBuyIn))
                .setMaxBuyin(Protos.moneyStr(maxBuyIn));
        if (totalRounds != null) {
            builder.setTotalRounds(totalRounds);
        }

        repository.createTable(builder.build());
    }
}
