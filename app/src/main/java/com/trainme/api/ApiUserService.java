package com.trainme.api;

import androidx.lifecycle.LiveData;

import com.trainme.api.model.Credentials;
import com.trainme.api.model.Token;
import com.trainme.api.model.User;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiUserService {
    @POST("users/login")
    LiveData<ApiResponse<Token>> login(@Body Credentials credentials);

    @POST("users/logout")
    LiveData<ApiResponse<Void>> logout();

    @GET("users/current")
    LiveData<ApiResponse<User>> getCurrentUser();

    @PUT("users/current")
    LiveData<ApiResponse<User>> editProfile(@Body User user);

    @GET("/users/{userId}")
    LiveData<ApiResponse<User>> getUser(@Path("userId") int userId);
}
