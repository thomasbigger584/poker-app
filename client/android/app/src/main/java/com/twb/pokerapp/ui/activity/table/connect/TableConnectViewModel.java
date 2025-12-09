package com.twb.pokerapp.ui.activity.table.connect;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.twb.pokerapp.data.repository.TableRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TableConnectViewModel extends ViewModel {
    public final LiveData<Throwable> errors;
    private final TableRepository repository;

    @Inject
    public TableConnectViewModel(TableRepository repository) {
        this.repository = repository;
        this.errors = repository.getErrors();
    }


}
