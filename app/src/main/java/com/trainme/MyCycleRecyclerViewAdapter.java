package com.trainme;


import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.trainme.api.model.Cycle;
import com.trainme.api.model.PagedList;
import com.trainme.databinding.FragmentItemCycleBinding;
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
    private final Context context;


    public MyCycleRecyclerViewAdapter(RoutineRepository repository, LifecycleOwner lifecycleOwner, Context context, int id) {
        this.repository = repository;
        this.lifecycleOwner = lifecycleOwner;
        page = 0;
        this.context = context;
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
                holder.id = holder.mItem.getId();
                holder.reps.setText(holder.mItem.getRepetitions().toString());
                holder.exerciseList.setLayoutManager(new GridLayoutManager(context, 1));        // TODO: Cambiar el numero de columnas segun el tamaño?
                holder.exerciseList.setAdapter(new MyExerciseRecyclerViewAdapter(repository, lifecycleOwner, context, holder.id));

            }

        }


    }

    @Override
    public int getItemCount() {
        if (mValues == null)
            return 0;
        else return data.getTotalCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView cycleName, cycleDetail, cycleType, reps;
        public ConstraintLayout expandableLayout;
        public LinearLayout cycleMainLinearLayout;
        public FrameLayout exercisesFrameLayout;
        public RecyclerView exerciseList;
        public Cycle mItem;
        public int id;

        public ViewHolder(FragmentItemCycleBinding binding) {
            super(binding.getRoot());
            cycleName = binding.cycleNameTextView;
            cycleType = binding.cycleTypeTextView;
            reps = binding.repsTextView;

            cycleMainLinearLayout = binding.cycleLinearLayout;
            expandableLayout = binding.expandableCycle;
            exerciseList = binding.list;

            binding.cycleCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("cycle viewhodler", "onClick: de cycle view");
                    mItem.setExpanded(!mItem.isExpanded());
                    expandableLayout.setVisibility(mItem.isExpanded()? View.VISIBLE : View.GONE);

                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + cycleName.getText() + "'";
        }
    }
}