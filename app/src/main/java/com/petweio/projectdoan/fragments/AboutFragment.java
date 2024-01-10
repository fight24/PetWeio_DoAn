package com.petweio.projectdoan.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.petweio.projectdoan.Adapter.HistoryAdapter;
import com.petweio.projectdoan.Model.Device;
import com.petweio.projectdoan.Model.HistoryInfo;
import com.petweio.projectdoan.Model.Property;
import com.petweio.projectdoan.R;
import com.petweio.projectdoan.api.ApiManager;
import com.petweio.projectdoan.service.ApiService;
import com.petweio.projectdoan.service.MyViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "user_name";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG= "AboutFragment";

    // TODO: Rename and change types of parameters
    private String username;
    private TextView tvUsername;
    private RecyclerView rvHistory;
    private LinearLayoutManager manager;
    private HistoryAdapter historyAdapter;
    List<Device> devices ;
    List<HistoryInfo> itemList ;
    private SearchView searchView;
    MyViewModel myViewModel;
    ApiService apiService;
//    boolean isScrolling = false;
//    int currentItems,totalItems,scrollOutItems;
    public AboutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.

     * @return A new instance of fragment AboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutFragment newInstance(String param1) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        init(v);
        return v;
    }
    @SuppressLint("SetTextI18n")
    private void init(View rootView){
        apiService = ApiManager.getInstance().getMyApiService();
        tvUsername = rootView.findViewById(R.id.txtUserName);
        rvHistory = rootView.findViewById(R.id.rvListHistory);

        if (getArguments() != null) {
            username = getArguments().getString(ARG_PARAM1);
            assert username != null;
            tvUsername.setText("Hi!,"+upCaseFirstWord(username));
            myViewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
            devices = myViewModel.getDevices();
            for(Device device:devices){
                Log.d(TAG, "device"+device.getNameDevice());
            }

        }

        searchView = rootView.findViewById(R.id.searchView);
        searchView.clearFocus();
        manager = new LinearLayoutManager(requireContext());
        itemList = new ArrayList<>();
        handler();
    }

    private void filterList(String newText) {

        List<HistoryInfo> filterItem = new ArrayList<>();
        for(HistoryInfo info : itemList){
            if(info.getNameDevice().toLowerCase().contains(newText.toLowerCase())){
                filterItem.add(info);
            }
        }
        if(itemList.isEmpty()){
            Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show();

        }else{
            historyAdapter.setInfoListFilter(filterItem);
        }
    }

    public String upCaseFirstWord(@NonNull String name){
        return name.substring(0,1).toUpperCase() + name.substring(1);
    }
    private void handler(){
        Call<List<Property>> call = apiService.get50Property();
        call.enqueue(new Callback<List<Property>>() {
            @Override
            public void onResponse(@NonNull Call<List<Property>> call, @NonNull Response<List<Property>> response) {
                if(response.isSuccessful()){
                    List<Property> properties = response.body();
                    if (properties != null) {
                        for(Property property : properties){
                            if(!devices.isEmpty()){
                                for(Device device:devices){
                                    if(device.getIdDevice() == property.getDevice_id()){
                                        HistoryInfo info = new HistoryInfo(property);
                                        info.setIdDevice(device.getIdDevice());
                                        info.setCodeDevice(device.getCodeDevice());
                                        info.setNameDevice(device.getNameDevice());
                                        itemList.add(info);
                                    }
                                }
                            }
                        }
                    }

                    historyAdapter = new HistoryAdapter(itemList);
//        rvHistory.setLayoutManager(gridLayoutManager);
//                    rvHistory.setHasFixedSize(true);
                    rvHistory.setLayoutManager(manager);
                    rvHistory.setAdapter(historyAdapter);
                    rvHistory.setOverScrollMode(View.OVER_SCROLL_NEVER);
//                    rvHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                        @Override
//                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                            super.onScrollStateChanged(recyclerView, newState);
//                            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
//                                isScrolling = true;
//                            }
//                        }
//
//                        @Override
//                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                            super.onScrolled(recyclerView, dx, dy);
//
//                            // Kiểm tra nếu đang cuộn đến cuối danh sách
//                            currentItems = manager.getChildCount();
//                            totalItems = manager.getItemCount();
//                            scrollOutItems = manager.findFirstVisibleItemPosition();
//                            if(isScrolling && (currentItems +scrollOutItems == totalItems)){
//                                isScrolling = false;
////                                fetchData();
//                            }
//                        }
//                    });

//                    assert properties != null;
//                    for(Property property : properties){
//                        Log.d(TAG, property.getMessage());

//                    }
                    Log.d(TAG,"Response status code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Property>> call, @NonNull Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

    }

//    private void fetchData() {
//        new Handler().postDelayed(()->{
//
//        },3000);
//    }


}