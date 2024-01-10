package com.petweio.projectdoan.fragments;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.style.FoldingCube;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.utils.PolylineUtils;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfMeasurement;
import com.petweio.projectdoan.Adapter.DeviceFeaturesAdapter;
import com.petweio.projectdoan.Adapter.DeviceMenuAdapter;
import com.petweio.projectdoan.Model.Device;
import com.petweio.projectdoan.Model.DeviceFeatures;
import com.petweio.projectdoan.Model.LastProperty;
import com.petweio.projectdoan.R;
import com.petweio.projectdoan.api.ApiManager;
import com.petweio.projectdoan.service.ApiService;
import com.petweio.projectdoan.service.BitmapEncode;
import com.petweio.projectdoan.service.LatLngEvaluator;
import com.petweio.projectdoan.service.MqttViewModel;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements PermissionsListener, OnMapReadyCallback {
    private static final String TAG = "MapFragment";

    private static final String ICON_DESTINATION_DEVICE_V1_ID = "destinationDefault-deviceV1-icon-Id";
    private static final String ICON_DESTINATION_Update_DEVICE_V1_ID = "destinationUpdate-deviceV1-icon-Id";
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final int SLIDE_DOWN = 1;
    private static final int SLIDE_UP = 2;
    private static final int SLIDE_LEFT_OUT = 3;
    private static final int SLIDE_LEFT_IN = 4;
    private static final String ARG_PARAM_USER_NAME = "username";
    private MqttAndroidClient mqttAndroidClient;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private RecyclerView rvFeatures, listDevice;
    private DeviceFeaturesAdapter deviceFeaturesAdapter;
    private DeviceMenuAdapter deviceMenuAdapter;
    PermissionsManager permissionsManager;
    LocationComponent locationComponent;
    private ConstraintLayout containerMap;
    private LinearLayout loading;
    private TextView txtTitleDevice, txtValueType;
    private Point origin;
    private SymbolManager symbolManager;
    private Symbol symbol;
    private DirectionsRoute walkingRoute;

    FloatingActionButton fat;
    List<Symbol> symbolsToDelete = new ArrayList<>(), symbolsUpdate = new ArrayList<>();
    LinearLayoutManager linearLayoutHorizontalManager, linearLayoutVerticalManager;
    ImageButton imgBtnClose, btnMenu;
    AppCompatButton btnFind;
    FrameLayout containerFeatures;
    View clickInterceptor;
    Animation animation;
    Animation.AnimationListener animationListener;
    CircleImageView deviceImage;
    ProgressBar loadingProgressBar;
    String[] topics;
    int[] qos;
    double distance = 0d, journey = 0d;
    Device deviceCheck;
    ApiService apiService;
    String userName;
    Map<String, Point> deviceLocationUpdate = new HashMap<>();

    Map<String, List<DeviceFeatures>> deviceFeaturesMap = new HashMap<>();
    LastProperty lastProperty;
    MqttViewModel viewModel;
    List<Device> deviceList;
    private static final String ARG_MQTT_CLIENT_JSON = "mqtt_client_json";


    private final MapboxMap.OnMoveListener onMoveListener = new MapboxMap.OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector detector) {

        }

        @Override
        public void onMove(@NonNull MoveGestureDetector detector) {
            fat.show();
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector detector) {

        }
    };

    public MapFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    @NonNull
    public static MapFragment newInstance(String name) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_USER_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    public static MapFragment newInstanceMqtt(String encodeMqtt) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MQTT_CLIENT_JSON, encodeMqtt);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(requireActivity(), getString(R.string.key_mapbox));
//        viewModel = new ViewModelProvider(requireActivity()).get(MqttViewModel.class);
////        mqttAndroidClient = viewModel.getMqttClient();
//        viewModel.getMqttData().observe(getViewLifecycleOwner(), data -> {
//            // Xử lý dữ liệu từ MQTT ở đây
//            mqttAndroidClient = data;
//        });


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        apiService = ApiManager.getInstance().getMyApiService();
        initView(rootView, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MqttViewModel.class);
