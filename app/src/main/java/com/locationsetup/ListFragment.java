package com.locationsetup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {

    View view;
    static public ListFragment fragment = new ListFragment();

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance() {
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_list, container, false);
        tempCreate(view);
        return view;
    }

    private void tempCreate(View view){
        LocationItem locationItem = new LocationItem("house","address");
        ArrayList<LocationItem> locationItems = new ArrayList<LocationItem>();
        locationItems.add(new LocationItem("school","address"));

        RecyclerView reView = (RecyclerView) view.findViewById(R.id.reView);

        locationItems.add(locationItem);

        ListAdapter adapter = new ListAdapter(view.getContext(),locationItems);
        reView.setAdapter(adapter);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        reView.setLayoutManager(gridLayoutManager);

        adapter.notifyDataSetChanged();
    }

}
