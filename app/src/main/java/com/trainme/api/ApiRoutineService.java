package com.trainme.api;

import androidx.lifecycle.LiveData;


import com.trainme.api.model.PagedList;
import com.trainme.api.model.Routine;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiRoutineService {

    @GET("routines/{routineId}")
    LiveData<ApiResponse<Routine>> getRoutine(@Path("routineId") int routineId);

    @GET("routines")
    LiveData<ApiResponse<PagedList<Routine>>> getRoutines(@Query("page") int page, @Query("size") int size, @Query("orderBy") String orderBy);
}
