package com.petweio.projectdoan.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.petweio.projectdoan.Model.Device;
import com.petweio.projectdoan.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;

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
    private String name_type;
    private Device device;
    private ImageButton btnBack;
    private TextView txtNameType;
    private CircleImageView imageDevice;
    private CircleButton btnNotify,btnHistory,btnEdit;
    private LineChart lineChartBattery,lineChartTimeAction;
    private LinearLayout linearLayoutEdit,linearLayoutNotification;
    private AppCompatButton btnNoTiCancel,btnNoTiOk,btnEditCancel,btnEditOk;
    private EditText edtNmDeviceEdit,edtTypeDeviceEdit;
    private static final String URL_AVATAR="https://ui-avatars.com/api/?size=512&background=random&name=";

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
        if (getArguments() != null) {
            device = (Device) getArguments().getSerializable(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_devices, container, false);
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

        linearLayoutEdit = root.findViewById(R.id.linearLayoutEdit);
        linearLayoutNotification = root.findViewById(R.id.linearLayoutNotification);

        btnEditOk = root.findViewById(R.id.btnEditOk);
        btnNoTiOk = root.findViewById(R.id.btnNoTiOk);

        btnEditCancel = root.findViewById(R.id.btnEditCancel);
        btnNoTiCancel = root.findViewById(R.id.btnNoTiCancel);

        edtNmDeviceEdit = root.findViewById(R.id.edtNmDeviceEdit);
        edtTypeDeviceEdit = root.findViewById(R.id.edtTypeDeviceEdit);


        try {
            String c = device.getCodeDevice().split("")[device.getCodeDevice().length()];
            txtNameType.setText(device.getNameDevice()+" - "+device.getTypeDevice());
            imageDevice.setImageBitmap(device.getBitmap());
        }catch (Exception e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }


        lineChartBattery = root.findViewById(R.id.lineChartBattery);
        lineChartTimeAction = root.findViewById(R.id.lineChartTimeAction);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnEdit.setOnClickListener(v-> {
            setColorItem(3);
            linearLayoutEdit.setVisibility(View.VISIBLE);
            btnEditOk.setOnClickListener(v2 -> {
                linearLayoutEdit.setVisibility(View.INVISIBLE);
                setColorItem(0);
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
            btnNoTiOk.setOnClickListener(v1 -> {
                setColorItem(0);
                linearLayoutNotification.setVisibility(View.INVISIBLE);
                Log.d(TAG,"Notify ok");
            });
            btnNoTiCancel.setOnClickListener(v1 -> {
                setColorItem(0);
                linearLayoutNotification.setVisibility(View.INVISIBLE);
                Log.d(TAG,"Notify Cancel");
            });
        });
        btnHistory.setOnClickListener(v-> {
            setColorItem(2);
            new Handler().postDelayed((()->{
                setColorItem(0);
            }),500);
        });
        LineDataSet lineDataSet =new LineDataSet(dataValues(),"Data set 1");
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        lineChartBattery.setData(data);
        lineChartTimeAction.setData(data);
        lineChartBattery.invalidate();
        lineChartTimeAction.invalidate();
        btnBack.setOnClickListener(v-> requireActivity().getSupportFragmentManager().popBackStack());
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
    @NonNull
    private List<Entry> dataValues(){
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(2,20));
        entries.add(new Entry(1,24));
        entries.add(new Entry(2,2));
        entries.add(new Entry(3,10));
        return entries;
    }
}