//        mqttAndroidClient = viewModel.getMqttClient();
        mqttAndroidClient = viewModel.getMqttData().getValue();
        Log.d(TAG, "onCreateView");
        return rootView;
    }


    private void initView(@NonNull View rootView, Bundle savedInstanceState) {

        mapView = rootView.findViewById(R.id.mapView);
        fat = rootView.findViewById(R.id.fat);

        rvFeatures = rootView.findViewById(R.id.recyclerView);
        listDevice = rootView.findViewById(R.id.listViewDevice);

        imgBtnClose = rootView.findViewById(R.id.btnImgClose);
        btnMenu = rootView.findViewById(R.id.btnMenu);
        btnFind = rootView.findViewById(R.id.btnFind);

        containerFeatures = rootView.findViewById(R.id.containerFeatures);
        clickInterceptor = rootView.findViewById(R.id.clickInterceptor);

        deviceFeaturesAdapter = new DeviceFeaturesAdapter();

        linearLayoutHorizontalManager = new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false);
        linearLayoutVerticalManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);

        txtTitleDevice = rootView.findViewById(R.id.txtTitle);
        txtValueType = rootView.findViewById(R.id.txtValueType);
        deviceImage = rootView.findViewById(R.id.imageDevice);
        Log.d(TAG, "LinearLayoutManager  " + requireContext());


        fat.hide();
        containerMap = rootView.findViewById(R.id.containerMap);
        loading = rootView.findViewById(R.id.loading);
        loadingProgressBar = rootView.findViewById(R.id.loadingProgressBar);
        loadingProgressBar.setIndeterminateDrawable(new FoldingCube());

        mapView.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setRecyclerView();
        Log.d(TAG, "onViewCreated");

        if (mqttAndroidClient != null) {
            Log.e(TAG, "mqttAndroidClient is running");
            mqttAndroidClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.e(TAG, "Connection lost: " + cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    Log.d(TAG, "Topic: " + topic);
                    String msg = new String(message.getPayload());
                    Log.d(TAG, msg);

                    assert mapboxMap.getLocationComponent().getLastKnownLocation() != null;
                    origin = Point.fromLngLat(mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude()
                            , mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude());
                    Log.d(TAG, "origin: " + origin);
                    for (int i = 0; i < deviceList.size(); i++) {
                        if (("devices/" + deviceList.get(i).getCodeDevice()).equals(topic)) {
                            Log.e(TAG, "symbolsUpdate 1: " + symbolsUpdate);
                            updateSymbolsLocation(symbolsUpdate.get(i), new LatLng(updateDestinationOnMap(msg).latitude(), updateDestinationOnMap(msg).longitude()));
//                            symbolsUpdate.get(i).setLatLng(new LatLng(updateDestinationOnMap(msg).latitude(),updateDestinationOnMap(msg).longitude()));

                            Log.e(TAG, "symbolsUpdate 2: " + symbolsUpdate);
                        }
                    }
                    deviceLocationUpdate.put(topic, updateDestinationOnMap(msg));
                    Log.d(TAG, "key : " + topic + "Value: " + deviceLocationUpdate.get(topic));
                    updateValuesFeature(topic, origin, deviceLocationUpdate.get(topic), deviceLocationUpdate.get(topic + "-origin"));

//                     getSingleRoute(deviceLocationUpdate.get(topic+"-origin"),deviceLocationUpdate.get(topic));

//                    destinationDefault = updateDestinationOnMap(msg);
//
//
//
////                    symbolDefault = updateSymbolDestination(ICON_DESTINATION_DEVICE_V1_ID, destinationDefault, symbolDefault);
//                    Log.d(TAG, "destinationUpdate: " + destinationDefault);
//
//                    distance = TurfMeasurement.distance(origin, destinationDefault, TurfConstants.UNIT_DEFAULT);
//                    Log.d(TAG, "distance mqtt: " + distance);
//                    symbol = updateSymbolDestination(ICON_DESTINATION_Update_DEVICE_V1_ID, destinationDefault, symbol);
//

//                    getSingleRoute(destinationOrigin, destinationDefault);
//                    Log.d(TAG, "Animate: " );
////                    setDotTwoPoints(destinationOrigin, destinationDefault);
////                    animateSymbolMovement(topic,new LatLng(destinationDefault.latitude(),destinationDefault.longitude()),ICON_DESTINATION_DEVICE_V1_ID);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        } else {
            Log.e(TAG, "Mqtt Client null ");
        }

        mapView.getMapAsync(this);

    }

    private void updateSymbolsLocation(@NonNull Symbol s, LatLng updateLocation) {
        ValueAnimator animator = ValueAnimator.ofObject(new LatLngEvaluator(), s.getLatLng(), updateLocation);
        animator.setDuration(5000);
        animator.addUpdateListener(animation -> {
            LatLng animatedLatLng = (LatLng) animation.getAnimatedValue();
            s.setLatLng(animatedLatLng);
            symbolManager.update(s);
            Log.d(TAG, "add Update Listener");
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Animation completed, update the symbol's final position
//                    symbolManager.update(s);
                s.setLatLng(updateLocation);
                symbolManager.update(s);

                Log.d(TAG, "add Listener");
            }
        });
        animator.start();
    }


    // Gọi hàm này để bắt đầu quá trình xóa


    private void setRecyclerView() {
        // feature
        rvFeatures.setLayoutManager(linearLayoutHorizontalManager);
        deviceFeaturesAdapter.setData(getListFeatures());
        rvFeatures.setAdapter(deviceFeaturesAdapter);
        rvFeatures.setOverScrollMode(View.OVER_SCROLL_NEVER);


        imgBtnClose.setOnClickListener(v -> setAnimation(SLIDE_DOWN, containerFeatures));
        //menu
        listDevice.setLayoutManager(linearLayoutVerticalManager);
        deviceMenuAdapter = new DeviceMenuAdapter();
        Bundle args = this.getArguments();
        if (args != null) {
            userName = args.getString(ARG_PARAM_USER_NAME);
        }
        //click items
        Log.d(TAG, "text: " + txtTitleDevice);
        listDevice.setAdapter(deviceMenuAdapter);
        listDevice.setOverScrollMode(View.OVER_SCROLL_NEVER);
//        showDevice(userName);

    }


    private void showDevice(String name) {
        Call<List<Device>> call = apiService.showDevicesFromUser(name);

        call.enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(@NonNull Call<List<Device>> call, @NonNull Response<List<Device>> response) {
                if (response.isSuccessful()) {
                    deviceList = response.body();
                    Log.d(TAG, "Ok show");
                    if (deviceList != null) {
                        topics = getArrayTopic(deviceList);
                        try {
                            Log.d(TAG, "Ok sub:" + Arrays.toString(topics));
                            mqttAndroidClient.subscribe(topics, qos);
                            for (String topic : topics) {
                                deviceLocationUpdate.put(topic, null);

                            }


                        } catch (MqttException e) {
                            throw new RuntimeException(e);
                        }
                        addAndChangeDevice(deviceList);
                        new Handler().postDelayed(() -> {
                            loading.setVisibility(View.GONE);
                            containerMap.setVisibility(View.VISIBLE);
                        }, 5000);

                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Device>> call, @NonNull Throwable t) {
                Log.d(TAG, "show Devices : onFailure: ");
            }
        });
    }

    private void setLastPointDevice(String code) {
        Call<LastProperty> call = apiService.getLastPropertyByCode(code);
        call.enqueue(new Callback<LastProperty>() {
            @Override
            public void onResponse(@NonNull Call<LastProperty> call, @NonNull Response<LastProperty> response) {
                if (response.isSuccessful()) {

                    if (response.body() != null) {
                        lastProperty = response.body();
                        Log.d(TAG, "`response last location" + lastProperty.toString());
//                        updateSymbolDestination(ICON_DESTINATION_DEVICE_V1_ID, destinationOrigin, symbolDefault);
                        deviceLocationUpdate.put("devices/" + code + "-origin", updateDestinationOnMap(lastProperty.getLatest_property().getMessage()));
                        deviceLocationUpdate.put("devices/" + code, updateDestinationOnMap(lastProperty.getLatest_property().getMessage()));
                        if (mapboxMap.getLocationComponent().getLastKnownLocation() != null){
                            origin = Point.fromLngLat(mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude()
                                    , mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude());
                            updateValuesFeature("devices/" + code, origin, deviceLocationUpdate.get("devices/" + code), deviceLocationUpdate.get("devices/" + code + "-origin"));
                        }
                        mapboxMap.getStyle(style -> {
                            if (deviceList.isEmpty()) {
                                Log.e(TAG, "deviceList is empty");
                            } else {
                                for (Device d : deviceList) {
                                    setUpSymbol(d.getNameDevice() + "-Origin", ICON_DESTINATION_DEVICE_V1_ID, Objects.requireNonNull(deviceLocationUpdate.get("devices/" + d.getCodeDevice())));
                                    symbolsUpdate.add(setUpSymbol(d.getNameDevice(), ICON_DESTINATION_DEVICE_V1_ID, Objects.requireNonNull(deviceLocationUpdate.get("devices/" + d.getCodeDevice()))));
                                }
                            }
                        });
                    } else {
                        Log.e(TAG, "get LastKnownLocation of device is null");
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<LastProperty> call, @NonNull Throwable t) {
                Log.e(TAG, "error get Last Known Location of device" + t);
            }
        });

    }

    private void funSleep(long timeMillis) {
        long currentTimeMillis = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - currentTimeMillis >= timeMillis) {
                break;
            }
        }
    }

    @NonNull
    private String[] getArrayTopic(@NonNull List<Device> devices) {
        String[] arrayTopic = new String[devices.size()];
        qos = new int[devices.size()];
        int i = 0;
        for (Device device : devices) {

            arrayTopic[i] = "devices/" + device.getCodeDevice();
            qos[i] = 0;
            deviceFeaturesMap.put(arrayTopic[i], getListFeatures());
            setLastPointDevice(device.getCodeDevice());
            i++;
        }
        return arrayTopic;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addAndChangeDevice(@NonNull List<Device> list) {
//            devices.add(new DeViceMenuV2(R.color.green_status, R.drawable.ba_battery, R.drawable.images, "Devices01", "None"));
        for (Device device : list) {
            Log.d(TAG, "device"+device.toString());
        }

        deviceMenuAdapter.setData(list);
        Objects.requireNonNull(listDevice.getAdapter()).notifyDataSetChanged();
        btnMenu.setOnClickListener(v -> {
            setAnimation(SLIDE_LEFT_IN, listDevice);
            clickInterceptor.setVisibility(View.VISIBLE);

        });
        clickInterceptor.setOnClickListener(v -> {
            setAnimation(SLIDE_LEFT_OUT, listDevice);
            clickInterceptor.setVisibility(View.GONE);
        });
        deviceMenuAdapter.setClickListener(deviceMenu -> {

        });
        deviceMenuAdapter.setBtnFindClick(deviceMenu -> {
            setCamera(Objects.requireNonNull(deviceLocationUpdate.get("devices/" + deviceMenu.getCodeDevice())));
//            symbolDefault = updateSymbolDestination(ICON_DESTINATION_DEVICE_V1_ID, deviceLocationUpdate.get("devices/" + deviceMenu.getCodeDevice()), symbolDefault);
        });
        deviceMenuAdapter.setBtnInfoClick(deviceMenu -> {
            deviceCheck = deviceMenu;
            Log.d(TAG, "distance: " + distance);
            Log.d(TAG, "id device: " + deviceMenu.getIdDevice());
            setAnimation(SLIDE_LEFT_OUT, listDevice);
            txtTitleDevice.setText(deviceMenu.getNameDevice());
            txtValueType.setText(deviceMenu.getTypeDevice());
            try{
                Log.d(TAG, "Bitmap"+deviceMenu.getBitmapToString());
                deviceImage.setImageBitmap(BitmapEncode.convertStringToBitmap(deviceMenu.getBitmapToString()));
            }catch ( Exception e ){
                Log.e(TAG, "error converting"+e);
                deviceImage.setImageResource(R.drawable.image_not_found_1150x647);
            }

            if (deviceFeaturesMap != null) {
                Log.d(TAG, "Device FeaturesMap");
                deviceFeaturesAdapter.setData(deviceFeaturesMap.get("devices/" + deviceMenu.getCodeDevice()));
                Objects.requireNonNull(rvFeatures.getAdapter()).notifyDataSetChanged();
            }
            new Handler().postDelayed(() -> setAnimation(SLIDE_LEFT_OUT, clickInterceptor), 200);
            new Handler().postDelayed(() -> setAnimation(SLIDE_UP, containerFeatures), 500);

        });

        btnFind.setOnClickListener(v -> new Handler().postDelayed(() -> {
            setAnimation(SLIDE_DOWN, containerFeatures);
            assert mapboxMap.getLocationComponent().getLastKnownLocation() != null;
            origin = Point.fromLngLat(mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude()
                    , mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude());
//            symbolDefault = updateSymbolDestination(ICON_DESTINATION_DEVICE_V1_ID, deviceLocationUpdate.get("devices/" + deviceCheck.getCodeDevice()), symbolDefault);
            getSingleRoute(origin, deviceLocationUpdate.get("devices/" + deviceCheck.getCodeDevice()));
        }, 1000));
//        checkDevice(list);

    }

    private void setAnimation(int i, View v) {

        switch (i) {
            case 1:
                animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down);
                break;
            case 2:
                animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up);
                break;
            case 3:
                animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_left_out_menu);
                break;
            case 4:
                animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_left_in_menu);
                break;
            default:
                animation = null;

        }
        int finalIndex;
        if (i % 2 == 0) {
            finalIndex = 2;
        } else {
            finalIndex = 1;
        }
        animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "onAnimationEnd: " + i);
                switch (finalIndex) {
                    case 1:
                        v.setVisibility(View.GONE);
                        break;
                    case 2:
                        v.setVisibility(View.VISIBLE);
                        break;
                    default:


                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Log.d(TAG, "animation: " + animation);
        if (animation != null) {
            requireActivity().runOnUiThread(() -> v.startAnimation(animation));
            animation.setAnimationListener(animationListener);
        }

    }


    @NonNull
    private List<DeviceFeatures> getListFeatures() {
        List<DeviceFeatures> list = new ArrayList<>();
        list.add(new DeviceFeatures(R.drawable.distance, "Distance", "0 Km"));
        list.add(new DeviceFeatures(R.drawable.sandglass, "Duration", "0 Hour"));
        list.add(new DeviceFeatures(R.drawable.paw, "Journey", "0 Km"));
        return list;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateValuesFeature(String topic, Point a, Point b, Point c) {
        distance = TurfMeasurement.distance(a, b, TurfConstants.UNIT_DEFAULT);
        journey = journey + TurfMeasurement.distance(b, c, TurfConstants.UNIT_DEFAULT);
        Log.e(TAG, "journey=" + journey);
        if (distance < 1) {
            distance = distance * 1000;
            Objects.requireNonNull(deviceFeaturesMap.get(topic)).get(0).setValue(new DecimalFormat("0.00").format(distance) + " m");
            Objects.requireNonNull(deviceFeaturesMap.get(topic)).get(1).setValue(getAppUsageTime(requireContext(), "com.petweio.projectdoan") / (1000 * 60 * 60) + " Hour");
            Objects.requireNonNull(deviceFeaturesMap.get(topic)).get(2).setValue(new DecimalFormat("0.00").format(journey) + " m");
        } else {
            Objects.requireNonNull(deviceFeaturesMap.get(topic)).get(0).setValue(new DecimalFormat("0.00").format(distance) + " Km");
            Objects.requireNonNull(deviceFeaturesMap.get(topic)).get(1).setValue(getAppUsageTime(requireContext(), "com.petweio.projectdoan") / (1000 * 60 * 60) + " Hour");
            Objects.requireNonNull(deviceFeaturesMap.get(topic)).get(2).setValue(new DecimalFormat("0.00").format(journey) + " Km");
        }
        if (deviceCheck != null && deviceFeaturesMap != null) {
            Log.d(TAG, "Device FeaturesMap check" + deviceCheck);
            if (topic.equals("devices/" + deviceCheck.getCodeDevice())) {
                Log.d(TAG, "Device FeaturesMap");
                deviceFeaturesAdapter.setData(deviceFeaturesMap.get("devices/" + deviceCheck.getCodeDevice()));
                Objects.requireNonNull(rvFeatures.getAdapter()).notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onMapReady(@NonNull MapboxMap map) {
        MapFragment.this.mapboxMap = map;

        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/fight242001/clmrq46bn02ag01qx122jgyc2"), style -> {
            setupGesturesListener();
            showDevice(userName);
            Log.d(TAG, "onMapReady ");
            fat.setOnClickListener(v -> {
                try{
                    checkAndEnableGPS(style);
                    assert mapboxMap.getLocationComponent().getLastKnownLocation() != null;
                    setCamera(Point.fromLngLat(mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude()
                            , mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude()));
                    Log.d(TAG, "Vi tri cua toi: " + mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude() + "," + mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude());
                    mapboxMap.addOnMoveListener(onMoveListener);
                    fat.hide();
                }catch (Exception e){
                    Toast.makeText(requireContext(),"error",Toast.LENGTH_LONG).show();
                }


            });


            style.addImage(ICON_DESTINATION_DEVICE_V1_ID, Objects.requireNonNull(createLayeredCircleBitmap(getContext()
                    , R.drawable.ic_mapbox_user_red, R.drawable.ic_mapbox_mylocation_bg
                    , R.drawable.ic_mapbox_user_shadow)));
            style.addImage(ICON_DESTINATION_Update_DEVICE_V1_ID, Objects.requireNonNull(createLayeredCircleBitmap(getContext()
                    , R.drawable.ic_mapbox_user_green, R.drawable.ic_mapbox_mylocation_bg
                    , R.drawable.ic_mapbox_user_shadow)));


            // Create a SymbolManager.
            symbolManager = new SymbolManager(mapView, mapboxMap, style);
            // Set non-data-driven properties.
            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);

            initLayers(style);
            initSource(style);

            enableLocationComponent(style);


        });

    }

    private void setupGesturesListener() {
        mapboxMap.addOnMoveListener(onMoveListener);
    }

    private void setCamera(@NonNull Point p) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(p.latitude(), p.longitude()))
                .zoom(15)
                .build();

