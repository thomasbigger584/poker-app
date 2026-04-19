package com.twb.pokerapp.ui.activity.table.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.twb.pokerapp.data.model.dto.table.AvailableTableDTO;
import com.twb.pokerapp.data.model.dto.table.TableDTO;
import com.twb.pokerapp.data.repository.TableRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TableListViewModel extends ViewModel {
    private final TableRepository repository;

    public final LiveData<Throwable> errorLiveData;
    public final LiveData<List<AvailableTableDTO>> tablesLiveData;

    @Inject
    public TableListViewModel(TableRepository repository) {
        this.repository = repository;
        this.errorLiveData = repository.errorLiveData;
        this.tablesLiveData = repository.tablesLiveData;
    }

    public void refresh() {
        repository.refreshAvailableTables();
    }
}
