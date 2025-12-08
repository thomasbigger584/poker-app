package com.twb.pokerapp.ui.activity.table.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.twb.pokerapp.data.model.dto.table.TableDTO;
import com.twb.pokerapp.data.repository.TableRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TableListViewModel extends ViewModel {
    public final LiveData<Throwable> errors;
    private final TableRepository repository;

    @Inject
    public TableListViewModel(TableRepository repository) {
        this.repository = repository;
        this.errors = repository.getErrors();
    }

    public LiveData<List<TableDTO>> getPokerTables() {
        return repository.getTables();
    }
}
