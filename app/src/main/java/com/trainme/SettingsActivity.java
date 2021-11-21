package com.trainme;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.trainme.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySettingsBinding binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.action_settings));
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences settings0 = getSharedPreferences("UserPreferences", 0);
        if (settings0 != null) {
            boolean value = settings0.getBoolean("DetailExerciseView", false);
            if (value) {
                binding.detailViewBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
                binding.detailViewBtn.setText(getResources().getString(R.string.active));
                binding.multipleExercisesViewBtn.setText(getResources().getString(R.string.notActive));
                binding.multipleExercisesViewBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            } else {
                binding.multipleExercisesViewBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
                binding.multipleExercisesViewBtn.setText(getResources().getString(R.string.active));
                binding.detailViewBtn.setText(getResources().getString(R.string.notActive));
                binding.detailViewBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            }
            switch (settings0.getInt(getResources().getString(R.string.defaultSection), 0)) {
                case R.id.navigation_profile:
                    binding.profileBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
                    break;

                case R.id.navigation_explore:
                    binding.exploreBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
                    break;
                case R.id.navigation_favs:
                    binding.favsBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
                    break;
                default:
                    binding.myRoutinesBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
                    break;
            }
        }
        binding.detailViewBtn.setOnClickListener(v -> {
            binding.detailViewBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
            binding.detailViewBtn.setText(getResources().getString(R.string.active));
            binding.multipleExercisesViewBtn.setText(getResources().getString(R.string.notActive));
            binding.multipleExercisesViewBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            SharedPreferences settings1 = getSharedPreferences("UserPreferences", 0);
            SharedPreferences.Editor editor = settings1.edit();
            editor.putBoolean("DetailExerciseView", true);
            editor.apply();
        });
        binding.multipleExercisesViewBtn.setOnClickListener(v -> {
            binding.multipleExercisesViewBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
            binding.multipleExercisesViewBtn.setText(getResources().getString(R.string.active));
            binding.detailViewBtn.setText(getResources().getString(R.string.notActive));
            binding.detailViewBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            SharedPreferences settings2 = getSharedPreferences("UserPreferences", 0);
            SharedPreferences.Editor editor = settings2.edit();
            editor.putBoolean("DetailExerciseView", false);
            editor.apply();
        });
        binding.exploreBtn.setOnClickListener(v -> {
            binding.exploreBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
            binding.profileBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.myRoutinesBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.favsBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            SharedPreferences settings3 = getSharedPreferences("UserPreferences", 0);
            SharedPreferences.Editor editor = settings3.edit();
            editor.putInt(getResources().getString(R.string.defaultSection), R.id.navigation_explore);
            editor.apply();
        });
        binding.profileBtn.setOnClickListener(v -> {
            binding.exploreBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.profileBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
            binding.myRoutinesBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.favsBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            SharedPreferences settings4 = getSharedPreferences("UserPreferences", 0);
            SharedPreferences.Editor editor = settings4.edit();
            editor.putInt(getResources().getString(R.string.defaultSection), R.id.navigation_profile);
            editor.apply();
        });
        binding.myRoutinesBtn.setOnClickListener(v -> {
            binding.exploreBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.profileBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.myRoutinesBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
            binding.favsBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            SharedPreferences settings5 = getSharedPreferences("UserPreferences", 0);
            SharedPreferences.Editor editor = settings5.edit();
            editor.putInt(getResources().getString(R.string.defaultSection), R.id.navigation_myroutines);
            editor.apply();
        });
        binding.favsBtn.setOnClickListener(v -> {
            binding.exploreBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.profileBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.myRoutinesBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.favsBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
            SharedPreferences settings6 = getSharedPreferences("UserPreferences", 0);
            SharedPreferences.Editor editor = settings6.edit();
            editor.putInt(getResources().getString(R.string.defaultSection), R.id.navigation_favs);
            editor.apply();
        });

    }
}
