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


import com.trainme.api.model.Cycle;
import com.trainme.api.model.PagedList;
import com.trainme.databinding.ActivityDetailRoutineBinding;
import com.trainme.databinding.CycleDetailBinding;
import com.trainme.databinding.FragmentItemCycleBinding;
import com.trainme.databinding.FragmentRoutinesBinding;
import com.trainme.repository.RoutineRepository;
import com.trainme.repository.Status;


import java.util.List;


public class MyCycleRecyclerViewAdapter extends RecyclerView.Adapter<MyCycleRecyclerViewAdapter.ViewHolder> {

    private final static int PageSize = 10;
    private final RoutineRepository repository;
    private final int id;
    private final LifecycleOwner lifecycleOwner;
    private final Context myContext;
    private List<Cycle> mValues;
    private PagedList<Cycle> data;
    private int page;
    private boolean isLastPage;

    public MyCycleRecyclerViewAdapter(RoutineRepository repository, LifecycleOwner lifecycleOwner, Context context, int id) {
        this.repository = repository;
        this.lifecycleOwner = lifecycleOwner;
        page = 0;
        this.id = id;
        repository.getCycles(page++, PageSize, "id", id).observe(lifecycleOwner, r -> {
            if (r.getStatus() == Status.SUCCESS) {
                Log.d("CyclesAdapter", "MyCycleRecyclerViewAdapter: sucess de cycles");
                data = r.getData();
                mValues = data.getContent();
                isLastPage = data.getIsLastPage();
                notifyDataSetChanged();
            } else if (r.getStatus() == Status.ERROR) {
                Log.d("CyclesAdapter", "MyCycleRecyclerViewAdapter: Error consiguiendo cycles");
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

        return new ViewHolder(FragmentItemCycleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mValues != null) {
            if (position != mValues.size() - 1 && !isLastPage) {
                Log.d("Routines", "NextPage");
                repository.getCycles(page++, PageSize, "id", id).observe(lifecycleOwner, r -> {

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
                holder.cycleName.setText(holder.mItem.getName());
//                holder.cycleDetail.setText(holder.mItem.getDetail());
                holder.cycleType.setText(holder.mItem.getType());
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
        public TextView cycleName, cycleDetail, cycleType, reps;
        public Cycle mItem;

        public ViewHolder(FragmentItemCycleBinding binding) {
            super(binding.getRoot());
            cycleName = binding.cycleNameTextView;
            cycleType = binding.cycleTypeTextView;
            reps = binding.reps;
            binding.cycleCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("cycle viewhodler", "onClick: de cycle view");

//                    Intent myIntent = new Intent(MainActivity.this, Detail.class);
//                    Intent myIntent = new Intent(myContext, DetailRoutine.class);
//                    myIntent.putExtra("ID", 18); //Optional parameters
//                    myContext.startActivity(myIntent);
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + cycleName.getText() + "'";
        }
    }
}