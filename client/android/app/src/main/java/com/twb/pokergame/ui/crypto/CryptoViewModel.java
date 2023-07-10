package com.twb.pokergame.ui.crypto;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.twb.pokergame.data.model.Cryptocurrency;
import com.twb.pokergame.data.repository.CryptocurrencyRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CryptoViewModel extends ViewModel {
    public final MutableLiveData<List<Cryptocurrency>> cryptoLiveData = new MutableLiveData<>();
    private final CryptocurrencyRepository repository;

    @Inject
    public CryptoViewModel(CryptocurrencyRepository repository) {
        this.repository = repository;
        loadCryptocurrency();
    }

    private void loadCryptocurrency() {
        List<Cryptocurrency> cryptocurrency = repository.getCryptocurrency();
        cryptoLiveData.postValue(cryptocurrency);
    }
}
