package com.trainme.ui.profile;

import android.app.AlertDialog;
import android.app.Dialog;
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
import java.util.Objects;

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
                    binding.birthProfile.setText(birth.getDate() + "/" + birth.getMonth() + "/" + birth.getYear());
                } else {
                    binding.birthProfile.setText(R.string.NoBdate);
                }
                String url = r.getData().getAvatarUrl();
                if (url == null){
                    url = "https://i.pinimg.com/474x/65/25/a0/6525a08f1df98a2e3a545fe2ace4be47.jpg";
                }
                Picasso.get().load(url).into(binding.avatarURLProfile);
//                imageLoader.DisplayImage(r.getData().getAvatarUrl(), R.drawable.profilepic, binding.avatarURLProfile);
            } else if (r.getStatus() == Status.ERROR) {
                Log.d("debugeado", "onCreateView: " + r.getError().toString());

            }
        });

        binding.LogOutBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.logoutQuestion)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            app.getUserRepository().logout().observe(getViewLifecycleOwner(), r -> {
                                Log.d("Logout", r.getStatus().toString());
                                if (r.getStatus() == Status.SUCCESS) {
                                    Log.d("profile", getString(R.string.success));
                                    App newApp = (App) requireActivity().getApplication();
                                    newApp.getPreferences().setAuthToken(null);
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }

                    })
                    .setNegativeButton(R.string.no, null)
                    .show();

        });

        ((MainActivity) mActivity).filterBtn(!(section.equals("favs") || section.equals("profile")));
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}