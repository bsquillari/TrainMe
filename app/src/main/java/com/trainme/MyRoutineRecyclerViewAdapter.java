package com.trainme;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trainme.placeholder.PlaceholderContent.PlaceholderItem;
import com.trainme.databinding.FragmentRoutinesBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRoutineRecyclerViewAdapter extends RecyclerView.Adapter<MyRoutineRecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderItem> mValues;
    private Context myContext;

    public MyRoutineRecyclerViewAdapter(List<PlaceholderItem> items, Context context) {
        mValues = items;
        myContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentRoutinesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.routineName.setText(mValues.get(position).name);
        holder.routineDescription.setText(mValues.get(position).description);
        holder.iconImage.setImageResource(mValues.get(position).imageSrc);
        holder.colorPill.setCardBackgroundColor(mValues.get(position).color);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iconImage;
        public TextView routineName, routineDescription;
        public PlaceholderItem mItem;
        public CardView colorPill;

        public ViewHolder(FragmentRoutinesBinding binding) {
            super(binding.getRoot());
            iconImage = binding.iconImageView;
            routineName = binding.nameTextView;
            routineDescription = binding.descriptionTextView;
            colorPill = binding.colorPill;
            binding.routineCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent myIntent = new Intent(MainActivity.this, Detail.class);
                    Intent myIntent = new Intent(myContext,DetailRoutine.class);
                    myIntent.putExtra("ID", 123); //Optional parameters
                    myContext.startActivity(myIntent);
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + routineName.getText() + "'";
        }
    }
}