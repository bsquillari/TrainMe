package com.trainme;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import com.squareup.picasso.Picasso;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.trainme.api.model.Review;
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
    private String username;
    private int routineScore;
    private int colorPill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailRoutineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //https://trainme.com/?id=1&name=routineName&detail=detail&difficulty=difficulty&score=2&color_pill=1
        routineId = getIntent().getExtras().getInt("ID");
        routineName = getIntent().getExtras().getString("Name");
        routineDetail = getIntent().getExtras().getString("Detail");
        routineDifficulty = getIntent().getExtras().getString("Difficulty");
        routineScore = getIntent().getExtras().getInt("Score");
        colorPill = getIntent().getExtras().getInt("ColorPill");
        //username=getIntent().getExtras().getString("Username");

        binding.contentScrollingFragment.detailTextView.setText(routineDetail);
        binding.contentScrollingFragment.titleTextView.setText(routineName);
        binding.contentScrollingFragment.rating.setRating(routineScore);
        binding.contentScrollingFragment.difficultyTextView.setText(routineDifficulty);
        binding.contentScrollingFragment.colorPill.setCardBackgroundColor(colorPill);
        //binding.contentScrollingFragment.username.setText(username);

        App api= (App)getApplication();
        api.getRoutineRepository().getRoutine(routineId).observe(this,r -> {
            if (r.getStatus() == Status.SUCCESS) {
                Log.d("user", "user");
                String username=r.getData().getUser().getUsername();
                Picasso.get().load(r.getData().getUser().getAvatarUrl()).into(binding.contentScrollingFragment.iconImageView);
                binding.contentScrollingFragment.username.setText(username);

            } else if (r.getStatus() == Status.ERROR) {
                Log.d("user", " Error");
            }
        });

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

        if(id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String URI = "https://trainme.com/"
                    + "?id=" + routineId
                    + "&name=" + routineName
                    + "&detail=" + routineDetail
                    + "&difficulty=" + routineDifficulty
                    + "&score=" + routineScore
                    + "&color_pill=" + colorPill;
            shareIntent.putExtra(Intent.EXTRA_TEXT, URI.replaceAll(" ", "+"));
            startActivity(Intent.createChooser(shareIntent, "Choose one"));
            return true;
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
            return true;
        } else if (id == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
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


        Dialog rankDialog = new Dialog(DetailRoutineActivity.this, R.style.FullHeightDialog);
        rankDialog.setContentView(R.layout.rank_dialog);
        rankDialog.setCancelable(true);

        RatingBar ratingBar = (RatingBar)rankDialog.findViewById(R.id.dialog_ratingbar);


        TextView text = (TextView) rankDialog.findViewById(R.id.rank_dialog_title);
        text.setText(routineName);

        Button updateButton = (Button) rankDialog.findViewById(R.id.rank_dialog_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App api= (App) getApplication();
                Integer score= Double.valueOf(ratingBar.getRating()).intValue();

                //llamado a la Api
                String message="Rated "+ score.toString() + " stars !";
                Review review=new Review(score,"" );
                api.getRoutineRepository().addReview(routineId,review).observe(DetailRoutineActivity.this,
                        r -> {
                            if(r.getStatus()== Status.SUCCESS) {
                                Log.d("RATE", "added review");
                                Toast.makeText(DetailRoutineActivity.this, message, Toast.LENGTH_LONG).show();
                            }else if (r.getStatus() == Status.ERROR)
                                Log.d("RATE", "rate Error");

                        });

                rankDialog.dismiss();
                refreshRating();

            }
        });
        //now that the dialog is set up, it's time to show it
        rankDialog.show();
    }

    private void refreshRating() {
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