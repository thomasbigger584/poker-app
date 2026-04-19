package com.twb.pokerapp.ui.activity.table.connect;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.twb.pokerapp.R;
import com.twb.pokerapp.data.model.dto.table.TableDTO;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TableConnectViewModel extends ViewModel {

    private final MutableLiveData<Integer> _errorResId = new MutableLiveData<>();
    public final LiveData<Integer> errorResId = _errorResId;

    private final MutableLiveData<TableConnectEvent> _connectEvent = new MutableLiveData<>();
    public final LiveData<TableConnectEvent> connectEvent = _connectEvent;

    @Inject
    public TableConnectViewModel() {
    }

    public void onConnectTableClick(TableDTO table, int selectedRadioId, String buyInStr) {
        if (buyInStr == null || buyInStr.trim().isEmpty()) {
            _errorResId.setValue(R.string.error_blank_buy_in);
            return;
        }

        double buyIn;
        try {
            buyIn = Double.parseDouble(buyInStr.trim());
        } catch (NumberFormatException e) {
            _errorResId.setValue(R.string.error_blank_buy_in);
            return;
        }

        if (buyIn < table.getMinBuyin() || buyIn > table.getMaxBuyin()) {
            _errorResId.setValue(R.string.error_buy_in_range);
            return;
        }

        if (selectedRadioId == -1) {
            _errorResId.setValue(R.string.error_select_connection_type);
            return;
        }

        var connectionType = "PLAYER";
        if (selectedRadioId == R.id.radio_viewer) {
            connectionType = "LISTENER";
            buyIn = 0d;
        }

        if ("LISTENER".equals(connectionType)) {
            _errorResId.setValue(R.string.error_listener_unavailable);
            return;
        }

        if (!"TEXAS_HOLDEM".equals(table.getGameType())) {
            _errorResId.setValue(R.string.error_unsupported_game_type);
            return;
        }

        _connectEvent.setValue(new TableConnectEvent(table, connectionType, buyIn));
    }

    public static class TableConnectEvent {
        private final TableDTO table;
        private final String connectionType;
        private final Double buyInAmount;

        public TableConnectEvent(TableDTO table, String connectionType, Double buyInAmount) {
            this.table = table;
            this.connectionType = connectionType;
            this.buyInAmount = buyInAmount;
        }

        public TableDTO getTable() {
            return table;
        }

        public String getConnectionType() {
            return connectionType;
        }

        public Double getBuyInAmount() {
            return buyInAmount;
        }
    }
}
