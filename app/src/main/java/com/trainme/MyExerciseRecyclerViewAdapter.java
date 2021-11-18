package com.trainme;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.trainme.api.model.ContentEx;
import com.trainme.api.model.Cycle;
import com.trainme.api.model.Exercise;
import com.trainme.api.model.PagedList;
import com.trainme.databinding.ActivityDetailRoutineBinding;
import com.trainme.databinding.FragmentExerciseItemBinding;
import com.trainme.databinding.FragmentItemCycleBinding;
import com.trainme.databinding.FragmentRoutinesBinding;
import com.trainme.repository.RoutineRepository;
import com.trainme.repository.Status;


import java.util.List;


public class MyExerciseRecyclerViewAdapter extends RecyclerView.Adapter<MyExerciseRecyclerViewAdapter.ViewHolder> {

    private final static int PageSize = 10;
    private final RoutineRepository repository;
    private final int id;
    private final LifecycleOwner lifecycleOwner;
    private final Context myContext;
    private List<ContentEx> mValues;
    private PagedList<ContentEx> data;
    private int page;
    private boolean isLastPage;

    public MyExerciseRecyclerViewAdapter(RoutineRepository repository, LifecycleOwner lifecycleOwner, Context context, int id) {
        this.repository = repository;
        this.lifecycleOwner = lifecycleOwner;
        page = 0;
        this.id = id;
        repository.getExercises(page++, PageSize, "exerciseId", id).observe(lifecycleOwner, r -> {
            if (r.getStatus() == Status.SUCCESS) {
                Log.d("ExerciseAdapter", "MyExerciseeRecyclerViewAdapter: sucess de exercises");
                data = r.getData();
                mValues = data.getContent();
                isLastPage = data.getIsLastPage();
                notifyDataSetChanged();
            } else if (r.getStatus() == Status.ERROR) {
                Log.d("ExerciseAdapter", "exercise adapter: Error consiguiendo exercises");
            }
        });
//            repository.getMyRoutines(page++, PageSize, "id").observe(lifecycleOwner, r -> {
//
//                if (r.getStatus() == Status.SUCCESS) {
//                    Log.d("Routines", r.getData().getContent().toString());
//                    data = r.getData();
//                    mValues = r.getData().getContent();
//                    isLastPage = r.getData().getIsLastPage();
//                    notifyDataSetChanged();
//                } else {
//
//                }
//            });
//
        myContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentExerciseItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mValues != null) {
            if (position != mValues.size() - 1 && !isLastPage) {
                Log.d("Exercise", "NextPage");
                repository.getExercises(page++, PageSize, "exerciseId", id).observe(lifecycleOwner, r -> {

                    if (r.getStatus() == Status.SUCCESS) {
                        Log.d("Cycles", r.getData().getContent().toString());
                        mValues.addAll(r.getData().getContent());
                        isLastPage = r.getData().getIsLastPage();
//                        notifyDataSetChanged();
                    } else if (r.getStatus() == Status.ERROR) {
                        Log.d("CyclesAdapter", "MyCycleRecyclerViewAdapter: Error consiguiendo cycles");
                    }
                });

            }
            if (position < mValues.size()) {
                holder.mItem = mValues.get(position);
                holder.exerciseName.setText(holder.mItem.getExercise().getName());
                holder.reps.setText(holder.mItem.getRepetitions().toString());
                holder.type.setText(holder.mItem.getExercise().getType());
                holder.duration.setText(holder.mItem.getDuration().toString());
//                holder.cycleDetail.setText(holder.mItem.getDetail());
//                holder.reps.setText(holder.mItem.getRepetitions());
            }

        }
//        holder.colorPill.setCardBackgroundColor( mValues.ITEMS.get(position).color);

    }

    @Override
    public int getItemCount() {
        if (mValues == null)
            return 0;
        else return data.getTotalCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView exerciseName, reps, type, duration;
        public ContentEx mItem;

        public ViewHolder(FragmentExerciseItemBinding binding) {
            super(binding.getRoot());
            exerciseName = binding.exerciseName;
            reps = binding.exerciseRepetitions;
            type = binding.exerciseType;
            duration = binding.exerciseDuration;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + exerciseName.getText() + "'";
        }
    }
}