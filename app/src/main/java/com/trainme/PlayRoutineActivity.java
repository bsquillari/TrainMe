package com.trainme;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.trainme.databinding.ActivityPlayRoutineBinding;

public class PlayRoutineActivity extends AppCompatActivity {

    private ActivityPlayRoutineBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPlayRoutineBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

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
}