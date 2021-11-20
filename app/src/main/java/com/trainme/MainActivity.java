package com.trainme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


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
                R.id.navigation_myroutines, R.id.navigation_explore, R.id.navigation_profile, R.id.navigation_favs)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        SharedPreferences settings = getSharedPreferences("UserPreferences", 0);
        if(settings!=null){
            int i;
            if((settings.getInt(getResources().getString(R.string.defaultSection), 0))!=0)
            navController.navigate(settings.getInt(getResources().getString(R.string.defaultSection), 0));
        }
        binding.filterBtn.show();
        binding.currentOrderText.setText(getResources().getString(R.string.orderBy));
        binding.filterBtn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.inflate(R.menu.popup_filter_menu);
            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu menu) {
                    binding.currentOrder.setVisibility(View.GONE);
                }
            });
            binding.currentOrder.setVisibility(View.VISIBLE);
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




    public void openEditProfileActivity(View view) {
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }

    public void openDetailRoutine(View view) {
        Intent intent = new Intent(this, DetailRoutineActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Fragment navHost = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        RoutinesFragment fragment = (RoutinesFragment)navHost.getChildFragmentManager().getPrimaryNavigationFragment();
        switch (item.getItemId()){
            case R.id.menu_item_date:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.currentOrder, getResources().getString(R.string.menu_item_date)), Toast.LENGTH_LONG).show();
                item.setChecked(true);
                fragment.refreshOrderBy("date");
                break;
            case R.id.menu_item_score:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.currentOrder, getResources().getString(R.string.menu_item_score)), Toast.LENGTH_LONG).show();
                item.setChecked(true);
                fragment.refreshOrderBy("score");
                break;
            case R.id.menu_item_category:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.currentOrder, getResources().getString(R.string.menu_item_category)), Toast.LENGTH_LONG).show();
                item.setChecked(true);
                fragment.refreshOrderBy("category");
                break;
            case R.id.menu_item_difficulty:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.currentOrder, getResources().getString(R.string.menu_item_difficulty)), Toast.LENGTH_LONG).show();
                item.setChecked(true);
                fragment.refreshOrderBy("difficulty");
                break;
            case R.id.menu_item_name:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.currentOrder, getResources().getString(R.string.menu_item_name)), Toast.LENGTH_LONG).show();
                item.setChecked(true);
                fragment.refreshOrderBy("name");
                break;
            default: return false;
        }
        return true;
    }
}