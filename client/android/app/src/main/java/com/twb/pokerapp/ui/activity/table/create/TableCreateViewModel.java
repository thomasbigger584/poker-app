package com.twb.pokerapp.ui.activity.table.create;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.twb.pokerapp.data.model.dto.table.CreateTableDTO;
import com.twb.pokerapp.data.model.dto.table.TableDTO;
import com.twb.pokerapp.data.repository.TableRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TableCreateViewModel extends ViewModel {
    public final LiveData<Throwable> errors;
    public final MutableLiveData<TableDTO> createdTableLiveData;
    private final TableRepository repository;

    @Inject
    public TableCreateViewModel(TableRepository repository) {
        this.repository = repository;
        this.errors = repository.getErrors();
        this.createdTableLiveData = repository.getCreatedTableLiveData();
    }

    public void createTable(CreateTableDTO createTableDTO) {
        repository.createTable(createTableDTO);
    }
}
