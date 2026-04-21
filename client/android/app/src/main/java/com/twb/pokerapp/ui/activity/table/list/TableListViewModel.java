package com.twb.pokerapp.ui.activity.table.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.twb.pokerapp.data.model.dto.appuser.AppUserDTO;
import com.twb.pokerapp.data.model.dto.table.AvailableTableDTO;
import com.twb.pokerapp.data.repository.AppUserRepository;
import com.twb.pokerapp.data.repository.RepositoryCallback;
import com.twb.pokerapp.data.repository.TableRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TableListViewModel extends ViewModel {
    private final TableRepository repository;
    private final AppUserRepository appUserRepository;

    public final LiveData<Throwable> errorLiveData;
    public final LiveData<List<AvailableTableDTO>> tablesLiveData;
    public final MutableLiveData<AppUserDTO> userLiveData = new MutableLiveData<>();

    @Inject
    public TableListViewModel(TableRepository repository, AppUserRepository appUserRepository) {
        this.repository = repository;
        this.appUserRepository = appUserRepository;
        this.errorLiveData = repository.errorLiveData;
        this.tablesLiveData = repository.tablesLiveData;
    }

    public void refresh() {
        repository.refreshAvailableTables();
        appUserRepository.getCurrentUser(new RepositoryCallback<>() {
            @Override
            public void onSuccess(AppUserDTO result) {
                userLiveData.setValue(result);
            }

            @Override
            public void onFailure(Throwable t) {
                // error handled by repository live data
            }
        });
    }

    public void resetFunds(RepositoryCallback<AppUserDTO> callback) {
        appUserRepository.resetFunds(new RepositoryCallback<>() {
            @Override
            public void onSuccess(AppUserDTO result) {
                userLiveData.setValue(result);
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }
}
