package com.twb.pokergame.web.rest.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HandRankRequest {
    private List<CardRequest> cardList = new ArrayList<>();
}
