package com.petweio.projectdoan.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.petweio.projectdoan.Adapter.HomeCategoryAdapter;
import com.petweio.projectdoan.Model.ApiResponse;
import com.petweio.projectdoan.Model.Device;
import com.petweio.projectdoan.R;
import com.petweio.projectdoan.api.ApiManager;
import com.petweio.projectdoan.service.ApiService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private static final String URL_AVATAR="https://ui-avatars.com/api/?size=512&background=random&name=";

    private static final String ARG_PARAM_MQTT = "MQTT";
    private static final String ARG_PARAM_USER_NAME = "username";
    private String userName;
    private RecyclerView rvListDeviceGrid;
    private GridLayoutManager gridLayoutManager;
    private HomeCategoryAdapter homeCategoryAdapter;
    private TextView txtUserName;
    private FloatingActionButton fatAdd;
    private CircleImageView imgUser;
    private TextView txtEmpty;
    private LinearLayout loadingScanQr,containerItems;

    private ActivityResultLauncher<String> requestPermissionLauncher ;
    private ActivityResultLauncher<ScanOptions> qrCodeLauncher ;

    ApiService apiService;
    private void setResult(String contents){
        Log.d(TAG,"setResult: "+contents);
        addDeviceToUserName(userName,contents);
  
    }
    private void setValueScan(){
        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
                    if(isGranted){
                        showCamera();
                    }else{
                        Toast.makeText(getContext(),"Permission denied",Toast.LENGTH_SHORT).show();
                    }
                });
        qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
            if(result.getContents() == null){
                Toast.makeText(requireContext(),"Cancelled",Toast.LENGTH_SHORT).show();
            }else{
                setResult(result.getContents());
            }
        });
    }
    private void showCamera(){
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan QR Code from device");
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);
        qrCodeLauncher.launch(options);
    }
    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String name) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_USER_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");


        setValueScan();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        init(rootView);
        Log.d(TAG, "onCreateView");
        return rootView;
    }
    @SuppressLint("SetTextI18n")
    private void init(View rootView){
        fatAdd = rootView.findViewById(R.id.fatAdd);
        txtUserName = rootView.findViewById(R.id.txtUserName);
        rvListDeviceGrid = rootView.findViewById(R.id.rvListDeviceGrid);
        gridLayoutManager = new GridLayoutManager(rootView.getContext(),2);
        txtEmpty = rootView.findViewById(R.id.txtEmpty);

        loadingScanQr = rootView.findViewById(R.id.loadingScanQr);
        containerItems = rootView.findViewById(R.id.containerItems);

        imgUser = rootView.findViewById(R.id.imgUser);
        homeCategoryAdapter = new HomeCategoryAdapter();
        rvListDeviceGrid.setLayoutManager(gridLayoutManager);
        rvListDeviceGrid.setAdapter(homeCategoryAdapter);
        rvListDeviceGrid.setOverScrollMode(View.OVER_SCROLL_NEVER);
        apiService = ApiManager.getInstance().getMyApiService();
        Bundle args = this.getArguments();
        if (args != null) {
            userName = args.getString(ARG_PARAM_USER_NAME);
        }
        if(userName == null) {
            userName = "Tester";
        }
        txtUserName.setText("Hi!,"+upCaseFirstWord(userName));
        setAvatar(userName,imgUser);
        loadingItem();
    }
    private void showDevice(String name){
        Call<List<Device>> call = apiService.showDevicesFromUser(name);
     
        call.enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(@NonNull Call<List<Device>> call, @NonNull Response<List<Device>> response) {
             if(response.isSuccessful()){
                 List<Device> deviceList = response.body();
                 Log.d(TAG,"Ok show : "+response.body().toString());
                 if(deviceList != null) {
                     addDeviceFromQRCode(deviceList);
                 }
             }

            }

            @Override
            public void onFailure(@NonNull Call<List<Device>> call, @NonNull Throwable t) {
                Log.d(TAG, "show Devices : onFailure: ");
            }
        });
    }
    private void setAvatar(String name,CircleImageView image){
        requireActivity().runOnUiThread(()->{
            Picasso.get().load(URL_AVATAR+name).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    image.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    Log.e(TAG,"avatar failed: "+e.getMessage());

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        });
    }
    private void addDeviceToUserName(String name,String code) {
        Call<ApiResponse> call = apiService.addDeviceToUserByUserName(name,new Device(code));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "Response: "+response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "add device : onFailure: ");
            }
        });
    }
    private void checkDevice(@NonNull List<Device> list){
        if(!list.isEmpty()){
            rvListDeviceGrid.setVisibility(View.VISIBLE);
            txtEmpty.setVisibility(View.GONE);
        }
    }
    public int getRandomColor() {
        Random random = new Random();
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
    String upCaseFirstWord(@NonNull String name){
        return name.substring(0,1).toUpperCase() + name.substring(1);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        fatAdd.setOnClickListener(v-> {
            checkPermissionAndActivity(requireContext());
            Log.d(TAG,"on click ok");
        });

        homeCategoryAdapter.setClickListener( (device, position) -> {
            Log.d(TAG, "On device " + device);
                DeviceFragment deviceFragment = DeviceFragment.newInstance(device);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, deviceFragment)
                        .addToBackStack(null) // Để có thể quay lại Fragment A
                        .commit();
//                homeCategoryAdapter.notifyItemChanged(position);

        });

    }

    private void checkPermissionAndActivity(Context context) {
        if(ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED){
            showCamera();
        }else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
            Toast.makeText(context,"Camera permission required",Toast.LENGTH_SHORT).show();
        }else{
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addDeviceFromQRCode(List<Device> list) {
//            devices.add(new DeViceMenuV2(R.color.green_status, R.drawable.ba_battery, R.drawable.images, "Devices01", "None"));
            homeCategoryAdapter.setData(list);
            Objects.requireNonNull(rvListDeviceGrid.getAdapter()).notifyDataSetChanged();
            checkDevice(list);

        }
    private void loadingItem(){
        loadingScanQr.setVisibility(View.VISIBLE);
        containerItems.setVisibility(View.INVISIBLE);
        showDevice(userName);
        new Handler().postDelayed(()->{

            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

// Đặt thời gian thực hiện animation (millisecond)
            alphaAnimation.setDuration(1000);

// Đặt lắng nghe sự kiện kết thúc animation (nếu cần)
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // Animation bắt đầu
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Animation kết thúc, bạn có thể ẩn View ở đây
                    loadingScanQr.setVisibility(View.GONE);
                    containerItems.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // Animation lặp lại (nếu có)
                }
            });

// Áp dụng animation vào View
            loadingScanQr.startAnimation(alphaAnimation);

        },5000);
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        loadingItem();

        Log.d(TAG, "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}