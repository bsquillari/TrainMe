package com.trainme;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.trainme.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySettingsBinding binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.action_settings));
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        settings = getSharedPreferences("UserPreferences", 0);
        if (settings != null) {
            if (settings.getBoolean("DetailExerciseView", false)) {
                binding.singleExercise.setChecked(true);
            } else {
                binding.multipleExercises.setChecked(true);
            }
            switch (settings.getInt(getResources().getString(R.string.defaultSection), 0)) {
                case R.id.navigation_profile:
                    binding.profileRadio.setChecked(true);
                    break;
                case R.id.navigation_explore:
                    binding.exploreRadio.setChecked(true);
                    break;
                case R.id.navigation_favs:
                    binding.favRadio.setChecked(true);
                    break;
                default:
                    binding.myRoutinesRadio.setChecked(true);
                    break;
            }
        }
        binding.singleExercise.setOnClickListener(v -> {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("DetailExerciseView", true);
            editor.apply();
        });
        binding.multipleExercises.setOnClickListener(v -> {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("DetailExerciseView", false);
            editor.apply();
        });

        binding.exploreRadio.setOnClickListener(v -> {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(getResources().getString(R.string.defaultSection), R.id.navigation_explore);
            editor.apply();
        });
        binding.profileRadio.setOnClickListener(v -> {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(getResources().getString(R.string.defaultSection), R.id.navigation_profile);
            editor.apply();
        });
        binding.myRoutinesRadio.setOnClickListener(v -> {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(getResources().getString(R.string.defaultSection), R.id.navigation_myroutines);
            editor.apply();
        });
        binding.favRadio.setOnClickListener(v -> {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(getResources().getString(R.string.defaultSection), R.id.navigation_favs);
            editor.apply();
        });

    }
}
