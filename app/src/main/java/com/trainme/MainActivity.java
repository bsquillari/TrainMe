package com.trainme;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.trainme.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        Button editProfileButton = findViewById(R.id.EditProfileBtn);
//        Log.d("hello", "onClick: a ");
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_profile, R.id.navigation_favs)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        binding.filterBtn.show();
        binding.filterBtn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.inflate(R.menu.popup_filter_menu);
            popupMenu.show();
        });
    }

    public void filterBtn(boolean value){
        if(binding==null)
            return;
        if(value){
            binding.filterBtn.show();
        }else binding.filterBtn.hide();
    }

    public void openEditProfileActivity() {
//        setContentView(ActivityEditProfileBinding.inflate(getLayoutInflater()).getRoot());


    }


    public void openEditProfileActivity(View view) {
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }

    public void openDetailRoutine(View view) {
        Intent intent = new Intent(this, DetailRoutine.class);
        startActivity(intent);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Fragment navHost = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        RoutinesFragment fragment = (RoutinesFragment)navHost.getChildFragmentManager().getPrimaryNavigationFragment();
        switch (item.getItemId()){
            case R.id.menu_item_date:
                fragment.refreshOrderBy("date");
                break;
            case R.id.menu_item_score:
                fragment.refreshOrderBy("score");
                break;
            case R.id.menu_item_category:
                fragment.refreshOrderBy("category");
                break;
            case R.id.menu_item_difficulty:
                fragment.refreshOrderBy("difficulty");
                break;
            case R.id.menu_item_name:
                fragment.refreshOrderBy("name");
                break;
            default: return false;
        }
        return true;
    }
}