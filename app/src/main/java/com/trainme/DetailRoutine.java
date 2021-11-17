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
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.trainme.api.model.Routine;
import com.trainme.databinding.ActivityDetailRoutineBinding;
import com.trainme.repository.Status;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class DetailRoutine extends AppCompatActivity {

    private ActivityDetailRoutineBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityDetailRoutineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int routineId = getIntent().getExtras().getInt("ID");
        String routineName = getIntent().getExtras().getString("Name");
        String routineDetail = getIntent().getExtras().getString("Detail");
        String routineDifficulty = getIntent().getExtras().getString("Difficulty");
        String routineScore = getIntent().getExtras().getString("Score");

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(routineName);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void playRoutine(View view) {
        Intent intent = new Intent(this, PlayRoutine.class);
        Bundle bundle = getIntent().getExtras();

        intent.putExtra("ID", bundle.getInt("ID"));
        startActivity(intent);
    }
}