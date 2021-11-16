package com.trainme.api;

import androidx.lifecycle.LiveData;


import com.trainme.api.model.ContentEx;
import com.trainme.api.model.Cycle;
import com.trainme.api.model.Exercise;
import com.trainme.api.model.PagedList;
import com.trainme.api.model.Routine;

import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiRoutineService {

    @GET("routines/{routineId}")
    LiveData<ApiResponse<Routine>> getRoutine(@Path("routineId") int routineId);

    @GET("routines")
    LiveData<ApiResponse<PagedList<Routine>>> getRoutines(@Query("page") int page, @Query("size") int size, @Query("orderBy") String orderBy);

    @GET("favourites")
    LiveData<ApiResponse<PagedList<Routine>>> getFavourites(@Query("page") int page, @Query("size") int size);

    @DELETE("favourites/{routineId}")
    LiveData<ApiResponse<Void>> removeFav(@Path("routineId") int size);

    @POST("favourites/{routineId}")
    LiveData<ApiResponse<Void>>addToFavs(@Path("routineId") int routineId);

    @GET("users/current/routines")
    LiveData<ApiResponse<PagedList<Routine>>> getMyRoutines(@Query("page") int page, @Query("size") int size, @Query("orderBy") String orderBy);

    @GET("routines/{routineId}/cycles")
    LiveData<ApiResponse<PagedList<Cycle>>> getCycles(@Path("routineId") int routine, @Query("page") int page, @Query("size") int size, @Query("orderBy") String orderBy);

    @GET("cycles/{cycleId}/exercises")
    LiveData<ApiResponse<PagedList<ContentEx>>> getExercises(@Path("cycleId") int routine, @Query("page") int page, @Query("size") int size, @Query("orderBy") String orderBy);


}
