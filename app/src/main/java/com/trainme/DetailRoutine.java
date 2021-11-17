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
import android.widget.RatingBar;

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

        Intent myIntent=getIntent();
        int routineId = myIntent.getExtras().getInt("ID");
        String routineName = myIntent.getExtras().getString("Name");

        String routineDetail = myIntent.getExtras().getString("Detail");
        String routineDifficulty = myIntent.getExtras().getString("Difficulty");
        int routineRating = myIntent.getExtras().getInt("Score");

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(routineName);



        binding.contentScrollingFragment.detailTextView.setText(routineDetail);

        binding.contentScrollingFragment.titleTextView.setText(routineName);
        binding.contentScrollingFragment.rating.setRating(routineRating);


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
                    if (id == routineId) {
                        binding.FavBtn.setVisibility(View.VISIBLE);
                        binding.UnFavBtn.setVisibility(View.INVISIBLE);
                        isFav.set(1);
                        Log.d("fac", "no encontro en favs ");
                    }
                }
                if (isFav.get() == 0) {
                    Log.d("fac", "no encontro en favs ");
                    binding.FavBtn.setVisibility(View.INVISIBLE);
                    binding.UnFavBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.FavBtn.setOnClickListener(v -> {
            app.getRoutineRepository().removeFav(routineId).observe(this, r -> {
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
            app.getRoutineRepository().addToFavs(routineId).observe(this, r -> {
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

    public void rateRoutine(View view){
        Intent intent=new Intent(this, RateActiviy.class);

        intent.putExtra("ID", getIntent().getExtras().getInt("ID"));

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int routineId=getIntent().getExtras().getInt("ID");
        App api= (App)getApplication();
        api.getRoutineRepository().getRoutine(routineId).observe(this,r -> {
            if (r.getStatus() == Status.SUCCESS) {
                Log.d("onRes", "refresh");
                int routineRating=r.getData().getScore();
                binding.contentScrollingFragment.rating.setRating(routineRating);

            } else if (r.getStatus() == Status.ERROR) {
                Log.d("onRes", " Error");
            }
        });
    }

    public void playRoutine(View view) {
        Intent intent = new Intent(this, PlayRoutine.class);
        Bundle bundle = getIntent().getExtras();

        intent.putExtra("ID", bundle.getInt("ID"));
        startActivity(intent);
    }
}