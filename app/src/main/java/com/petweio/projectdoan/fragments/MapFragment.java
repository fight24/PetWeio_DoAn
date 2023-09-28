package com.petweio.projectdoan.fragments;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.petweio.projectdoan.Model.DeviceFeatures;
import com.petweio.projectdoan.Model.DeviceMenu;
import com.petweio.projectdoan.R;
import com.petweio.projectdoan.service.MqttClientManager;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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
public class MapFragment extends Fragment implements PermissionsListener, OnMapReadyCallback,MqttCallback {
    private static final String TAG = "MapFragment";
    private static final String ARG_PARAM_MQTT = "MQTT";
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
    private MqttClientManager mqttClient = new MqttClientManager() ;
    private MqttAndroidClient mqttAndroidClient;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private RecyclerView rvFeatures,listDevice;
    private DeviceFeaturesAdapter deviceFeaturesAdapter;
    private DeviceMenuAdapter deviceMenuAdapter;
    PermissionsManager permissionsManager;
    LocationComponent locationComponent;
    private ConstraintLayout containerMap;
    private LinearLayout loading;
    private TextView txtTitleDevice,txtValueType;
    private Point origin;
    private Point destinationDefault = Point.fromLngLat(106.64809818797131,20.993439557407193);
    private Point destinationOrigin = Point.fromLngLat( 105.79080889159361,21.028445539124206);
    private SymbolManager symbolManager;
    private SymbolOptions symbolOptions;
    private Symbol symbol,symbolDefault;
    private MapboxDirections client;
    private DirectionsRoute walkingRoute;
    FloatingActionButton fat;
    List<Symbol> symbolsToDelete = new ArrayList<>();
    LinearLayoutManager linearLayoutHorizontalManager,linearLayoutVerticalManager;
    ImageButton imgBtnClose,btnMenu;
    AppCompatButton btnFind;
    FrameLayout containerFeatures;
    View clickInterceptor;
    Animation animation;
    Animation.AnimationListener animationListener;
    CircleImageView deviceImage;
    boolean checkMenu = false;
    final String[] topics = new String[]{"device01", "device02", "device03"};
    final int[] qos = new int[]{1, 1, 1};
    double distance = 0d;


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
    public static MapFragment newInstance(MqttClientManager client) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_MQTT, client);
        fragment.setArguments(args);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(requireActivity(), getString(R.string.key_mapbox));
        if (this.getArguments() != null) {
            mqttClient = (MqttClientManager) this.getArguments().getSerializable(ARG_PARAM_MQTT);
            assert mqttClient != null;
            mqttAndroidClient = mqttClient.getMqttClient();
            Log.d(TAG, "OK");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        initView(rootView,savedInstanceState);
        return rootView;
    }

    private void initView(View rootView,Bundle savedInstanceState) {

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

        linearLayoutHorizontalManager = new LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false);
        linearLayoutVerticalManager = new LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false);

        txtTitleDevice = rootView.findViewById(R.id.txtTitle);
        txtValueType = rootView.findViewById(R.id.txtValueType);
        deviceImage = rootView.findViewById(R.id.imageDevice);
        Log.d(TAG, "LinearLayoutManager  " + requireContext());


        fat.hide();
        containerMap = rootView.findViewById(R.id.containerMap);
        loading = rootView.findViewById(R.id.loading);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            mqttAndroidClient.subscribe(topics,qos);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

        setRecyclerView();
    }
    private void setRecyclerView(){
        // feature
        rvFeatures.setLayoutManager(linearLayoutHorizontalManager);
        deviceFeaturesAdapter.setData(getListFeatures());
        rvFeatures.setAdapter(deviceFeaturesAdapter);
        rvFeatures.setOverScrollMode(View.OVER_SCROLL_NEVER);

        imgBtnClose.setOnClickListener(v->  setAnimation(SLIDE_DOWN,containerFeatures));
        //menu
        listDevice.setLayoutManager(linearLayoutVerticalManager);
        deviceMenuAdapter = new DeviceMenuAdapter(getListDevices(), deviceMenu -> {
            Log.d(TAG,"distance: "+ distance);
            checkMenu = false;
            requireActivity().runOnUiThread(() -> {
                setAnimation(SLIDE_LEFT_OUT,listDevice);
                clickInterceptor.setVisibility(View.GONE);
                txtTitleDevice.setText(deviceMenu.getTitle());
                txtValueType.setText(deviceMenu.getType());
                deviceImage.setImageResource(deviceMenu.getResourceId());
                setAnimation(SLIDE_UP, containerFeatures);
            });
        });

        //click items
        Log.d(TAG, "text: "+ txtTitleDevice);
        listDevice.setAdapter(deviceMenuAdapter);
        listDevice.setOverScrollMode(View.OVER_SCROLL_NEVER);

        clickInterceptor.setOnClickListener(v -> {
            checkMenu = false;
            setAnimation(SLIDE_LEFT_OUT,listDevice);
            clickInterceptor.setVisibility(View.GONE);
        });
        btnMenu.setOnClickListener(v->   {
            if(!checkMenu){
                checkMenu = true;
                setAnimation(SLIDE_LEFT_IN,listDevice);
                clickInterceptor.setVisibility(View.VISIBLE);
            }

        });
    }
    private void setAnimation(int i,View v){

        switch (i){
            case 1:
                animation= AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down);
                break;
            case 2:
                animation= AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up);
                break;
            case 3:
                animation= AnimationUtils.loadAnimation(requireContext(), R.anim.slide_left_out_menu);
                break;
            case 4:
                animation= AnimationUtils.loadAnimation(requireContext(), R.anim.slide_left_in_menu);
                break;
            default:
                animation = null;

        }
        int finalIndex;
        if(i%2 == 0){
            finalIndex = 2;
        }else{
            finalIndex = 1;
        }
        animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "onAnimationEnd: "+i);
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
        Log.d(TAG,"animation: "+ animation);
        if(animation != null) {
            requireActivity().runOnUiThread(()-> v.startAnimation(animation));
            animation.setAnimationListener(animationListener);
        }

    }

    @NonNull
    private List<DeviceMenu> getListDevices(){
        List<DeviceMenu> list = new ArrayList<>();
        list.add(new DeviceMenu(R.drawable.images,"Device 01", "DOG"));
        list.add(new DeviceMenu(R.drawable.image2,"Device 02","CAT"));
        list.add(new DeviceMenu(R.drawable.image3,"Device 03","N/A"));
        return list;
    }
    @NonNull
    private List<DeviceFeatures> getListFeatures(){
        List<DeviceFeatures> list = new ArrayList<>();
        list.add(new DeviceFeatures(R.drawable.distance,"Distance","0 Km"));
        list.add(new DeviceFeatures(R.drawable.sandglass,"Duration","0 Hour"));
        list.add(new DeviceFeatures(R.drawable.paw,"Journey","0 Km"));
            return list;
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        Log.d(TAG,"Topic: "+ topic);
        if(topic.equals(topics[0])){
            String msg = new String(message.getPayload());
            Log.d(TAG, msg);
            Log.d(TAG, "destination : "+destinationDefault);
            assert mapboxMap.getLocationComponent().getLastKnownLocation() != null;
            origin = Point.fromLngLat(mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude()
                    ,mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude());
            Log.d(TAG, "origin: "+origin);
            destinationDefault = updateDestinationOnMap(msg);
            symbolDefault = updateSymbolDestination(ICON_DESTINATION_DEVICE_V1_ID,destinationDefault,symbolDefault);

            Log.d(TAG,"destinationUpdate: "+destinationDefault);
            distance = TurfMeasurement.distance(origin, destinationDefault, TurfConstants.UNIT_DEFAULT);
            Log.d(TAG,"distance: "+ distance);
            symbol = updateSymbolDestination(ICON_DESTINATION_Update_DEVICE_V1_ID,destinationDefault,symbol);
            deviceFeaturesAdapter.updateTextForItem(0,new DecimalFormat("0.00").format(distance) + " Km");
            deviceFeaturesAdapter.updateTextForItem(1,1 + " Hour");
            btnFind.setOnClickListener(v-> new Thread(()->{
                setAnimation(SLIDE_DOWN,containerFeatures);
                getSingleRoute(origin,destinationDefault);
            }).start());


        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    @Override
    public void onMapReady(@NonNull MapboxMap map) {
        mapboxMap = map;
        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/fight242001/clmrk7ric029t01qx75rc7soa"), style -> {
            setupGesturesListener();
            fat.setOnClickListener(v->{
                assert mapboxMap.getLocationComponent().getLastKnownLocation() != null;
                setCamera(Point.fromLngLat(mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude()
                        ,mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude()));
                mapboxMap.addOnMoveListener(onMoveListener);
                fat.hide();

            });


            style.addImage(ICON_DESTINATION_DEVICE_V1_ID, Objects.requireNonNull(createLayeredCircleBitmap(getContext()
                    ,R.drawable.ic_mapbox_user_red,R.drawable.ic_mapbox_mylocation_bg
                    ,R.drawable.ic_mapbox_user_shadow)));
            style.addImage(ICON_DESTINATION_Update_DEVICE_V1_ID, Objects.requireNonNull(createLayeredCircleBitmap(getContext()
                    ,R.drawable.ic_mapbox_user_green,R.drawable.ic_mapbox_mylocation_bg
                    ,R.drawable.ic_mapbox_user_shadow)));



            // Create a SymbolManager.
            symbolManager = new SymbolManager(mapView, mapboxMap, style);
            // Set non-data-driven properties.
            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);
            symbolDefault = setSymbol(ICON_DESTINATION_DEVICE_V1_ID,destinationDefault);
            symbol = setSymbol(ICON_DESTINATION_Update_DEVICE_V1_ID,destinationDefault);


            initLayers(style);
            initSource(style);


            enableLocationComponent(style);
            mqttAndroidClient.setCallback(this);

            Log.e(TAG, symbolDefault.toString());

        });
        new Handler().postDelayed(() -> {
            loading.setVisibility(View.INVISIBLE);
            containerMap.setVisibility(View.VISIBLE);
        }, 5000);
    }
    private void setupGesturesListener() {
        mapboxMap.addOnMoveListener(onMoveListener);
    }
    private void setCamera(Point p){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(p.latitude(), p.longitude()))
                .zoom(15)
                .build();

