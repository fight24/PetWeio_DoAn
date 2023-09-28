package com.petweio.projectdoan.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.petweio.projectdoan.Model.DeviceFeatures;
import com.petweio.projectdoan.R;

import java.util.List;

public class DeviceFeaturesAdapter extends RecyclerView.Adapter<DeviceFeaturesAdapter.DeviceViewHolder>{

    private List<DeviceFeatures> mFeatures ;

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<DeviceFeatures> list){
        this.mFeatures = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_featrures,parent,false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        DeviceFeatures deviceFeatures = mFeatures.get(position);
        if(deviceFeatures == null){
            return;
        }
        holder.mImageView.setImageResource(deviceFeatures.getResourceId());
        holder.title.setText(deviceFeatures.getTitle());
        holder.value.setText(deviceFeatures.getValue());
    }

    @Override
    public int getItemCount() {
        if(mFeatures != null){
            return  mFeatures.size();
        }
        return 0;
    }
    public void updateTextForItem(int position, String newText) {
        if (position >= 0 && position < mFeatures.size()) {
            DeviceFeatures deviceFeatures = mFeatures.get(position);
            if (deviceFeatures != null) {
                deviceFeatures.setValue(newText);
                notifyItemChanged(position);
            }
        }
    }


    public static class DeviceViewHolder extends RecyclerView.ViewHolder{
        private final ImageView mImageView;
        private final TextView title;
        private final TextView value;
        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.id_icon_features);
            title = itemView.findViewById(R.id.txtTitleFeatures);
            value = itemView.findViewById(R.id.txtValueFeatures);
        }
    }
}