// Set the camera position.
        mapboxMap.setCameraPosition(cameraPosition);

        // Di chuyển camera đến giữa các điểm và thu phóng để hiển thị chúng

    }

    private void setCameraTwoPoint(@NonNull Point point1, @NonNull Point point2) {
        // Tính toán tọa độ trung tâm của hai điểm
        double centerLatitude = (point1.latitude() + point2.latitude()) / 2;
        double centerLongitude = (point1.longitude() + point2.longitude()) / 2;

// Di chuyển camera đến tọa độ trung tâm
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(centerLatitude, centerLongitude)) // Đặt tọa độ trung tâm
                .zoom(10) // Đặt mức độ thu phóng
                .build();

        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));

    }

    private void getSingleRoute(Point origin, Point destination) {
        List<Point> points = new ArrayList<>();
        points.add(origin);
        points.add(destination);
        MapboxDirections client = MapboxDirections.builder()
                .accessToken(getString(R.string.token_mapbox))
                .routeOptions(
                        RouteOptions.builder()
                                .coordinatesList(points)
                                .profile(DirectionsCriteria.PROFILE_WALKING)
                                .overview(DirectionsCriteria.OVERVIEW_FULL)
                                .build())
                .build();
//        setCameraTwoPoint(origin, destination);

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @SuppressLint("StringFormatInvalid")
            @Override
            public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Log.d(TAG, "Response code: " + response.code());
                if (response.body() == null) {
                    Log.e(TAG, "No routes found, make sure you set the right user and access token.");

                } else if (response.body().routes().size() < 1) {
                    Log.e(TAG, "No routes found");

                } else {
                    walkingRoute = response.body().routes().get(0);
                    for (DirectionsRoute route : response.body().routes()) {
                        List<Point> decode = PolylineUtils.decode(Objects.requireNonNull(route.geometry()), PRECISION_6);
                        Point pointLast = decode.get(decode.size() - 1);
                        showRouteLine(decode);
                        // I need here more points
                        if (symbolsToDelete != null) {
                            symbolManager.delete(symbolsToDelete);
                        }
                        for (Point p : Objects.requireNonNull(getBetweenTwoPoints(pointLast, destination))) {

                            Log.d(TAG, "Check v 2: " + p.latitude() + ", " + p.longitude());
                            requireActivity().runOnUiThread(() -> {
                                symbolsToDelete.add(symbol);
                                symbol = setSymbol(ICON_DESTINATION_Update_DEVICE_V1_ID, p);

                            });
                        }

                    }

                }

            }


            @Override
            public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Error: " + throwable.getMessage());
                Toast.makeText(getActivity(),
                        "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initLayers(@NonNull Style loadedMapStyle) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);
        // Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                PropertyFactory.lineDasharray(new Float[]{0.01f, 2f}),
                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                PropertyFactory.lineWidth(5f),
                lineColor(Color.parseColor("#006eff"))
        );
        loadedMapStyle.addLayer(routeLayer);

        // Add the red marker icon image to the map


        // Add the red marker icon SymbolLayer to the map
        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                iconImage(ICON_DESTINATION_DEVICE_V1_ID),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[]{0f, -9f})));
    }

    private void initSource(@NonNull Style loadedMapStyle) {
        GeoJsonSource routeLineSource = new GeoJsonSource(ROUTE_SOURCE_ID);
        loadedMapStyle.addSource(routeLineSource);
    }


    private void showRouteLine(List<Point> routeCoordinates) {

        if (mapboxMap != null) {
            mapboxMap.getStyle(style -> {
                GeoJsonSource routeLineSource = style.getSourceAs(ROUTE_SOURCE_ID);
                assert routeLineSource != null;
                routeLineSource.setGeoJson(FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(
                        LineString.fromLngLats(routeCoordinates)
                )}));
                // Create a LineString with the directions route's geometry and
                // reset the GeoJSON source for the route LineLayer source
                routeLineSource.setGeoJson(LineString.fromPolyline(Objects.requireNonNull(walkingRoute.geometry()),
                        PRECISION_6));

            });
        }
    }

    @Nullable
    private List<Point> getBetweenTwoPoints(@NonNull Point A, @NonNull Point B) {
        List<Point> pointsList = new ArrayList<>();
        double m = (B.longitude() - A.longitude()) / (B.latitude() - A.latitude());
        double b = A.longitude() - m * A.latitude();

        // Kiểm tra xem hai điểm A và B có trùng nhau hay không.
        if (A.latitude() == B.latitude() && A.longitude() == B.longitude()) {
            return null;
        }

        double x = Math.min(A.latitude(), B.latitude());
        do {
            double y = m * x + b;

            // Kiểm tra xem điểm hiện tại có nằm giữa hai điểm A và B hay không.
            if (Math.min(A.latitude(), B.latitude()) < x && x < Math.max(A.latitude(), B.latitude()) &&
                    Math.min(A.longitude(), B.longitude()) < y && y < Math.max(A.longitude(), B.longitude())) {
                pointsList.add(Point.fromLngLat(y, x));
            }
            x = x + 0.00001d;
        } while (x <= Math.max(A.latitude(), B.latitude()));
        // Tạo một vòng lặp for để duyệt qua tất cả các điểm trên đường thẳng đi qua hai
        // điểm A và B.

        return pointsList;
    }

    private Symbol setSymbol(@NonNull String id, @NonNull Point p) {
        float size = 1.3f;
        // Create a symbol at the specified location.
        if (id.equals(ICON_DESTINATION_Update_DEVICE_V1_ID)) {
            size = 0.5f;
        }
        SymbolOptions symbolOptions = new SymbolOptions()
                .withLatLng(new LatLng(p.latitude(), p.longitude()))
                .withIconImage(id)
                .withIconSize(size);
        // Use the manager to draw the symbol.
        return symbolManager.create(symbolOptions);
    }

    private Symbol setUpSymbol(String nameId, @NonNull String iconId, @NonNull Point p) {
        // Tạo một SymbolOptions cho biểu tượng
        String[] fonts = new String[]{"Inter Black"};
        SymbolOptions symbolOptions = new SymbolOptions()
                .withLatLng(new LatLng(p.latitude(), p.longitude())) // Vị trí của biểu tượng
                .withIconImage(iconId) // Đặt ID của hình ảnh biểu tượng
                .withIconSize(1.3f) // Đặt kích thước của biểu tượng
                .withTextField(nameId) // Text hiển thị bên cạnh biểu tượng
                .withTextSize(12.0f) // Đặt kích thước của văn bản
                .withTextFont(fonts);
// Thêm biểu tượng vào bản đồ
        return symbolManager.create(symbolOptions);

    }
