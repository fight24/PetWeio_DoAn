package com.petweio.projectdoan.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.petweio.projectdoan.Model.DeviceMenu;
import com.petweio.projectdoan.R;

import java.util.List;

public class DeviceMenuAdapter extends RecyclerView.Adapter<DeviceMenuAdapter.DeviceMenuViewHolder>{
    private List<DeviceMenu> mMenu ;
    private RecyclerViewClickListener mClickListener;

    public DeviceMenuAdapter(List<DeviceMenu> mMenu, RecyclerViewClickListener mClickListener) {
        this.mMenu = mMenu;
        this.mClickListener = mClickListener;
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<DeviceMenu> list){
        this.mMenu = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeviceMenuAdapter.DeviceMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_device,parent,false);
        return new DeviceMenuAdapter.DeviceMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceMenuAdapter.DeviceMenuViewHolder holder, int position) {
        DeviceMenu deviceMenu = mMenu.get(position);
        if(deviceMenu == null){
            return;
        }
        holder.mImageView.setImageResource(deviceMenu.getResourceId());
        holder.title.setText(deviceMenu.getTitle());

        holder.cardView.setOnClickListener(v-> mClickListener.onItemClick(deviceMenu));

    }

    @Override
    public int getItemCount() {
        if(mMenu != null){
            return  mMenu.size();
        }
        return 0;
    }

    public void setClickListener(RecyclerViewClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }


    public static class DeviceMenuViewHolder extends RecyclerView.ViewHolder{
        private final CardView cardView;
        private final ImageView mImageView;
        private final TextView title;
        public DeviceMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewLayout);
            mImageView = itemView.findViewById(R.id.icDeviceMenu);
            title = itemView.findViewById(R.id.txtTitleDevice);

        }


    }
}