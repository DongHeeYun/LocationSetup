package com.locationsetup;

import android.content.Context;
import android.content.Intent;
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

    /*private OnItemUpdateListener mCallback;

    public interface OnItemUpdateListener {
        void onItemUpdated(int position);
    }

    public void setItemClickListener(OnItemUpdateListener itemUpdateListener) {
        mCallback = itemUpdateListener;
    }*/

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
                //Toast.makeText(getActivity(), "Add item", Toast.LENGTH_SHORT).show();
                LocationItem item = new LocationItem("집", "경기도 고양시 일산서구 덕이동 하이파크시티 401동 2305호",
                        37.696757, 126.748531, 2, 1, 1, 20, 50);
                Intent intent = new Intent(getActivity(), ApplyActivity.class);
                intent.putExtra("setting", item);
                startActivity(intent);
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
            Toast.makeText(getActivity(), "#" + position + " Name: " + item.isEnabled(), Toast.LENGTH_SHORT).show();
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
    public void onItemSwitchCheck(int position, boolean isEnable) {
        MainActivity.items.get(position).setEnabled(isEnable);
        // realtime database
        //onItemUpdated(position);
    }

    @Override
    public void onItemChanged() {
        mAdapter.notifyDataSetChanged();
    }

}
