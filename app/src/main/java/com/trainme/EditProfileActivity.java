package com.trainme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.trainme.databinding.ActivityEditProfileBinding;
import com.trainme.databinding.ActivityMainBinding;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


//        setContentView(R.layout.activity_edit_profile);
    }
}