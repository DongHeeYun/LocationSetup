package com.locationsetup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by inter on 2017-11-07.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public Context context;
        public TextView nameTextView;
        public ImageButton imgBtnWifi, imgBtnBlue, imgBtnLoca, imgBtnNfc;

        public ViewHolder(Context context, View itemView) {
            super(itemView);

            this.context = context;

            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            imgBtnWifi = (ImageButton) itemView.findViewById(R.id.btn_wifi);
            imgBtnBlue = (ImageButton) itemView.findViewById(R.id.btn_blue);
            imgBtnLoca = (ImageButton) itemView.findViewById(R.id.btn_loca);
            imgBtnNfc = (ImageButton) itemView.findViewById(R.id.btn_nfc);

            imgBtnWifi.setOnClickListener(this);
            imgBtnBlue.setOnClickListener(this);
            imgBtnLoca.setOnClickListener(this);
            imgBtnNfc.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context, "push", Toast.LENGTH_SHORT).show();
        }
    }

    private List<LocationItem> locationItems;
    private Context context;

    public ListAdapter(Context context, List<LocationItem> lists){
        locationItems = lists;
        this.context = context;
    }

    private Context getContext(){
        return context;
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View locationItemView = layoutInflater.inflate(R.layout.list_item,parent,false);

        ViewHolder viewHolder = new ViewHolder(context,locationItemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {
        LocationItem locationItem = locationItems.get(position);

        TextView textView = holder.nameTextView;
        textView.setText(locationItem.getLocationName());
        //TODO: button setup
    }

    @Override
    public int getItemCount() {
        return locationItems.size();
    }
}

