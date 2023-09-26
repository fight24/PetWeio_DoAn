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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
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
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
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
import com.petweio.projectdoan.R;
import com.petweio.projectdoan.service.MqttClientManager;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private static final String ARG_PARAM_MQTT = "MQTT";
    private static final String ICON_DESTINATION_DEVICE_V1_ID = "destinationDefault-deviceV1-icon-Id";
    private static final String ICON_DESTINATION_Update_DEVICE_V1_ID = "destinationUpdate-deviceV1-icon-Id";
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private MqttClientManager mqttClient;
    private MqttAndroidClient mqttAndroidClient;
    private MapView mapView;
    private MapboxMap mapboxMap;
    PermissionsManager permissionsManager;
    LocationComponent locationComponent;
    private ConstraintLayout containerMap;
    private LinearLayout loading;

    private Point origin;
    private Point destinationDefault = Point.fromLngLat(106.64809818797131,20.993439557407193);
    private SymbolManager symbolManager;
    private SymbolOptions symbolOptions;
    private Symbol symbol,symbolDefault;
    private MapboxDirections client;
    private DirectionsRoute walkingRoute;
    FloatingActionButton fat;
    List<Symbol> symbolsToDelete = new ArrayList<>();

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

        if (getArguments() != null) {
            mqttClient = (MqttClientManager) getArguments().getSerializable(ARG_PARAM_MQTT);
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
        fat.hide();
        containerMap = rootView.findViewById(R.id.containerMap);
        loading = rootView.findViewById(R.id.loading);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull MapboxMap map) {
        mapboxMap = map;

        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/fight242001/clmrk7ric029t01qx75rc7soa"), style -> {
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
            mqttAndroidClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.e(TAG,"Connection lost: "+ cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {

                    String msg = new String(message.getPayload());
                    Log.d(TAG, msg);


                    Log.d(TAG, "destination : "+destinationDefault);
                    origin = Point.fromLngLat(mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude()
                            ,mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude());
                    Log.d(TAG, "origin: "+origin);
                    destinationDefault = updateDestinationOnMap(msg);
                    symbolDefault = updateSymbolDestination(ICON_DESTINATION_DEVICE_V1_ID,destinationDefault,symbolDefault);
                    symbol = updateSymbolDestination(ICON_DESTINATION_Update_DEVICE_V1_ID,destinationDefault,symbol);
                    Log.d(TAG,"destinationUpdate: "+destinationDefault);
                   new Thread(()->getSingleRoute(origin,destinationDefault)).start();

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
            enableLocationComponent(style);


            Log.e(TAG, symbolDefault.toString());

        });
        new Handler().postDelayed(() -> {
            loading.setVisibility(View.INVISIBLE);
            containerMap.setVisibility(View.VISIBLE);
        }, 3000);
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
                        List<Point> decode = PolylineUtils.decode(route.geometry(), PRECISION_6);
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
                    LatLngBounds bounds = new LatLngBounds.Builder()
                            .include(new LatLng(origin.latitude(), origin.longitude()))
                            .include(new LatLng(destination.latitude(), destination.longitude()))
                            .build();

                    // Di chuyển camera đến giữa các điểm và thu phóng để hiển thị chúng
                    mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
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
            symbolManager.delete(symbolTest);
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
        String[] destinationString = splitString(destinationInfo);
        Log.d(TAG, "Lat: "+destinationString[0] +" Long: "+ destinationString[1]);

        return Point.fromLngLat(Double.valueOf(destinationString[1]),Double.valueOf(destinationString[0]));
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
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(getActivity().getApplicationContext(), style).build());

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
        Bitmap layeredCircleBitmap = Bitmap.createBitmap(outerLayerBitmap.getWidth(), outerLayerBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        // Vẽ lớp nền (outer layer).
        Canvas canvas = new Canvas(layeredCircleBitmap);
        canvas.drawBitmap(outerLayerBitmap, -15f, -15f, null);

        // Vẽ lớp giữa (middle layer) lên trên lớp nền.
        canvas.drawBitmap(middleLayerBitmap, 2f, 2f, null);

        // Vẽ lớp trong cùng (inner layer) lên trên lớp giữa.
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
