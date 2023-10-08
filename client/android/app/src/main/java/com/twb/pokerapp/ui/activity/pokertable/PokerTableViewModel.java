package com.twb.pokerapp.ui.activity.pokertable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.twb.pokerapp.data.model.dto.pokertable.TableDTO;
import com.twb.pokerapp.data.repository.PokerTableRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PokerTableViewModel extends ViewModel {
    public final LiveData<Throwable> errors;
    private final PokerTableRepository repository;

    @Inject
    public PokerTableViewModel(PokerTableRepository repository) {
        this.repository = repository;
        this.errors = repository.getErrors();
    }

    public LiveData<List<TableDTO>> getPokerTables() {
        return repository.getPokerTables();
    }
}
