package com.locationsetup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by sky on 2017-11-07.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private static ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position);
        void onItemSwitchCheck(int position, boolean isEnable);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private Context mContext;

    public ListAdapter(Context context) {
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {

        private Context context;

        public TextView name;
        public TextView address;
        public Switch switchBtn;
        public ImageView wifi;
        public ImageView bluetooth;
        public ImageView sound;
        public ImageView brightness;

        public ViewHolder(Context context, View itemView) {
            super(itemView);

            this.context = context;

            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            wifi = itemView.findViewById(R.id.wifi);
            bluetooth = itemView.findViewById(R.id.bluetooth);
            sound = itemView.findViewById(R.id.volume);
            brightness = itemView.findViewById(R.id.bright);
            switchBtn = itemView.findViewById(R.id.switch_btn);
            switchBtn.setOnCheckedChangeListener(this);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClick(getLayoutPosition(), view);
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onItemLongClick(getLayoutPosition());
            return true;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            itemClickListener.onItemSwitchCheck(getLayoutPosition(), b);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.item_setting, parent,false);
        ViewHolder viewHolder = new ViewHolder(context, view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LocationItem item = FileManager.items.get(position);
        holder.name.setText(item.getName());
        holder.address.setText(item.getAddress());

        int types[] = setIconType(item);

        holder.wifi.setImageResource(types[0]);
        holder.bluetooth.setImageResource(types[1]);
        holder.sound.setImageResource(types[2]);
        holder.brightness.setImageResource(types[3]);
        holder.switchBtn.setChecked(item.isEnabled());
    }

    // 각각의 아이템에 대한 이미지 타입 설정
    private int[] setIconType(LocationItem item) {
        int types[] = new int[4];

        switch (item.getWifi()) {
            case 0:
                types[0] = R.drawable.ic_wifi_none;
                break;
            case 1:
                types[0] = R.drawable.ic_wifi_on;
                break;
            case 2:
                types[0] = R.drawable.ic_wifi_off;
                break;
            default:
                types[0] = R.drawable.ic_wifi_none;
        }

        switch (item.getBluetooth()) {
            case 0:
                types[1] = R.drawable.ic_bt_none;
                break;
            case 1:
                types[1] = R.drawable.ic_bt_on;
                break;
            case 2:
                types[1] = R.drawable.ic_bt_off;
                break;
            default:
                types[1] = R.drawable.ic_bt_none;
        }

        switch (item.getSound()) {
            case 0:
                types[2] = R.drawable.ic_vol_none;
                break;
            case 1:
                types[2] = R.drawable.ic_vol_on;
                break;
            case 2:
                types[2] = R.drawable.ic_vol_vib;
                break;
            case 3:
                types[2] = R.drawable.ic_vol_off;
            default:
                types[2] = R.drawable.ic_vol_none;
        }

        int brightness = item.getBrightness();
        if (brightness < 0) types[3] = R.drawable.ic_brt_none;
        else if (brightness < 25) types[3] = R.drawable.ic_brt_0;
        else if (brightness < 50) types[3] = R.drawable.ic_brt_1;
        else if (brightness < 75) types[3] = R.drawable.ic_brt_2;
        else types[3] = R.drawable.ic_brt_3;

        return types;
    }

    @Override
    public int getItemCount() {
        return FileManager.items.size();
    }

    /*public void removeItem(int position) {
        FileManager.items.remove(position);
        notifyItemRemoved(position);
    }*/

    public LocationItem getItem(int position) {
        return FileManager.items.get(position);
    }

}
