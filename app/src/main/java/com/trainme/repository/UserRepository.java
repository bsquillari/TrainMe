package com.trainme.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.trainme.App;
import com.trainme.api.ApiUserService;
import com.trainme.api.ApiClient;
import com.trainme.api.ApiResponse;
import com.trainme.api.model.User;
import com.trainme.api.model.Credentials;
import com.trainme.api.model.Token;;


//import ar.edu.itba.example.retrofit.App;
//import ar.edu.itba.example.retrofit.api.ApiClient;
//import ar.edu.itba.example.retrofit.api.ApiResponse;
//import ar.edu.itba.example.retrofit.api.ApiUserService;
//import ar.edu.itba.example.retrofit.api.model.Credentials;
//import ar.edu.itba.example.retrofit.api.model.Token;
//import ar.edu.itba.example.retrofit.api.model.User;

public class UserRepository {

    private final ApiUserService apiService;

    public UserRepository(App app) {
        this.apiService = ApiClient.create(app, ApiUserService.class);
    }

    public LiveData<Resource<Token>> login(Credentials credentials) {
        return new NetworkBoundResource<Token, Token>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<Token>> createCall() {
                return apiService.login(credentials);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Void>> logout() {
        return new NetworkBoundResource<Void, Void>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<Void>> createCall() {
                return apiService.logout();
            }
        }.asLiveData();
    }

    public LiveData<Resource<User>> getCurrentUser() {
        return new NetworkBoundResource<User, User>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<User>> createCall() {
                return apiService.getCurrentUser();
            }
        }.asLiveData();
    }

    public LiveData<Resource<User>> getUser(int userID){
        return new NetworkBoundResource<User, User>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<User>> createCall() {
                return apiService.getUser(userID);
            }
        }.asLiveData();
    }
}