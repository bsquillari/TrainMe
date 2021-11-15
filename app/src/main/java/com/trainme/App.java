package com.trainme;

import android.app.Application;

import com.trainme.repository.RoutineRepository;
import com.trainme.repository.SportRepository;
import com.trainme.repository.UserRepository;

//import ar.edu.itba.example.retrofit.repository.SportRepository;
//import ar.edu.itba.example.retrofit.repository.UserRepository;

public class App extends Application {

    private AppPreferences preferences;
    private UserRepository userRepository;
    private SportRepository sportRepository;
    private RoutineRepository routineRepository;

    public AppPreferences getPreferences() { return preferences; }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public SportRepository getSportRepository() {
        return sportRepository;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = new AppPreferences(this);

        userRepository = new UserRepository(this);

        sportRepository = new SportRepository(this);

        routineRepository = new RoutineRepository(this);
    }

    public RoutineRepository getRoutineRepository() {
        return routineRepository;
    }
}
