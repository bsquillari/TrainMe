package com.trainme.ui.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;
import com.trainme.App;
import com.trainme.MainActivity;
import com.trainme.R;
import com.trainme.RoutinesFragmentArgs;
import com.trainme.databinding.FragmentProfileBinding;
import com.trainme.repository.Status;
import com.trainme.ui.login.LoginActivity;

import java.util.Date;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    private Activity mActivity;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        String section = ProfileFragmentArgs.fromBundle(getArguments()).getSection();
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        App app = (App) getActivity().getApplication();

        app.getUserRepository().getCurrentUser().observe(getViewLifecycleOwner(), r -> {
            if (r.getStatus() == Status.SUCCESS) {
                ImageLoader imageLoader = new ImageLoader(getContext());
                Log.d("getCurret", r.getStatus().toString());
                assert r.getData() != null;
                binding.emailProfile.setText(r.getData().getEmail());
                binding.fullNameProfile.setText(r.getData().getFirstName() + " " + r.getData().getLastName());
                binding.usernameProfile.setText(r.getData().getUsername());
                binding.phoneProfile.setText(r.getData().getPhone());
                binding.genderProfile.setText(r.getData().getGender());
                if (r.getData().getBirthdate() != null) {
                    Date birth = new Date(r.getData().getBirthdate());
                    binding.birthProfile.setText(birth.getDate() + "/" + birth.getMonth() + "/" + birth.getYear() );
                }else{
                    binding.birthProfile.setText(R.string.NoBdate);
                }
                Picasso.get().load(r.getData().getAvatarUrl()).into(binding.avatarURLProfile);
//                imageLoader.DisplayImage(r.getData().getAvatarUrl(), R.drawable.profilepic, binding.avatarURLProfile);
            }else if(r.getStatus() == Status.ERROR){
                Log.d("debugeado", "onCreateView: " + r.getError().toString());

            }
        });

        binding.LogOutBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.logoutQuestion)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            app.getUserRepository().logout().observe(getViewLifecycleOwner(), r -> {
                                Log.d("Logout", r.getStatus().toString());
                                if (r.getStatus() == Status.SUCCESS) {
                                    Log.d("profile", getString(R.string.success));
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }

                    })
                    .setNegativeButton(R.string.no, null)
                    .show();

        });
        SharedPreferences settings0 = getActivity().getSharedPreferences("UserPreferences", 0);
        if(settings0!=null){
            boolean value = settings0.getBoolean("DetailExerciseView", false);
            if(value){
                binding.detailViewBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
                binding.detailViewBtn.setText(getResources().getString(R.string.active));
                binding.multipleExercisesViewBtn.setText(getResources().getString(R.string.notActive));
                binding.multipleExercisesViewBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            }else{
                binding.multipleExercisesViewBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
                binding.multipleExercisesViewBtn.setText(getResources().getString(R.string.active));
                binding.detailViewBtn.setText(getResources().getString(R.string.notActive));
                binding.detailViewBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            }
        }
        binding.detailViewBtn.setOnClickListener(v -> {
            binding.detailViewBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
            binding.detailViewBtn.setText(getResources().getString(R.string.active));
            binding.multipleExercisesViewBtn.setText(getResources().getString(R.string.notActive));
            binding.multipleExercisesViewBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
                SharedPreferences settings1 = getActivity().getSharedPreferences("UserPreferences", 0);
                SharedPreferences.Editor editor = settings1.edit();
                editor.putBoolean("DetailExerciseView",true);
                editor.apply();
        });
        binding.multipleExercisesViewBtn.setOnClickListener(v -> {
            binding.multipleExercisesViewBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
            binding.multipleExercisesViewBtn.setText(getResources().getString(R.string.active));
            binding.detailViewBtn.setText(getResources().getString(R.string.notActive));
            binding.detailViewBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            SharedPreferences settings2 = getActivity().getSharedPreferences("UserPreferences", 0);
            SharedPreferences.Editor editor = settings2.edit();
            editor.putBoolean("DetailExerciseView",false);
            editor.apply();
        });
        binding.exploreBtn.setOnClickListener(v -> {
            binding.exploreBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
            binding.profileBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.myRoutinesBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.favsBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            SharedPreferences settings3 = getActivity().getSharedPreferences("UserPreferences", 0);
            SharedPreferences.Editor editor = settings3.edit();
            editor.putInt(getResources().getString(R.string.defaultSection),R.id.navigation_explore);
            editor.apply();
        });
        binding.profileBtn.setOnClickListener(v -> {
            binding.exploreBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.profileBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
            binding.myRoutinesBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.favsBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            SharedPreferences settings4 = getActivity().getSharedPreferences("UserPreferences", 0);
            SharedPreferences.Editor editor = settings4.edit();
            editor.putInt(getResources().getString(R.string.defaultSection),R.id.navigation_profile);
            editor.apply();
        });
        binding.myRoutinesBtn.setOnClickListener(v -> {
            binding.exploreBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.profileBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.myRoutinesBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
            binding.favsBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            SharedPreferences settings5 = getActivity().getSharedPreferences("UserPreferences", 0);
            SharedPreferences.Editor editor = settings5.edit();
            editor.putInt(getResources().getString(R.string.defaultSection),R.id.navigation_myroutines);
            editor.apply();
        });
        binding.favsBtn.setOnClickListener(v -> {
            binding.exploreBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.profileBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.myRoutinesBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.favsBtn.setBackgroundColor(getResources().getColor(R.color.purple_500));
            SharedPreferences settings6 = getActivity().getSharedPreferences("UserPreferences", 0);
            SharedPreferences.Editor editor = settings6.edit();
            editor.putInt(getResources().getString(R.string.defaultSection),R.id.navigation_favs);
            editor.apply();
        });

//        final TextView textView = binding.textProfile;
//        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        ((MainActivity)mActivity).filterBtn(!(section.equals("favs") || section.equals("profile")));
        return root;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            mActivity =(Activity) context;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}