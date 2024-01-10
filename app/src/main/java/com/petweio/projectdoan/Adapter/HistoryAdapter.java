package com.petweio.projectdoan.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.petweio.projectdoan.Model.HistoryInfo;
import com.petweio.projectdoan.R;
import com.petweio.projectdoan.service.BitmapEncode;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>{
    private List<HistoryInfo> infoList ;
//    private ExecutorService executor = Executors.newFixedThreadPool(5);
    @SuppressLint("NotifyDataSetChanged")
    public void setInfoListFilter(List<HistoryInfo> infoListFilter){
        this.infoList = infoListFilter;
        notifyDataSetChanged();
    }

    public HistoryAdapter(List<HistoryInfo> infoList) {
        this.infoList = infoList;
    }

    private static final String URL_AVATAR="https://ui-avatars.com/api/?size=512&background=random&color=fff&name=";

    public List<HistoryInfo> getInfoList() {
        return infoList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setInfoList(List<HistoryInfo> infoList) {
        this.infoList = infoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_items_history,parent,false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryInfo info = infoList.get(position);
        if(info.getImageName() == null){
            Picasso.get().load(URL_AVATAR+info.getNameDevice()+"+"+info.getCodeDevice().split("")[info.getCodeDevice().length()]).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    holder.imageHistory.setImageBitmap(bitmap);

                    info.setBitmapToString(BitmapEncode.convertBitmapToString(bitmap));
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    holder.imageHistory.setImageResource(R.drawable.image_not_found_1150x647);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }else{
            holder.imageHistory.setImageResource(R.drawable.image_not_found_1150x647);
        }
        holder.tvTitle.setText(info.getNameDevice());
        if(info.getProperty().getMessage() != null){
            holder.tvValueOfLat.setText(splitString(info.getProperty().getMessage())[0].replace("[",""));
            holder.tvValueOfLong.setText(splitString(info.getProperty().getMessage())[1]);
        }
        holder.tvValueOfTime.setText(info.getProperty().getDate());
    }
    public String[] splitString(@NonNull String s) {
        return s.split(",");
    }
    @Override
    public int getItemCount() {
        if(infoList != null){
            return  infoList.size();
        }
        return 0;

    }
//    private List<HistoryInfo> fetchData() {
//        // Simulate loading more data from a data source
//        List<HistoryInfo> newData = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            // Create new DataItem objects or fetch from the actual data source
//            HistoryInfo item = infoList.get(infoList.size() +i);
//            newData.add(item);
//        }
//        return newData;
//    }
//    private void loadMoreDataAsync() {
//        // Submit a task to the executor to load more data
//        Future<List<HistoryInfo>> future = executor.submit(this::fetchData);
//
//        // Use the future to handle the result when the task is complete
//        CompletableFuture.supplyAsync(() -> {
//            try {
//                return future.get();
//            } catch (ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//                return Collections.emptyList();
//            }
//        }, executor).thenAccept(newData -> {
//            // Update the UI with the new data (assuming UI updates need to be done on the main thread)
//            infoList.addAll((Collection<? extends HistoryInfo>) newData);
//            notifyItemRangeInserted(infoList.size() - newData.size(), newData.size());
//        });
//    }
    public static class HistoryViewHolder extends RecyclerView.ViewHolder{
        private final CircleImageView imageHistory;
        private final TextView tvTitle;
        private final TextView tvValueOfLat;
        private final TextView tvValueOfLong;
        private final TextView tvValueOfTime;
        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageHistory = itemView.findViewById(R.id.icHistory);
            tvTitle = (TextView) itemView.findViewById(R.id.txtTitleDevice);
            tvValueOfLat = (TextView) itemView.findViewById(R.id.valueOfLat);
            tvValueOfLong = (TextView) itemView.findViewById(R.id.valueOfLng);
            tvValueOfTime = (TextView) itemView.findViewById(R.id.valueOfTime);

        }
    }
}
