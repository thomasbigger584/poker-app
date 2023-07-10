package com.twb.pokergame.data.repository;

import com.twb.pokergame.data.model.Cryptocurrency;

import java.util.List;

public class CryptocurrencyRepositoryImpl implements CryptocurrencyRepository {

    @Override
    public List<Cryptocurrency> getCryptocurrency() {
        return List.of(
                new Cryptocurrency("BitCoin", "https://upload.wikimedia.org/wikipedia/commons/5/50/Bitcoin.png"),
                new Cryptocurrency("Ethereum", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/01/Ethereum_logo_translucent.svg/1200px-Ethereum_logo_translucent.svg.png"),
                new Cryptocurrency("Binance", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/12/Binance_logo.svg/1920px-Binance_logo.svg.png"),
                new Cryptocurrency("DogeCoin", "https://upload.wikimedia.org/wikipedia/en/d/d0/Dogecoin_Logo.png"),
                new Cryptocurrency("LiteCoin", "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/Litecoin_Logo.jpg/2048px-Litecoin_Logo.jpg"),
                new Cryptocurrency("Stellar", "https://upload.wikimedia.org/wikipedia/commons/5/56/Stellar_Symbol.png"),
                new Cryptocurrency("Polkadot", "https://upload.wikimedia.org/wikipedia/commons/5/59/Polkadot_Logotype_color.png"));
    }
}
