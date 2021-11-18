package com.trainme;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.trainme.api.model.PagedList;
import com.trainme.api.model.Routine;
import com.trainme.databinding.FragmentRoutinesBinding;
import com.trainme.repository.RoutineRepository;
import com.trainme.repository.Status;


import java.util.List;


public class MyRoutineRecyclerViewAdapter extends RecyclerView.Adapter<MyRoutineRecyclerViewAdapter.ViewHolder> {

    private List<Routine> mValues;
    private PagedList<Routine> data;
    private final RoutineRepository repository;
    private final LifecycleOwner lifecycleOwner;
    private final Context myContext;
    private final static int PageSize = 10;
    private int page;
    private boolean isLastPage;
    private String section;
    private String orderBy;
    //private final int colors[]={Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.LTGRAY};

    /*private int getColor(int id){
        return colors[id % 4];
    }*/


    public MyRoutineRecyclerViewAdapter(RoutineRepository repository, LifecycleOwner lifecycleOwner, Context context, String section, String orderBy) {
        this.repository = repository;
        this.lifecycleOwner = lifecycleOwner;
        this.section = section;
        page = 0;
        this.orderBy = orderBy;

        if(section.equals("home")) {
            repository.getMyRoutines(page++, PageSize, this.orderBy).observe(lifecycleOwner, r -> {

                if (r.getStatus() == Status.SUCCESS) {
                    Log.d("Routines", r.getData().getContent().toString());
                    data = r.getData();
                    mValues = r.getData().getContent();
                    isLastPage = r.getData().getIsLastPage();
                    notifyDataSetChanged();
                } else {

                }
            });
        }
        if(section.equals("dashboard")) {
            repository.getRoutines(page++, PageSize, this.orderBy).observe(lifecycleOwner, r -> {

                if (r.getStatus() == Status.SUCCESS) {
                    Log.d("Routines", r.getData().getContent().toString());
                    data = r.getData();
                    mValues = r.getData().getContent();
                    isLastPage = r.getData().getIsLastPage();
                    notifyDataSetChanged();
                } else {

                }
            });
        }
        if(section.equals("favs")) {
            repository.getFavourites(page++, PageSize).observe(lifecycleOwner, r -> {

                if (r.getStatus() == Status.SUCCESS) {
                    Log.d("Routines", r.getData().getContent().toString());
                    data = r.getData();
                    mValues = r.getData().getContent();
                    isLastPage = r.getData().getIsLastPage();
                    notifyDataSetChanged();
                } else {

                }
            });
        }

        myContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentRoutinesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(mValues!=null) {
            if(position!=mValues.size()-1 && !isLastPage){
                Log.d("Routines", "NextPage");
                if(section.equals("dashboard")) {
                    repository.getRoutines(page++, PageSize, this.orderBy).observe(lifecycleOwner, r -> {

                        if (r.getStatus() == Status.SUCCESS) {
                            Log.d("Routines", r.getData().getContent().toString());
                            mValues.addAll(r.getData().getContent());
                            isLastPage = r.getData().getIsLastPage();
                        } else {

                        }
                    });
                }
                if(section.equals("home")) {
                    repository.getMyRoutines(page++, PageSize, this.orderBy).observe(lifecycleOwner, r -> {

                        if (r.getStatus() == Status.SUCCESS) {
                            Log.d("Routines", r.getData().getContent().toString());
                            mValues.addAll(r.getData().getContent());
                            isLastPage = r.getData().getIsLastPage();
                        } else {

                        }
                    });
                }
                if(section.equals("favs")) {
                    repository.getFavourites(page++, PageSize).observe(lifecycleOwner, r -> {

                        if (r.getStatus() == Status.SUCCESS) {
                            Log.d("Routines", r.getData().getContent().toString());
                            mValues.addAll(r.getData().getContent());
                            isLastPage = r.getData().getIsLastPage();
                        } else {

                        }
                    });
                }
            }
                if(position<mValues.size()) {
                    holder.mItem = mValues.get(position);
                    holder.routineName.setText(holder.mItem.getName());
                    holder.routineDescription.setText(holder.mItem.getDetail());
                    holder.iconImage.setImageResource(R.mipmap.ic_launcher);
                    int color=Color.BLACK;
                    switch (holder.mItem.getDifficulty()){
                        case "rookie": color=Color.GREEN;
                        break;
                        case "beginner": color=Color.YELLOW;
                        break;
                        case "intermediate": color=Color.LTGRAY;
                        break;
                        case "advanced": color=Color.BLUE;
                            break;
                        case "expert": color=Color.RED;
                            break;
                    }
                    holder.colorPill.setCardBackgroundColor(color);
                    int finalColor = color;
                    holder.routineCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                    Intent myIntent = new Intent(MainActivity.this, Detail.class);
                            Intent myIntent = new Intent(myContext, DetailRoutineActivity.class);
                            myIntent.putExtra("ID", holder.mItem.getId()); //Optional parameters
                            myIntent.putExtra("Name", holder.mItem.getName()); //Optional parameters
                            myIntent.putExtra("Detail", holder.mItem.getDetail()); //Optional parameters
                            myIntent.putExtra("Difficulty", holder.mItem.getDifficulty()); //Optional parameters
                            myIntent.putExtra("Score", holder.mItem.getScore()); //Optional parameters
                            myIntent.putExtra("ColorPill", finalColor);
                            //myIntent.putExtra("Username", holder.mItem.getUser().getUsername());
                            myContext.startActivity(myIntent);
                        }
                    });
                }

        }
//        holder.colorPill.setCardBackgroundColor( mValues.ITEMS.get(position).color);

    }

    @Override
    public int getItemCount() {
        if(mValues==null)
            return 0;
        else return data.getTotalCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iconImage;
        public TextView routineName, routineDescription;
        public Routine mItem;
        public CardView colorPill;
        public CardView routineCard;

        public ViewHolder(FragmentRoutinesBinding binding) {
            super(binding.getRoot());
            iconImage = binding.iconImageView;
            routineName = binding.nameTextView;
            routineDescription = binding.descriptionTextView;
            colorPill = binding.colorPill;
            routineCard = binding.routineCard;
//            binding.routineCard.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    Intent myIntent = new Intent(MainActivity.this, Detail.class);
//                    Intent myIntent = new Intent(myContext,DetailRoutine.class);
//                    myIntent.putExtra("ID", data.); //Optional parameters
//                    myContext.startActivity(myIntent);
//                }
//            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + routineName.getText() + "'";
        }
    }
}