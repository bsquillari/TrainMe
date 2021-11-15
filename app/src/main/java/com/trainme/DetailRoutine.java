package com.trainme;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.trainme.databinding.ActivityDetailRoutineBinding;
import com.trainme.repository.Status;

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
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(frame.getId(), newFragment).commit();
        }

        binding.FavBtn.setOnClickListener(v -> {
            App app = (App) getApplication();
            app.getRoutineRepository().addToFavs(getIntent().getExtras().getInt("ID")).observe(this, r ->{
                if (r.getStatus() == Status.SUCCESS){
                    Log.d("FAVS", "onCreate: added to Favs");

//                    binding.FavBtn.setBackgroundColor(R.color.teal_200);
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

    public void playRoutine(View view){
        Intent intent = new Intent(this, PlayRoutine.class);
        Bundle b=getIntent().getExtras();

        intent.putExtra("ID",b.getInt("ID") );
        startActivity(intent);
    }
}