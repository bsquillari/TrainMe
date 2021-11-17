package com.trainme;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import com.trainme.api.model.Routine;
import com.trainme.databinding.ActivityDetailRoutineBinding;
import com.trainme.repository.Status;
import java.util.Objects;

public class DetailRoutineActivity extends AppCompatActivity {

    private ActivityDetailRoutineBinding binding;
    private int routineId;
    private String routineName;
    private boolean routineFavorite;
    private String routineDetail;
    private String routineDifficulty;
    private int routineScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailRoutineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(getIntent().getAction() != null) {
            routineId = Integer.parseInt(getIntent().getData().getQueryParameter("id"));
            routineName = getIntent().getData().getQueryParameter("name");
        } else {
            routineId = getIntent().getExtras().getInt("ID");
            routineName = getIntent().getExtras().getString("Name");
            routineDetail = getIntent().getExtras().getString("Detail");
            routineDifficulty = getIntent().getExtras().getString("Difficulty");
            routineScore = getIntent().getExtras().getInt("Score");
        }

        binding.contentScrollingFragment.detailTextView.setText(routineDetail);
        binding.contentScrollingFragment.titleTextView.setText(routineName);
        binding.contentScrollingFragment.rating.setRating(routineScore);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(routineName);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FrameLayout frame = binding.contentScrollingFragment.cyclesFrameLayout;

        if (savedInstanceState == null) {
            Fragment newFragment = new CycleFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(frame.getId(), newFragment).commit();
        }

        routineFavorite = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail_routine, menu);

        App app = (App) getApplication();
        int page = 0;

        app.getRoutineRepository().getFavourites(page, 100).observe(this, r -> {
            if (r.getStatus() == Status.SUCCESS){
                for (Routine routine : r.getData().getContent()) {
                    if (routine.getId() == routineId) {
                        menu.findItem(R.id.action_fav).setIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.error)));
                        routineFavorite = true;
                        break;
                    }
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
        } else if(id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "https://trainme.com/?id=" + routineId + "&?name=" + routineName);
            startActivity(Intent.createChooser(shareIntent, "Choose one"));
        } else if(id == R.id.action_fav) {
            App app = (App) getApplication();

            if(routineFavorite) {
                app.getRoutineRepository().removeFav(routineId).observe(this, r -> {
                    if (r.getStatus() == Status.SUCCESS) {
                        item.setIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                        routineFavorite = !routineFavorite;
                    } else if (r.getStatus() == Status.ERROR) {
                    }
                });
            } else {
                app.getRoutineRepository().addToFavs(routineId).observe(this, r -> {
                    if (r.getStatus() == Status.SUCCESS) {
                        item.setIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.error)));
                        routineFavorite = !routineFavorite;
                    } else if (r.getStatus() == Status.ERROR) {

                    }
                });
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void playRoutine(View view) {
        Intent intent = new Intent(this, PlayRoutineActivity.class);
        Bundle bundle = getIntent().getExtras();

        intent.putExtra("ID", bundle.getInt("ID"));
        startActivity(intent);
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

}