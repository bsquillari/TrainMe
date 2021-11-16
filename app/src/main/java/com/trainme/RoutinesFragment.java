package com.trainme;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A fragment representing a list of Items.
 */
public class RoutinesFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private String section;
    public String orderBy = "date";
    public MyRoutineRecyclerViewAdapter myRoutineRecyclerViewAdapter;
    private Context context;
    private Activity mActivity;
    private RecyclerView recyclerView;



    public void refreshOrderBy(String orderBy){
        if(myRoutineRecyclerViewAdapter!=null) {
            Log.d("refreshOrderBy", "Not Null RecyclerAdapter. Set order to: " + orderBy);
//            myRoutineRecyclerViewAdapter.setOrderBy(orderBy);
//            myRoutineRecyclerViewAdapter.notifyDataSetChanged();
            this.orderBy = orderBy;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            MyRoutineRecyclerViewAdapter myRoutineRecyclerViewAdapter = new MyRoutineRecyclerViewAdapter(((App)getActivity().getApplication()).getRoutineRepository(), getViewLifecycleOwner(), this.getContext(), section, orderBy);
            recyclerView.setAdapter(myRoutineRecyclerViewAdapter);
            this.myRoutineRecyclerViewAdapter = myRoutineRecyclerViewAdapter;
        }else Log.d("refreshOrderBy", "Null RecyclerAdapter");
    }
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RoutinesFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RoutinesFragment newInstance(int columnCount) {
        RoutinesFragment fragment = new RoutinesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        section = RoutinesFragmentArgs.fromBundle(getArguments()).getSection();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            mActivity =(Activity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routines_list, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            ((MainActivity)mActivity).filterBtn(!(section.equals("favs") || section.equals("profile")));
            this.context = context;
            RecyclerView recyclerView = (RecyclerView) view;
            this.recyclerView = recyclerView;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            MyRoutineRecyclerViewAdapter myRoutineRecyclerViewAdapter = new MyRoutineRecyclerViewAdapter(((App)getActivity().getApplication()).getRoutineRepository(), getViewLifecycleOwner(), this.getContext(), section, orderBy);
            recyclerView.setAdapter(myRoutineRecyclerViewAdapter);
            this.myRoutineRecyclerViewAdapter = myRoutineRecyclerViewAdapter;
        }

        return view;
    }
}