//    private Symbol updateSymbolDestination(String id, Point newDestination, Symbol symbolTest) {
//        if (destinationDefault != null) {
//            Log.d(TAG, "Symbol manager: " + symbolManager.toString());
//            Log.d(TAG, "Symbol Test: " + symbolTest.getId());
//            try {
//
//                symbolManager.delete(symbolTest);
//            } catch (Exception e) {
//                Log.e(TAG, "Symbol error: " + e);
//            }
//
//        }
//        if (newDestination != null) {
//            // Create a symbol at the specified location.
//            symbolTest = setSymbol(id, newDestination);
//        }
//        return symbolTest;
//    }

    @NonNull
    private Point updateDestinationOnMap(String destinationInfo) {
        // Xử lý và cập nhật điểm đích trên bản đồ ở đây
        destinationInfo = destinationInfo.replaceAll(" ", "").replace("[", "").replace("]", "");
        String[] destinationString = splitString(destinationInfo);
        Log.d(TAG, "Lat: " + destinationString[0] + " Long: " + destinationString[1] + " Bat: " + destinationString[2]);

        return Point.fromLngLat(Double.parseDouble(destinationString[1]), Double.parseDouble(destinationString[0]));
    }

    public String[] splitString(@NonNull String s) {
        return s.split(",");
    }

    private void checkAndEnableGPS(Style style) {
        locationComponent = mapboxMap.getLocationComponent();
        LocationComponentActivationOptions options = LocationComponentActivationOptions
                .builder(requireContext(), style)
                .useDefaultLocationEngine(true)
                .build();

        locationComponent.activateLocationComponent(options);
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationComponent.setLocationComponentEnabled(true);

        if (!locationComponent.isLocationComponentActivated()) {
            // Yêu cầu bật GPS
            locationComponent.activateLocationComponent(options);
            locationComponent.setLocationComponentEnabled(true);
        }
    }


    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(requireContext())) {

// Get an instance of the component
            locationComponent = mapboxMap.getLocationComponent();

// Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(requireContext(), loadedMapStyle).build());

// Enable to make component visible
            if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(requireActivity());
        }
    }


    public Bitmap createLayeredCircleBitmap(Context context, @DrawableRes int innerLayerRes, @DrawableRes int middleLayerRes, @DrawableRes int outerLayerRes) {
        // Lấy các Drawable từ tài nguyên.
        Drawable innerLayerDrawable = AppCompatResources.getDrawable(context, innerLayerRes);
        Drawable middleLayerDrawable = AppCompatResources.getDrawable(context, middleLayerRes);
        Drawable outerLayerDrawable = AppCompatResources.getDrawable(context, outerLayerRes);

        // Chuyển đổi các Drawable thành các Bitmap.
        Bitmap innerLayerBitmap = convertDrawableToBitmap(innerLayerDrawable);
        Bitmap middleLayerBitmap = convertDrawableToBitmap(middleLayerDrawable);
        Bitmap outerLayerBitmap = convertDrawableToBitmap(outerLayerDrawable);

        // Tạo một Bitmap mới có kích thước giống với hình ảnh nền (outer layer).
        assert outerLayerBitmap != null;
        Bitmap layeredCircleBitmap = Bitmap.createBitmap(outerLayerBitmap.getWidth(), outerLayerBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        // Vẽ lớp nền (outer layer).
        Canvas canvas = new Canvas(layeredCircleBitmap);
        canvas.drawBitmap(outerLayerBitmap, -15f, -15f, null);

        // Vẽ lớp giữa (middle layer) lên trên lớp nền.
        assert middleLayerBitmap != null;
        canvas.drawBitmap(middleLayerBitmap, 2f, 2f, null);

        // Vẽ lớp trong cùng (inner layer) lên trên lớp giữa.
        assert innerLayerBitmap != null;
        canvas.drawBitmap(innerLayerBitmap, 6f, 6f, null);

        return layeredCircleBitmap;
    }

    private Bitmap convertDrawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        // Nếu không phải BitmapDrawable, chúng ta sẽ vẽ nó lên một Canvas mới để tạo Bitmap.
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    // Hàm này trả về thời gian hoạt động của ứng dụng với gói "packageName" trong millis.
    public long getAppUsageTime(@NonNull Context context, String packageName) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTime = System.currentTimeMillis();
        long startTime = currentTime - 86400000;  // Lấy dữ liệu trong vòng 24 giờ trước đó

        List<UsageStats> appUsageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, startTime, currentTime);

        long totalUsageTime = 0;

        for (UsageStats stats : appUsageStats) {
            if (stats.getPackageName().equals(packageName)) {
                totalUsageTime += stats.getTotalTimeInForeground();
            }
        }

        return totalUsageTime;
    }

    @Override
    public void onExplanationNeeded(List<String> list) {
    }

    @Override
    public void onPermissionResult(boolean b) {
        if (b) {
            mapboxMap.getStyle(this::enableLocationComponent);
        } else {
            Toast.makeText(requireContext(), "user location permission not granted", Toast.LENGTH_LONG).show();
            requireActivity().finish();
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        Log.d(TAG,"OnResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        Log.d(TAG,"OnStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();

        Log.d(TAG,"OnStop");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"OnPause");
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cancel the Directions API request
        mapView.onDestroy();
        try {
            mqttAndroidClient.unsubscribe(topics);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        mapView.onLowMemory();
    }

}
