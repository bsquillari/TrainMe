package com.trainme.ui.profile;

import android.content.Context;
import android.content.Intent;
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
                    binding.birthProfile.setText(birth.toString());
                }else{
                    binding.birthProfile.setText(R.string.NoBdate);
                }
                imageLoader.DisplayImage(r.getData().getAvatarUrl(), R.drawable.profilepic, binding.avatarURLProfile);
            }else if(r.getStatus() == Status.ERROR){
                Log.d("debugeado", "onCreateView: " + r.getError().toString());

            }
        });

        binding.LogOutBtn.setOnClickListener(v -> {

            app.getUserRepository().logout().observe(getViewLifecycleOwner(), r -> {
                Log.d("Logout", r.getStatus().toString());
                if (r.getStatus() == Status.SUCCESS) {
                    Log.d("profile", getString(R.string.success));
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
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