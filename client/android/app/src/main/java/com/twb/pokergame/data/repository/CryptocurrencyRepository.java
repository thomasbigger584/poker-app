package com.twb.pokergame.data.repository;

import com.twb.pokergame.data.model.Cryptocurrency;

import java.util.List;

public interface CryptocurrencyRepository {
    List<Cryptocurrency> getCryptocurrency();
}
