package com.petweio.projectdoan.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.petweio.projectdoan.Model.Device;
import com.petweio.projectdoan.R;
import com.petweio.projectdoan.service.BitmapEncode;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import at.markushi.ui.CircleButton;

public class DeviceMenuAdapter extends RecyclerView.Adapter<DeviceMenuAdapter.DeviceMenuViewHolder>{
    private List<Device> mMenu ;
    private RecyclerViewClickListener mClickListener,btnFindClick,btnInfoClick;
    private static final String URL_AVATAR="https://ui-avatars.com/api/?size=512&background=random&color=fff&name=";

    public DeviceMenuAdapter(List<Device> mMenu, RecyclerViewClickListener mClickListener) {
        this.mMenu = mMenu;
        this.mClickListener = mClickListener;
    }

    public DeviceMenuAdapter() {
    }

    public DeviceMenuAdapter(List<Device> mMenu) {
        this.mMenu = mMenu;
    }

    public void setClickListener(RecyclerViewClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public void setBtnFindClick(RecyclerViewClickListener btnFindClick) {
        this.btnFindClick = btnFindClick;
    }

    public void setBtnInfoClick(RecyclerViewClickListener btnInfoClick) {
        this.btnInfoClick = btnInfoClick;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Device> list){
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
        Device deviceMenu = mMenu.get(position);
        if(deviceMenu == null){
            return;
        }
        if(deviceMenu.getImageName().equals("None")){
            Picasso.get().load(URL_AVATAR+deviceMenu.getNameDevice()+"+"+deviceMenu.getCodeDevice().split("")[deviceMenu.getCodeDevice().length()]).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    holder.mImageView.setImageBitmap(bitmap);

                    deviceMenu.setBitmapToString(BitmapEncode.convertBitmapToString(bitmap));
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    holder.mImageView.setImageResource(R.drawable.image_not_found_1150x647);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }else{
            holder.mImageView.setImageResource(R.drawable.image_not_found_1150x647);
        }
        holder.title.setText(deviceMenu.getNameDevice());
        holder.cardView.setOnClickListener(v-> mClickListener.onItemClick(deviceMenu));
        holder.btnInfoDevice.setOnClickListener(v-> btnInfoClick.onItemClick(deviceMenu));
        holder.btnFindDevice.setOnClickListener(v-> btnFindClick.onItemClick(deviceMenu));
    }

    @Override
    public int getItemCount() {
        if(mMenu != null){
            return  mMenu.size();
        }
        return 0;
    }


    public static class DeviceMenuViewHolder extends RecyclerView.ViewHolder{
        private final CardView cardView;
        private final ImageView mImageView;
        private final TextView title;
        private final CircleButton btnFindDevice,btnInfoDevice;
        public DeviceMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewLayout);
            mImageView = itemView.findViewById(R.id.icDeviceMenu);
            title = itemView.findViewById(R.id.txtTitleDevice);
            btnFindDevice = itemView.findViewById(R.id.btnFindDevice);
            btnInfoDevice = itemView.findViewById(R.id.btnInfoDevice);

        }


    }
}