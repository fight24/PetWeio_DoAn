package com.petweio.projectdoan.fragments;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.petweio.projectdoan.Model.ApiResponse;
import com.petweio.projectdoan.Model.Device;
import com.petweio.projectdoan.Model.Warning;
import com.petweio.projectdoan.Notification.MyApplication;
import com.petweio.projectdoan.R;
import com.petweio.projectdoan.api.ApiManager;
import com.petweio.projectdoan.service.ApiService;
import com.petweio.projectdoan.service.BitmapEncode;
import com.petweio.projectdoan.service.LocationService;
import com.petweio.projectdoan.service.MqttViewModel;
import com.petweio.projectdoan.service.VerticalTextView;
import com.petweio.projectdoan.splash.SplashActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "name_type";
    private static final String ARG_PARAM2 = "Device";
    private static final String TAG = "DeviceFragment";
    private String nameValue,typeValue;
    private Device device;
    private ImageButton btnBack;
    private TextView txtNameType,tvWarning;
    private CircleImageView imageDevice;
    private CircleButton btnNotify,btnHistory,btnEdit;
    private LineChart lineChartBattery,lineChartTimeAction;
    private LinearLayout linearLayoutEdit,linearLayoutNotification;
    FrameLayout containerChart;
    private AppCompatButton btnNoTiCancel,btnNoTiOk,btnEditCancel,btnEditOk;
    private EditText edtNmDeviceEdit,edtTypeDeviceEdit,edtDistance;
    private ApiService apiService;
    boolean checkWarning;
    private static final String URL_AVATAR="https://ui-avatars.com/api/?size=512&background=random&name=";
    String editTextValue;
    MqttAndroidClient mqttAndroidClient;
    MqttViewModel viewModel;
    List<Entry> entries = new ArrayList<>();
    List<Date> timeList = new ArrayList<>();
    XAxis xAxis;
    TextView xAxisName;
    LineDataSet lineDataSet;
    LineData data;
    // TODO: Rename and change types of parameters

    public DeviceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DeviceFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static DeviceFragment newInstance(Device device) {
        DeviceFragment fragment = new DeviceFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM2, device);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_devices, container, false);

        Log.d(TAG, "onCreateView");
        viewModel = new ViewModelProvider(requireActivity()).get(MqttViewModel.class);
        mqttAndroidClient = viewModel.getMqttData().getValue();
        if(mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            Log.e(TAG, "mqttAndroidClient is already connected");
        }
        else {
            Log.e(TAG, "mqttAndroidClient null or not connected");
        }
        init(root);
        return root;
    }
    @SuppressLint("SetTextI18n")
    private void init(@NonNull View root) {
        btnBack = root.findViewById(R.id.btnBack);
        txtNameType = root.findViewById(R.id.deviceName_type);
        imageDevice = root.findViewById(R.id.img_device);
        btnNotify = root.findViewById(R.id.NotifyButton);
        btnHistory = root.findViewById(R.id.HistoryButton);
        btnEdit = root.findViewById(R.id.EditButton);
        tvWarning = root.findViewById(R.id.tvWarning);
        linearLayoutEdit = root.findViewById(R.id.linearLayoutEdit);
        linearLayoutNotification = root.findViewById(R.id.linearLayoutNotification);
        containerChart = root.findViewById(R.id.viewLineCharts);
        btnEditOk = root.findViewById(R.id.btnEditOk);
        btnNoTiOk = root.findViewById(R.id.btnNoTiOk);

        btnEditCancel = root.findViewById(R.id.btnEditCancel);
        btnNoTiCancel = root.findViewById(R.id.btnNoTiCancel);

        edtNmDeviceEdit = root.findViewById(R.id.edtNmDeviceEdit);
        edtTypeDeviceEdit = root.findViewById(R.id.edtTypeDeviceEdit);
        edtDistance = root.findViewById(R.id.edtDistance);

        apiService = ApiManager.getInstance().getMyApiService();
        if (getArguments() != null) {
            device = (Device) getArguments().getSerializable(ARG_PARAM2);
            try {
                assert device != null;
                txtNameType.setText(device.getNameDevice()+" - "+device.getTypeDevice());
                imageDevice.setImageBitmap(BitmapEncode.convertStringToBitmap(device.getBitmapToString()));
            }catch (Exception e) {
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }
            Log.e(TAG, "is_warning: "+device.isIs_warning());
            checkWarning = device.isIs_warning();
        }
        try {
            mqttAndroidClient.subscribe("devices/"+device.getCodeDevice(),0);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
        lineChartBattery = root.findViewById(R.id.lineChartBattery);
        setupChart();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        btnEdit.setOnClickListener(v-> {
            setColorItem(3);
            linearLayoutEdit.setVisibility(View.VISIBLE);
            btnEditOk.setOnClickListener(v2 -> {
                nameValue = edtNmDeviceEdit.getText().toString().trim();
                typeValue = edtTypeDeviceEdit.getText().toString().trim();
                if((nameValue.length()>=5 && nameValue.length() <=9 )|| (typeValue.length()>=3 && typeValue.length() <=5)){
                    Call<ApiResponse> callUpdateInfo = apiService.updateDeviceInfo(device.getIdDevice(),new Device(upCaseFirstWord(nameValue),typeValue.toUpperCase()));
                    callUpdateInfo.enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(requireContext(),"Name or type updated successfully ",Toast.LENGTH_LONG).show();
                                requireActivity().getSupportFragmentManager().popBackStack();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                            Toast.makeText(requireContext(),"Name or type didn't update ",Toast.LENGTH_LONG).show();
                        }
                    });
                    linearLayoutEdit.setVisibility(View.INVISIBLE);
                    setColorItem(0);
                }
                else if(nameValue.length() == 0 || typeValue.length() == 0){
                    Toast.makeText(requireContext(),"The name or type is empty ",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(requireContext(),"The name from 5 to 9 characters, the type from 3 to 5 characters ",Toast.LENGTH_LONG).show();
                }


                Log.d(TAG,"Edit Ok");
            });
            btnEditCancel.setOnClickListener(v2 -> {
                linearLayoutEdit.setVisibility(View.INVISIBLE);
                setColorItem(0);
                Log.d(TAG,"edit Canceled");
            });
        });
        btnNotify.setOnClickListener(v-> {
            setColorItem(1);
            linearLayoutNotification.setVisibility(View.VISIBLE);
            if(checkWarning){
                edtDistance.setVisibility(View.GONE);
                btnNoTiOk.setOnClickListener(v1 -> {
                    ((MyApplication)requireActivity().getApplication()).cancelNotification(1);
                    checkWarning = false;
                    Intent intent = new Intent(requireContext(), LocationService.class);
                    requireActivity().stopService(intent);
                   Call<ApiResponse> call =  apiService.updateDeviceWarning(device.getIdDevice(),new Warning(checkWarning));
                   call.enqueue(new Callback<ApiResponse>() {
                       @Override
                       public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                           if(response.isSuccessful()){
                               Log.d(TAG, "Successfully updated device"+response.body());
                           }
                       }

                       @Override
                       public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {

                       }
                   });
                    setColorItem(0);
                    linearLayoutNotification.setVisibility(View.INVISIBLE);
                });
                // Firebase Messaging Service đang hoạt động
                // Bạn có thể thực hiện các hành động cần thiết ở đây
                tvWarning.setText("Do you want to disable alerts for this device?");

            }else {
                edtDistance.setVisibility(View.VISIBLE);
                tvWarning.setText("Do you want to enable alerts for this device?");
                btnNoTiOk.setOnClickListener(v1 -> {
                    editTextValue = edtDistance.getText().toString().trim();
                    if (!editTextValue.isEmpty()){
                        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edtDistance.getWindowToken(), 0);
//                        ((MyApplication)requireActivity().getApplication()).triggerNotificationWithBackStack(SplashActivity.class,
//                                getString(R.string.NEWS_CHANNEL_ID),
//                                "Notification",
//                                "You enabled warning in "+device.getNameDevice(),
//                                "You enabled warning in "+device.getNameDevice(),
//                                NotificationCompat.PRIORITY_HIGH,
//                                true,
//                                getResources().getInteger(R.integer.notificationId),
//                                PendingIntent.FLAG_UPDATE_CURRENT);


                        Call<ApiResponse> callUpdateDistance = apiService.updateDeviceDistance(device.getIdDevice(),new Device(Float.parseFloat(editTextValue)));
                        callUpdateDistance.enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                                if(response.isSuccessful()){


                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                                Toast.makeText(requireContext(), "Error update distance "+ t, Toast.LENGTH_SHORT).show();
                            }
                        });

                        Log.d(TAG,"Notify ok");

                        checkWarning = true;
                        Call<ApiResponse> call =  apiService.updateDeviceWarning(device.getIdDevice(),new Warning(checkWarning));
                        call.enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                                if(response.isSuccessful()){
                                    Log.d(TAG, "Successfully updated device"+response.body());
                                    ((MyApplication)requireActivity().getApplication()).triggerNotification(SplashActivity.class,
                                            getString(R.string.NEWS_CHANNEL_ID),
                                            "Notification",
                                            "You enabled warning in "+device.getNameDevice(),
                                            "You enabled warning in "+device.getNameDevice(),
                                            NotificationCompat.PRIORITY_HIGH,
                                            true,
                                            getResources().getInteger(R.integer.notificationId),
                                            PendingIntent.FLAG_UPDATE_CURRENT);
                                    setColorItem(0);
                                    linearLayoutNotification.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                                Toast.makeText(requireContext(), "Error update is_warning"+ t, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        Toast.makeText(requireContext(), "Please enter your complete information", Toast.LENGTH_SHORT).show();
                    }

                });


            }





            btnNoTiCancel.setOnClickListener(v1 -> {
                setColorItem(0);
                linearLayoutNotification.setVisibility(View.INVISIBLE);
                Log.d(TAG,"Notify Cancel");
            });
        });
        btnHistory.setOnClickListener(v-> {
            setColorItem(2);
            new Handler().postDelayed((()-> setColorItem(0)),500);
        });



