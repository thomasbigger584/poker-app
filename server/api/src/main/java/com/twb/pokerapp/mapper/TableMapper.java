package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.proto.CreateTableDTO;
import com.twb.pokerapp.proto.TableDTO;
import org.springframework.stereotype.Component;

@Component
public class TableMapper {

    public TableDTO modelToDto(PokerTable model) {
        if (model == null) {
            return null;
        }
        var builder = TableDTO.newBuilder()
                .setId(ProtoConvert.uuidStr(model.getId()))
                .setName(ProtoConvert.text(model.getName()))
                .setMinBuyin(ProtoConvert.money(model.getMinBuyin()))
                .setMaxBuyin(ProtoConvert.money(model.getMaxBuyin()));
        if (model.getGameType() != null) {
            builder.setGameType(model.getGameType());
        }
        if (model.getSpeedMultiplier() != null) {
            builder.setSpeedMultiplier(model.getSpeedMultiplier());
        }
        if (model.getTotalRounds() != null) {
            builder.setTotalRounds(model.getTotalRounds());
        }
        if (model.getMinPlayers() != null) {
            builder.setMinPlayers(model.getMinPlayers());
        }
        if (model.getMaxPlayers() != null) {
            builder.setMaxPlayers(model.getMaxPlayers());
        }
        return builder.build();
    }

    public PokerTable createDtoToModel(CreateTableDTO dto) {
        if (dto == null) {
            return null;
        }
        var table = new PokerTable();
        table.setName(dto.getName());
        table.setGameType(dto.getGameType());
        if (dto.hasSpeedMultiplier()) {
            table.setSpeedMultiplier(dto.getSpeedMultiplier());
        }
        if (dto.hasTotalRounds()) {
            table.setTotalRounds(dto.getTotalRounds());
        }
        table.setMinPlayers(dto.getMinPlayers());
        table.setMaxPlayers(dto.getMaxPlayers());
        table.setMinBuyin(ProtoConvert.bigDecimal(dto.getMinBuyin()));
        table.setMaxBuyin(ProtoConvert.bigDecimal(dto.getMaxBuyin()));
        return table;
    }
}
