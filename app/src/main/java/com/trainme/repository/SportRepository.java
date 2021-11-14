package com.trainme.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.trainme.App;
import com.trainme.api.ApiClient;
import com.trainme.api.ApiResponse;
import com.trainme.api.ApiSportService;
import com.trainme.api.model.PagedList;
import com.trainme.api.model.Sport;

//import ar.edu.itba.example.retrofit.App;
//import ar.edu.itba.example.retrofit.api.ApiClient;
//import ar.edu.itba.example.retrofit.api.ApiResponse;
//import ar.edu.itba.example.retrofit.api.ApiSportService;
//import ar.edu.itba.example.retrofit.api.model.PagedList;
//import ar.edu.itba.example.retrofit.api.model.Sport;

public class SportRepository {

    private final ApiSportService apiService;

    public SportRepository(App application) {
        this.apiService = ApiClient.create(application, ApiSportService.class);
    }

    public LiveData<Resource<PagedList<Sport>>> getSports() {
        return new NetworkBoundResource<PagedList<Sport>, PagedList<Sport>>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<PagedList<Sport>>> createCall() {
                return apiService.getSports();
            }
        }.asLiveData();
    }

    public LiveData<Resource<Sport>> getSport(int sportId) {
        return new NetworkBoundResource<Sport, Sport>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<Sport>> createCall() {
                return apiService.getSport(sportId);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Sport>> addSport(Sport sport) {
        return new NetworkBoundResource<Sport, Sport>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<Sport>> createCall() {
                return apiService.addSport(sport);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Sport>> modifySport(Sport sport) {
        return new NetworkBoundResource<Sport, Sport>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<Sport>> createCall() {
                return apiService.modifySport(sport.getId(), sport);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Void>> deleteSport(int sportId) {
        return new NetworkBoundResource<Void, Void>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<Void>> createCall() {
                return apiService.deleteSport(sportId);
            }
        }.asLiveData();
    }
}
