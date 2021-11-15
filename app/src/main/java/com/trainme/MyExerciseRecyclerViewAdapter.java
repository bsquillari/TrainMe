package com.trainme;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trainme.api.model.Exercise;
import com.trainme.databinding.FragmentExerciseItemBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyExerciseRecyclerViewAdapter extends RecyclerView.Adapter<MyExerciseRecyclerViewAdapter.ViewHolder> {

    private final List<Exercise> mValues;

    public MyExerciseRecyclerViewAdapter(List<Exercise> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentExerciseItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.name.setText("Exercise");
        holder.repetitions.setText(mValues.get(position).getRepetitions().toString());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView name, repetitions;
        public Exercise mItem;

        public ViewHolder(FragmentExerciseItemBinding binding) {
            super(binding.getRoot());
            name = binding.exerciseName;
            repetitions = binding.exerciseRepetitions;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + name.getText() + "'";
        }
    }
}