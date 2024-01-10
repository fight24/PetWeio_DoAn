package com.petweio.projectdoan.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.petweio.projectdoan.Model.Device;
import com.petweio.projectdoan.R;
import com.petweio.projectdoan.service.BitmapEncode;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeCategoryAdapter  extends RecyclerView.Adapter<HomeCategoryAdapter.CategoryViewHolder>{
    private List<Device> homeCategoryList;
    private static final String URL_AVATAR="https://ui-avatars.com/api/?size=512&background=random&color=fff&name=";

    // Các phương thức khác của Adapter
    @SuppressLint("NotifyDataSetChanged")
    public void setBattery(int position, int battery) {
        if (position >= 0 && position < homeCategoryList.size()) {
            homeCategoryList.get(position).setBatteryImg(battery);
            notifyDataSetChanged();
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void changeStatus(int position, boolean status) {
        if (position >= 0 && position < homeCategoryList.size()) {
            homeCategoryList.get(position).setIs_status(status);
            notifyDataSetChanged();
        }
    }
    public HomeCategoryAdapter() {
    }

    public List<Device> getDataList() {
        return homeCategoryList;
    }
    private ButtonOnClickListener mClickListener;
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Device> list){
        this.homeCategoryList = list;
        notifyDataSetChanged();
    }

    public List<Device> getHomeCategoryList() {
        return homeCategoryList;
    }

    public HomeCategoryAdapter(List<Device> homeCategoryList, ButtonOnClickListener mClickListener) {
        this.homeCategoryList = homeCategoryList;
        this.mClickListener = mClickListener;
    }

    public HomeCategoryAdapter(List<Device> homeCategoryList) {
        this.homeCategoryList = homeCategoryList;
    }

    public void setClickListener(ButtonOnClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_home,parent,false);
        return new CategoryViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Device category = homeCategoryList.get(position);
        if(category == null){
            return;
        }
        holder.txtNameDevice.setText(category.getNameDevice());

        switch (category.getBatteryImg()){
            case 100 : holder.battery.setImageResource(R.drawable.ba_full_battery);
                break;
            case 75: holder.battery.setImageResource(R.drawable.ba_battery);
                break;
            case 50: holder.battery.setImageResource(R.drawable.ba_half_battery);
                break;
            case 25: holder.battery.setImageResource(R.drawable.ba_low_battery);
                break;
            case 0: holder.battery.setImageResource(R.drawable.ba_empty_battery);
                break;

        }
        if(category.isIs_status()){
            holder.statusImg.setImageResource(R.color.green_status);
        }else{
            holder.statusImg.setImageResource(R.color.red_status);
        }
        if(category.getImageName() == null){
            try{
                Picasso.get().load(URL_AVATAR+category.getNameDevice()+"+"+category.getCodeDevice().split("")[category.getCodeDevice().length()]).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        holder.DeviceHomeImg.setImageBitmap(bitmap);
                        category.setBitmapToString(BitmapEncode.convertBitmapToString(bitmap));
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        holder.DeviceHomeImg.setImageResource(R.drawable.image_not_found_1150x647);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }catch (Exception e) {
                Log.e("DeBug", "onPrepareLoad error"+e);
            }

       }else{
            holder.DeviceHomeImg.setImageResource(R.drawable.image_not_found_1150x647);
        }
        holder.imgNotification.setImageResource(R.drawable.notifications_color);
        if(category.isIs_warning()){
            holder.imgNotification.setVisibility(View.VISIBLE);
        }else{
            holder.imgNotification.setVisibility(View.INVISIBLE);
        }
        holder.txtType.setText(category.getTypeDevice());
        holder.btnNotification.setText("Edit");
        holder.btnNotification.setOnClickListener(v-> mClickListener.onClick(category,position));

    }

    @Override
    public int getItemCount() {
        if(homeCategoryList != null){
            return homeCategoryList.size();
        }
        return 0;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView statusImg;
        private final ImageView battery;
        private final ImageView imgNotification;

        private final CircleImageView DeviceHomeImg;
        private final AppCompatButton btnNotification;
        private final TextView txtNameDevice;
        private final TextView txtType;

        public CategoryViewHolder(@NonNull View itemView ) {
            super(itemView);
            statusImg = itemView.findViewById(R.id.statusDevice);
            battery = itemView.findViewById(R.id.idImgBattery);
            DeviceHomeImg = itemView.findViewById(R.id.imgDeviceHome);
            txtNameDevice = itemView.findViewById(R.id.txtNameDeviceHome);
            btnNotification = itemView.findViewById(R.id.btnNotification);
            txtType = itemView.findViewById(R.id.txtTypeDeviceHome);
            imgNotification = itemView.findViewById(R.id.idImgNotification);

        }


    }
    public interface ButtonOnClickListener{
        void onClick(Device device,int position);
    }

}
