package com.trainme.ui.favs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.trainme.databinding.FragmentFavsBinding;

public class FavsFragment extends Fragment {

    private FavsViewModel favsViewModel;
    private FragmentFavsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        favsViewModel =
                new ViewModelProvider(this).get(FavsViewModel.class);

        binding = FragmentFavsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textFavs;
        favsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}