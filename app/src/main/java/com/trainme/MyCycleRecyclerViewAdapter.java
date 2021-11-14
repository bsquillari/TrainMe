package com.trainme;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trainme.api.model.Cycle;
import com.trainme.placeholder.PlaceholderContent.PlaceholderItem;
import com.trainme.databinding.FragmentItemCycleBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Cycle}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyCycleRecyclerViewAdapter extends RecyclerView.Adapter<MyCycleRecyclerViewAdapter.ViewHolder> {

    private final List<Cycle> mValues;

    public MyCycleRecyclerViewAdapter(List<Cycle> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentItemCycleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.cycle = mValues.get(position);
        holder.name.setText(mValues.get(position).getName());
        holder.type.setText(mValues.get(position).getType());
        holder.repetitions.setText(mValues.get(position).getRepetitions().toString());
        boolean isExpanded = mValues.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Cycle cycle;
        public TextView name, type, repetitions;
        public ConstraintLayout expandableLayout;
        public LinearLayout cycleMainLinearLayout;
        public ImageView expandIcon;
        public ViewHolder(FragmentItemCycleBinding binding) {
            super(binding.getRoot());
            name = binding.cycleNameTextView;
            type = binding.cycleTypeTextView;
            repetitions = binding.cyclesRepetitionsTextView;
            expandableLayout = binding.expandableCycle;
            cycleMainLinearLayout = binding.cycleLinearLayout;
            expandIcon = binding.dropDown;
            cycleMainLinearLayout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    cycle.setExpanded(!cycle.isExpanded());
                    if(cycle.isExpanded())
                        expandIcon.setImageResource(R.drawable.ic_baseline_arrow_circle_up_24);
                    else
                        expandIcon.setImageResource(R.drawable.ic_baseline_arrow_circle_down_24);
                    notifyItemChanged(getBindingAdapterPosition());

                }
            });
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}