//        if(!timeList.isEmpty()){
//            xAxis = lineChartBattery.getXAxis();
//            xAxis.setValueFormatter(new MyAxisValueFormatter(timeList, null));
//        }
        // Định cấu hình trục X


        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG, "Connection lost " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String msg = new String(message.getPayload());
                Log.e(TAG, "value " + msg);
                addEntriesToChart(updateLatLng(msg));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        btnBack.setOnClickListener(v-> requireActivity().getSupportFragmentManager().popBackStack());
    }
    String upCaseFirstWord(@NonNull String name){
        return name.substring(0,1).toUpperCase() + name.substring(1);
    }
    private void setColorItem(int i){
        switch (i){
            case 1:
                btnEdit.setImageResource(R.drawable.baseline_mode_edit_24);
                btnHistory.setImageResource(R.drawable.ic_history);
                btnNotify.setImageResource(R.drawable.baseline_notifications_24_selected);
                break;
            case 2 :
                btnEdit.setImageResource(R.drawable.baseline_mode_edit_24);
                btnHistory.setImageResource(R.drawable.ic_history_selected);
                btnNotify.setImageResource(R.drawable.baseline_notifications_24);
                break;
            case 3:
                btnEdit.setImageResource(R.drawable.baseline_mode_edit_24_selected);
                btnHistory.setImageResource(R.drawable.ic_history);
                btnNotify.setImageResource(R.drawable.baseline_notifications_24);
                break;
            default:
                btnEdit.setImageResource(R.drawable.baseline_mode_edit_24);
                btnHistory.setImageResource(R.drawable.ic_history);
                btnNotify.setImageResource(R.drawable.baseline_notifications_24);
        }
    }
    private void addEntriesToChart(float[] array){

//            Date date = new Date();
            Log.d(TAG, "addEntriesToChart: "+ array[0]+" "+ array[1]);
            entries.add(new Entry(array[0],array[1]));

            lineDataSet.notifyDataSetChanged();
            data.notifyDataChanged();
            lineChartBattery.notifyDataSetChanged();
//            // Di chuyển tới giá trị mới nhất
            lineChartBattery.moveViewToX(entries.size() - 1);

            // Refresh biểu đồ
            lineChartBattery.invalidate();

}

    @SuppressLint({"RtlHardcoded", "SetTextI18n"})
    private void setupChart() {
//        Date date2 = new Date();
        entries.add(new Entry(0f,0f));
        lineDataSet =new LineDataSet(entries,"Location");
        lineDataSet.setLineWidth(3f);
        configureDataSet(lineDataSet);
        List<ILineDataSet> dataSets = new ArrayList<>();
//        lineDataSet.setColors(Color.parseColor("#49BEFF"));
//        lineDataSet.setCircleColor(Color.parseColor("#0D5D98"));
        dataSets.add(lineDataSet);
        data = new LineData(dataSets);
        lineChartBattery.setData(data);
        //config chart
        lineChartBattery.getDescription().setEnabled(false);
        lineChartBattery.setTouchEnabled(true);
        lineChartBattery.setDragEnabled(true);
        lineChartBattery.setScaleEnabled(true);
        lineChartBattery.setPinchZoom(true);
        lineChartBattery.setDrawGridBackground(false);

        xAxis = lineChartBattery.getXAxis();
//        xAxis.setValueFormatter(new MyAxisValuesFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

//        lineChartBattery.getAxisLeft().setDrawGridLines(false);// left
        lineChartBattery.getAxisRight().setEnabled(false);//right
        xAxisName = new TextView(getActivity());
        xAxisName.setText("Latitude");
        xAxisName.setTypeface(Typeface.create(String.valueOf(requireContext().getAssets()),R.style.btnMontserratsemiBold14));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        params.setMargins(0, 0, 0, 20);

        VerticalTextView yAxisName = new VerticalTextView(getActivity(),null);
        yAxisName.setText("Longitude");
        yAxisName.setTypeface(Typeface.create(String.valueOf(requireContext().getAssets()),R.style.btnMontserratsemiBold14));
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params2.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        containerChart.addView(xAxisName,params);
        containerChart.addView(yAxisName,params2);
//        lineChartBattery.getAxisLeft().setAxisMaximum(100f);
//        lineChartBattery.getAxisLeft().setAxisMinimum(0f);
//        lineChartBattery.getAxisRight().setAxisMinimum(0f);
//        lineChartBattery.getAxisRight().setAxisMaximum(100f);
        lineChartBattery.invalidate();
    }
    private float updateBat(String destinationInfo) {
        // Xử lý và cập nhật điểm đích trên bản đồ ở đây
        destinationInfo = destinationInfo.replaceAll(" ", "").replace("[", "").replace("]", "");
        String[] destinationString = destinationInfo.split(",");
        return Float.parseFloat(destinationString[2]);
    }
    private float[] updateLatLng(String destinationInfo) {
        // Xử lý và cập nhật điểm đích trên bản đồ ở đây
        destinationInfo = destinationInfo.replaceAll(" ", "").replace("[", "").replace("]", "");
        String[] destinationString = destinationInfo.split(",");
        return new float[]{Float.parseFloat(destinationString[0]),Float.parseFloat(destinationString[1])};
    }
    private void configureDataSet(@NonNull LineDataSet dataSet) {
        dataSet.setColor(Color.parseColor("#0D5D98"));
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#49BEFF"));
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
    }
    @Override
    public void onResume() {
        super.onResume();
      Log.d(TAG, "onResume: "+device.toString());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: "+device.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mqttAndroidClient.unsubscribe("devices/"+device.getCodeDevice());
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    private static class MyAxisValuesFormatter extends ValueFormatter {

        @Override
        public String getFormattedValue(float value) {

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            return sdf.format(value);

        }

    }

}
