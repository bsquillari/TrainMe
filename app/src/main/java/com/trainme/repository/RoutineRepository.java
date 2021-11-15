package com.trainme.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.trainme.App;
import com.trainme.api.ApiClient;
import com.trainme.api.ApiResponse;
import com.trainme.api.ApiRoutineService;
import com.trainme.api.model.PagedList;
import com.trainme.api.model.Routine;


public class RoutineRepository {

    private final ApiRoutineService apiService;

    public RoutineRepository(App app) {
        this.apiService = ApiClient.create(app, ApiRoutineService.class);
    }

    public LiveData<Resource<PagedList<Routine>>> getRoutines(int page, int size, String orderBy) {
        return new NetworkBoundResource<PagedList<Routine>, PagedList<Routine>>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<PagedList<Routine>>> createCall() {
                return apiService.getRoutines(page, size, orderBy);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Routine>> getRoutine(int routineId) {
        return new NetworkBoundResource<Routine, Routine>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<Routine>> createCall() {
                return apiService.getRoutine(routineId);
            }
        }.asLiveData();
    }
}
