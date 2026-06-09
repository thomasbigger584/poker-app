package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.proto.PlayerSessionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlayerSessionMapper {
    private final UserMapper userMapper;
    private final TableMapper tableMapper;

    public PlayerSessionDTO modelToDto(PlayerSession model) {
        if (model == null) {
            return null;
        }
        var builder = PlayerSessionDTO.newBuilder()
                .setId(ProtoConvert.uuidStr(model.getId()))
                .setFunds(ProtoConvert.money(model.getFunds()))
                .setSessionState(ProtoConvert.toProto(model.getSessionState()))
                .setConnectionType(ProtoConvert.toProto(model.getConnectionType()));
        if (model.getUser() != null) {
            builder.setUser(userMapper.modelToDto(model.getUser()));
        }
        if (model.getPokerTable() != null) {
            builder.setPokerTable(tableMapper.modelToDto(model.getPokerTable()));
        }
        if (model.getPosition() != null) {
            builder.setPosition(model.getPosition());
        }
        if (model.getDealer() != null) {
            builder.setDealer(model.getDealer());
        }
        return builder.build();
    }
}
