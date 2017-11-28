package com.locationsetup;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment implements ListAdapter.ItemClickListener,
        MainActivity.OnItemChangedListener {

    private final String TAG = ListFragment.class.getSimpleName();

    ListAdapter mAdapter;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance() {
        return new ListFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RecyclerView recyclerView = getView().findViewById(R.id.listView);
        recyclerView.setHasFixedSize(true);

        mAdapter = new ListAdapter(getActivity());
        mAdapter.setItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(getActivity(), new LinearLayoutManager(getActivity()).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        (getView().findViewById(R.id.addItem)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Add item", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ((MainActivity)context).setItemClickListener(this);
    }

    @Override
    public void onItemClick(int position, View v) {
        LocationItem item = mAdapter.getItem(position);
        // 상세 보기로 이동
        if (v.getId() == R.id.switch_btn) {

        } else {
            Toast.makeText(getActivity(), "#" + position + " Name: " + item.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemLongClick(int position) {
        LocationItem item = mAdapter.getItem(position);
        // 삭제 확인 다이얼로그
        mAdapter.removeItem(position);
        Toast.makeText(getActivity(), item.getName() + "removed", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onItemChanged() {
        mAdapter.notifyDataSetChanged();
        Log.d(TAG, "Adapter.notify()");

    }
}
