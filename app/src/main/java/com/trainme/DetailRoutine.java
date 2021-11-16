package com.trainme;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import  androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.trainme.api.model.Routine;
import com.trainme.databinding.ActivityDetailRoutineBinding;
import com.trainme.repository.Status;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class DetailRoutine extends AppCompatActivity {

    private ActivityDetailRoutineBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityDetailRoutineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        FrameLayout frame = binding.contentScrollingFragment.cyclesFrameLayout;

        if (savedInstanceState == null) {
            Fragment newFragment = new CycleFragment();
            FragmentTransaction ft =  getSupportFragmentManager().beginTransaction();
            ft.add(frame.getId(), newFragment).commit();
        }
        App app = (App) getApplication();
        int page = 0;
        AtomicInteger isFav = new AtomicInteger();
        app.getRoutineRepository().getFavourites(page, 100).observe(this, r -> {
            if (r.getStatus() == Status.SUCCESS){
                for (Routine routine : r.getData().getContent()) {
                    int id = routine.getId();
                    if (id == getIntent().getExtras().getInt("ID")) {
                        binding.FavBtn.setVisibility(View.INVISIBLE);
                        binding.UnFavBtn.setVisibility(View.VISIBLE);
                        isFav.set(1);
                        Log.d("fac", "no encontro en favs ");
                    }
                }
                if (isFav.get() == 0) {
                    Log.d("fac", "no encontro en favs ");
                    binding.FavBtn.setVisibility(View.VISIBLE);
                    binding.UnFavBtn.setVisibility(View.INVISIBLE);
                }
            }
        });

        binding.FavBtn.setOnClickListener(v -> {
            app.getRoutineRepository().addToFavs(getIntent().getExtras().getInt("ID")).observe(this, r -> {
                if (r.getStatus() == Status.SUCCESS) {
                    Log.d("FAVS", "onCreate: added to Favs");

                    binding.FavBtn.setVisibility(View.INVISIBLE);
                    binding.UnFavBtn.setVisibility(View.VISIBLE);
                }else if (r.getStatus() == Status.ERROR) {
                    Log.d("FAVS", "Favs Error");
                }
            });
        });

        binding.UnFavBtn.setOnClickListener(v -> {
            app.getRoutineRepository().removeFav(getIntent().getExtras().getInt("ID")).observe(this, r -> {
                if (r.getStatus() == Status.SUCCESS) {
                    Log.d("FAVS", "onCreate: removed from Favs");

                    binding.FavBtn.setVisibility(View.VISIBLE);
                    binding.UnFavBtn.setVisibility(View.INVISIBLE);
                } else if (r.getStatus() == Status.ERROR) {
                    Log.d("FAVS", "Favs Error");
                }
            });
        });

//        FloatingActionButton fab = binding.playRoutineBtn;
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(this, PlayRoutine.class);
//                startActivity(intent);
//            }
//        });
    }

    public void playRoutine(View view) {
        Intent intent = new Intent(this, PlayRoutine.class);
        Bundle b = getIntent().getExtras();

        intent.putExtra("ID", b.getInt("ID"));
        startActivity(intent);
    }
}