package com.trainme.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.trainme.App;
import com.trainme.CycleFragment;
import com.trainme.api.ApiClient;
import com.trainme.api.ApiResponse;
import com.trainme.api.ApiRoutineService;
import com.trainme.api.model.ContentEx;
import com.trainme.api.model.Cycle;
import com.trainme.api.model.Exercise;
import com.trainme.api.model.PagedList;
import com.trainme.api.model.Review;
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

    public LiveData<Resource<PagedList<Routine>>> getMyRoutines(int page, int size, String orderBy) {
        return new NetworkBoundResource<PagedList<Routine>, PagedList<Routine>>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<PagedList<Routine>>> createCall() {
                return apiService.getMyRoutines(page, size, orderBy);
            }
        }.asLiveData();
    }

    public LiveData<Resource<PagedList<Cycle>>> getCycles(int page, int size, String orderBy, int id) {
        return new NetworkBoundResource<PagedList<Cycle>, PagedList<Cycle>>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<PagedList<Cycle>>> createCall() {
                return apiService.getCycles(id, page, size, orderBy);
            }
        }.asLiveData();
    }

    public LiveData<Resource<PagedList<Routine>>> getFavourites(int page, int size) {
        return new NetworkBoundResource<PagedList<Routine>, PagedList<Routine>>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<PagedList<Routine>>> createCall() {
                return apiService.getFavourites(page, size);
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

    public LiveData<Resource<Void>> addToFavs(int routineId){
        return new NetworkBoundResource<Void, Void>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<Void>> createCall() {
                return apiService.addToFavs(routineId);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Void>> removeFav(int routineId){
        return new NetworkBoundResource<Void, Void>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<Void>> createCall() {
                return apiService.removeFav(routineId);
            }
        }.asLiveData();
    }

    public LiveData<Resource<PagedList<ContentEx>>> getExercises(int page, int size, String orderBy, int id) {
        return new NetworkBoundResource<PagedList<ContentEx>, PagedList<ContentEx>>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<PagedList<ContentEx>>> createCall() {
                return apiService.getExercises(id, page, size, orderBy);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Void>> addReview(int routineId, Review review){
        return new NetworkBoundResource<Void, Void>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<Void>> createCall() {
                return apiService.addReview(routineId, review );
            }
        }.asLiveData();
    }

}