// Set the camera position.
        mapboxMap.setCameraPosition(cameraPosition);

        // Di chuyển camera đến giữa các điểm và thu phóng để hiển thị chúng

    }

    private void getSingleRoute(Point origin,Point destination) {
        List<Point> points = new ArrayList<>();
        points.add(origin);
        points.add(destination);
        client = MapboxDirections.builder()
                .accessToken(getString(R.string.token_mapbox))
                .routeOptions(
                        RouteOptions.builder()
                                .coordinatesList(points)
                                .profile(DirectionsCriteria.PROFILE_WALKING)
                                .overview(DirectionsCriteria.OVERVIEW_FULL)
                                .build())
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @SuppressLint("StringFormatInvalid")
            @Override
            public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Log.d(TAG, "Response code: " + response.code());
                if (response.body() == null) {
                    Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Log.e(TAG, "No routes found");
                    return;
                }else {
                    walkingRoute = response.body().routes().get(0);
                    for (DirectionsRoute route : response.body().routes()) {
                        List<Point> decode = PolylineUtils.decode(Objects.requireNonNull(route.geometry()), PRECISION_6);
                        Point pointLast =decode.get(decode.size() - 1) ;
                        showRouteLine(decode);
                        // I need here more points
                        if(symbolsToDelete != null){
                            symbolManager.delete(symbolsToDelete);
                        }
                        for (Point p : Objects.requireNonNull(getBetweenTwoPoints(pointLast, destination))){

                            Log.d(TAG,"Check v 2: "+p.latitude() + ", " + p.longitude());
                            requireActivity().runOnUiThread(()-> {
                                symbolsToDelete.add(symbol);
                                symbol = setSymbol(ICON_DESTINATION_Update_DEVICE_V1_ID,p);

                            });
                        }

                    }

                }

                }


            @Override
            public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable throwable) {
                Log.e(TAG,"Error: " + throwable.getMessage());
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
                PropertyFactory.lineDasharray(new Float[] {0.01f, 2f}),
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
                routeLineSource.setGeoJson(FeatureCollection.fromFeatures(new Feature[] {Feature.fromGeometry(
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
    private List<Point> getBetweenTwoPoints(Point A, Point B){
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
                pointsList.add(Point.fromLngLat(y,x));
            }
            x = x + 0.00001d;
        } while (x <= Math.max(A.latitude(), B.latitude()));
        // Tạo một vòng lặp for để duyệt qua tất cả các điểm trên đường thẳng đi qua hai
        // điểm A và B.

        return pointsList;
    }
    private Symbol setSymbol(String id, @NonNull Point p){
        float size = 1.3f;
        // Create a symbol at the specified location.
        if(id.equals(ICON_DESTINATION_Update_DEVICE_V1_ID)){
            size = 0.5f;
        }
        symbolOptions = new SymbolOptions()
                .withLatLng(new LatLng(p.latitude(), p.longitude()))
                .withIconImage(id)
                .withIconSize(size);
        // Use the manager to draw the symbol.
        return symbolManager.create(symbolOptions);
    }
    private Symbol updateSymbolDestination(String id,Point newDestination,Symbol symbolTest){
        if(destinationDefault != null){
            Log.d(TAG,"Symbol manager: "+symbolManager.toString());
            Log.d(TAG,"Symbol Test: "+ symbolTest.getId());
            try{
                symbolManager.delete(symbolTest);
            }catch (Exception e){
                Log.e(TAG,"Symbol error: "+ e);
            }

        }
        if(newDestination != null){
            // Create a symbol at the specified location.
          symbolTest =  setSymbol(id, newDestination);
        }
        return symbolTest;
    }

    @NonNull
    private Point updateDestinationOnMap(String destinationInfo) {
        // Xử lý và cập nhật điểm đích trên bản đồ ở đây
        destinationInfo = destinationInfo.replaceAll(" ","");
        String[] destinationString = splitString(destinationInfo);
        Log.d(TAG, "Lat: "+destinationString[0] +" Long: "+ destinationString[1]);

        return Point.fromLngLat(Double.parseDouble(destinationString[1]),Double.parseDouble(destinationString[0]));
    }

    public String[] splitString(@NonNull String s){
        String[] parts = s.split(",") ;
        return parts;
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(Style style) {
        Log.d(TAG, "Location permission: "+ getActivity());
//         Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {

            // Get an instance of the component
            locationComponent = mapboxMap.getLocationComponent();

            // Activate with a built LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(requireActivity().getApplicationContext(), style).build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);






        } else {

            permissionsManager = new PermissionsManager(this);

            permissionsManager.requestLocationPermissions(getActivity());

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


    @Override
    public void onExplanationNeeded(List<String> list) {

    }

    @Override
    public void onPermissionResult(boolean b) {
        if(b){
            enableLocationComponent(mapboxMap.getStyle());
        }else {
            Log.e(TAG,"onPermissionResult: denied");
            requireActivity().finish();
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();

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
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        mapView.onLowMemory();
    }

}
