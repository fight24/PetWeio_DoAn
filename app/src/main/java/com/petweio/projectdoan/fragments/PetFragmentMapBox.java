//package com.petweio.projectdoan.fragments;
//
//
//import static com.mapbox.core.constants.Constants.PRECISION_6;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
//
//import android.annotation.SuppressLint;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//
//import com.mapbox.api.directions.v5.DirectionsCriteria;
//import com.mapbox.api.directions.v5.MapboxDirections;
//import com.mapbox.api.directions.v5.models.DirectionsResponse;
//import com.mapbox.api.directions.v5.models.DirectionsRoute;
//import com.mapbox.api.directions.v5.models.RouteOptions;
//import com.mapbox.geojson.Feature;
//import com.mapbox.geojson.LineString;
//import com.mapbox.geojson.Point;
//import com.mapbox.mapboxsdk.Mapbox;
//import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
//import com.mapbox.mapboxsdk.geometry.LatLng;
//import com.mapbox.mapboxsdk.geometry.LatLngBounds;
//import com.mapbox.mapboxsdk.maps.MapView;
//import com.mapbox.mapboxsdk.maps.MapboxMap;
//import com.mapbox.mapboxsdk.maps.Style;
//import com.mapbox.mapboxsdk.style.layers.LineLayer;
//import com.mapbox.mapboxsdk.style.layers.Property;
//import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
//import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
//import com.mapbox.mapboxsdk.utils.BitmapUtils;
//import com.petweio.projectdoan.R;
//import com.petweio.projectdoan.service.MqttClientManager;
//
//import org.eclipse.paho.android.service.MqttAndroidClient;
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.MqttCallback;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.concurrent.TimeUnit;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link PetFragmentMapBox#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class PetFragmentMapBox extends Fragment implements MapboxMap.OnMapClickListener{
//
//
//    // set value map box
//
//    private static final String TAG = "PetFragmentMapBox";
//    private static final String ARG_PARAM_MQTT = "MQTT";
//    private static final String ROUTE_LAYER_ID = "route-layer-id";
//    private static final String ROUTE_SOURCE_ID = "route-source-id";
//    private static final String ICON_LAYER_ID = "icon-layer-id";
//    private static final String ICON_SOURCE_ID = "icon-source-id";
//    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
//    private MapView mapView;
//    private MapboxMap mapboxMap;
//    private DirectionsRoute drivingRoute;
//    private DirectionsRoute walkingRoute;
//    private DirectionsRoute cyclingRoute;
//    private MapboxDirections client;
//    private final Point origin = Point.fromLngLat(106.64566276338101, 20.995052291698524);
//    private Point destination = Point.fromLngLat( 106.64809818797131 ,20.993439557407193 );
//    private String lastSelectedDirectionsProfile = DirectionsCriteria.PROFILE_DRIVING;
//    private Button drivingButton;
//    private Button walkingButton;
//    private Button cyclingButton;
//    private boolean firstRouteDrawn = false;
//    private final String[] profiles = new String[]{
//            DirectionsCriteria.PROFILE_DRIVING,
//            DirectionsCriteria.PROFILE_CYCLING,
//            DirectionsCriteria.PROFILE_WALKING
//    };
//
//
//    private MqttClientManager mqttClient;
//    private MqttAndroidClient mqttAndroidClient;
//    private int index = 0;
//    public PetFragmentMapBox() {
//        // Required empty public constructor
//    }
//
//
//    // TODO: Rename and change types and number of parameters
//    public static PetFragmentMapBox newInstance(MqttClientManager client) {
//        PetFragmentMapBox fragment = new PetFragmentMapBox();
//        Bundle args = new Bundle();
//        args.putSerializable(ARG_PARAM_MQTT,client);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        Mapbox.getInstance(requireActivity(), getString(R.string.token_mapbox));
//        if (getArguments() != null) {
//            if (getArguments() != null) {
//                mqttClient =(MqttClientManager) getArguments().getSerializable(ARG_PARAM_MQTT);
//                mqttAndroidClient = mqttClient.getMqttClient();
//                Log.d(TAG, "OK");
//
//            }
//        }
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_pet_map_box, container, false);
//        // Inflate the layout for this fragment
//        init(rootView,savedInstanceState);
//        return rootView;
//    }
//
//    private void init(View rootView,Bundle savedInstanceState) {
//
//        drivingButton = rootView.findViewById(R.id.driving_profile_button);
//        drivingButton.setTextColor(Color.WHITE);
//        walkingButton = rootView.findViewById(R.id.walking_profile_button);
//        cyclingButton = rootView.findViewById(R.id.cycling_profile_button);
//        ConstraintLayout containerMap = rootView.findViewById(R.id.containerMap);
//        LinearLayout loading = rootView.findViewById(R.id.loading);
//        // Setup the MapView
//        mapView = rootView.findViewById(R.id.mapView);
//
//
//        mapView.onCreate(savedInstanceState);
//        mqttAndroidClient.setCallback(new MqttCallback() {
//            @Override
//            public void connectionLost(Throwable cause) {
//
//            }
//
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//
//                String messages = new String(message.getPayload());
//                String[] test = splitString(messages);
//                Log.d(TAG, test[0] + " " + test[1] + index);
//                index++;
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//
//            }
//        });
//        mapView.getMapAsync(mapboxMap -> {
//
//            mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/fight242001/clmrk7ric029t01qx75rc7soa"), style -> {
//
//                PetFragmentMapBox.this.mapboxMap = mapboxMap;
//
//                initSource(style);
//
//                initLayers(style);
//
//                getAllRoutes(false);
//
//                initButtonClickListeners();
//
//                mapboxMap.addOnMapClickListener(this);
//                LatLngBounds bounds = new LatLngBounds.Builder()
//                        .include(new LatLng(origin.latitude(), origin.longitude()))
//                        .include(new LatLng(destination.latitude(), destination.longitude()))
//                        .build();
//
//                // Di chuyển camera đến giữa các điểm và thu phóng để hiển thị chúng
//                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
//                Toast.makeText(requireActivity(),
//                        "Let 's Go", Toast.LENGTH_SHORT).show();
//            });
//            new Handler().postDelayed(() ->{
//                loading.setVisibility(View.INVISIBLE);
//                containerMap.setVisibility(View.VISIBLE);
//            },3000);
//
//        });
//
//    }
//    private void getAllRoutes(boolean fromMapClick) {
//        for (String profile : profiles) {
//            getSingleRoute(profile, fromMapClick);
//        }
//    }
//
//    @Override
//    public boolean onMapClick(@NonNull LatLng point) {
//
//        destination = Point.fromLngLat(point.getLongitude(), point.getLatitude());
//        moveDestinationMarkerToNewLocation(point);
//        getAllRoutes(true);
//        return true;
//    }
//    public String[] splitString(String s){
//        String[] parts = s.split(",") ;
//        return parts;
//    }
//    /**
//     * Move the destination marker to wherever the map was tapped on.
//     *
//     * @param pointToMoveMarkerTo where the map was tapped on
//     */
//    private void moveDestinationMarkerToNewLocation(LatLng pointToMoveMarkerTo) {
//        mapboxMap.getStyle(style -> {
//            GeoJsonSource destinationIconGeoJsonSource = style.getSourceAs(ICON_SOURCE_ID);
//            if (destinationIconGeoJsonSource != null) {
//                destinationIconGeoJsonSource.setGeoJson(Feature.fromGeometry(Point.fromLngLat(
//                        pointToMoveMarkerTo.getLongitude(), pointToMoveMarkerTo.getLatitude())));
//            }
//        });
//    }
//
//    /**
//     * Add the source for the Directions API route line LineLayer.
//     */
//    private void initSource(@NonNull Style loadedMapStyle) {
//        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));
//        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID,
//                Feature.fromGeometry(Point.fromLngLat(destination.longitude(),
//                        destination.latitude())));
//        loadedMapStyle.addSource(iconGeoJsonSource);
//    }
//
//    /**
//     * Set up the click listeners on the buttons for each Directions API profile.
//     */
//    private void initButtonClickListeners() {
//        drivingButton.setOnClickListener(view -> {
//            drivingButton.setTextColor(Color.WHITE);
//            walkingButton.setTextColor(Color.BLACK);
//            cyclingButton.setTextColor(Color.BLACK);
//            lastSelectedDirectionsProfile = DirectionsCriteria.PROFILE_DRIVING;
//            showRouteLine();
//        });
//        walkingButton.setOnClickListener(view -> {
//            drivingButton.setTextColor(Color.BLACK);
//            walkingButton.setTextColor(Color.WHITE);
//            cyclingButton.setTextColor(Color.BLACK);
//            lastSelectedDirectionsProfile = DirectionsCriteria.PROFILE_WALKING;
//            showRouteLine();
//        });
//        cyclingButton.setOnClickListener(view -> {
//            drivingButton.setTextColor(Color.BLACK);
//            walkingButton.setTextColor(Color.BLACK);
//            cyclingButton.setTextColor(Color.WHITE);
//            lastSelectedDirectionsProfile = DirectionsCriteria.PROFILE_CYCLING;
//            showRouteLine();
//        });
//    }
//
//    /**
//     * Display the Directions API route line depending on which profile was last
//     * selected.
//     */
//    private void showRouteLine() {
//        if (mapboxMap != null) {
//            mapboxMap.getStyle(style -> {
//
//                // Retrieve and update the source designated for showing the directions route
//                GeoJsonSource routeLineSource = style.getSourceAs(ROUTE_SOURCE_ID);
//
//                // Create a LineString with the directions route's geometry and
//                // reset the GeoJSON source for the route LineLayer source
//                if (routeLineSource != null) {
//                    switch (lastSelectedDirectionsProfile) {
//                        case DirectionsCriteria.PROFILE_DRIVING:
//                            routeLineSource.setGeoJson(LineString.fromPolyline(Objects.requireNonNull(drivingRoute.geometry()),
//                                    PRECISION_6));
//                            break;
//                        case DirectionsCriteria.PROFILE_WALKING:
//                            routeLineSource.setGeoJson(LineString.fromPolyline(Objects.requireNonNull(walkingRoute.geometry()),
//                                    PRECISION_6));
//                            break;
//                        case DirectionsCriteria.PROFILE_CYCLING:
//                            routeLineSource.setGeoJson(LineString.fromPolyline(Objects.requireNonNull(cyclingRoute.geometry()),
//                                    PRECISION_6));
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            });
//        }
//    }
//
//    /**
//     * Add the route and icon layers to the map
//     */
//    private void initLayers(@NonNull Style loadedMapStyle) {
//        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);
//
//        // Add the LineLayer to the map. This layer will display the directions route.
//        routeLayer.setProperties(
//                lineCap(Property.LINE_CAP_ROUND),
//                lineJoin(Property.LINE_JOIN_ROUND),
//                lineWidth(5f),
//                lineColor(Color.parseColor("#006eff"))
//        );
//        loadedMapStyle.addLayer(routeLayer);
//
//        // Add the red marker icon image to the map
//        loadedMapStyle.addImage(RED_PIN_ICON_ID, Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
//                ContextCompat.getDrawable(requireActivity(), R.drawable.baseline_location_on_24))));
//
//        // Add the red marker icon SymbolLayer to the map
//        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
//                iconImage(RED_PIN_ICON_ID),
//                iconIgnorePlacement(true),
//                iconAllowOverlap(true),
//                iconOffset(new Float[]{0f, -9f})));
//    }
//
//    /**
//     * Make a request to the Mapbox Directions API. Once successful, pass the route to the
//     * route layer.
//     *
//     * @param profile the directions profile to use in the Directions API request
//     */
//    private void getSingleRoute(String profile, boolean fromMapClick) {
//        List<Point> points = new ArrayList<>();
//        points.add(origin);
//        points.add(destination);
//        client = MapboxDirections.builder()
//                .accessToken(getString(R.string.token_mapbox))
//                .routeOptions(
//                        RouteOptions.builder()
//                                .coordinatesList(points)
//                                .profile(profile)
//                                .overview(DirectionsCriteria.OVERVIEW_FULL)
//                                .build())
//                .build();
//
//        client.enqueueCall(new Callback<DirectionsResponse>() {
//            @SuppressLint("StringFormatInvalid")
//            @Override
//            public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull Response<DirectionsResponse> response) {
//                // You can get the generic HTTP info about the response
//                Log.d(TAG,"Response code: " + response.code());
//                if (response.body() == null) {
//                    Log.e(TAG,"No routes found, make sure you set the right user and access token.");
//                    return;
//                } else if (response.body().routes().size() < 1) {
//                    Log.e(TAG,"No routes found");
//                    return;
//                }
//
//                switch (profile) {
//                    case DirectionsCriteria.PROFILE_DRIVING:
//                        drivingRoute = response.body().routes().get(0);
//                        drivingButton.setText(String.format(getString(R.string.driving_profile),
//                                TimeUnit.SECONDS.toMinutes(drivingRoute.duration().longValue())));
//                        if (!firstRouteDrawn) {
//                            showRouteLine();
//                            firstRouteDrawn = true;
//                        }
//                        break;
//                    case DirectionsCriteria.PROFILE_WALKING:
//                        walkingRoute = response.body().routes().get(0);
//                        walkingButton.setText(String.format(getString(R.string.walking_profile),
//                                TimeUnit.SECONDS
//                                        .toMinutes(walkingRoute.duration().longValue())));
//                        break;
//                    case DirectionsCriteria.PROFILE_CYCLING:
//                        cyclingRoute = response.body().routes().get(0);
//                        cyclingButton.setText(String.format(getString(R.string.cycling_profile),
//                                TimeUnit.SECONDS
//                                        .toMinutes(cyclingRoute.duration().longValue())));
//                        break;
//                    default:
//                        break;
//                }
//                if (fromMapClick) {
//                    showRouteLine();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable throwable) {
//                Log.e(TAG,"Error: " + throwable.getMessage());
//                Toast.makeText(getActivity(),
//                        "Error: " + throwable.getMessage(),
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        mapView.onStart();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        mapView.onStop();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//
//        mapView.onPause();
//    }
//
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        mapView.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        // Cancel the Directions API request
//        if (client != null) {
//            client.cancelCall();
//        }
//        if (mapboxMap != null) {
//            mapboxMap.removeOnMapClickListener(this);
//        }
//        mapView.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//
//        mapView.onLowMemory();
//    }
//
//}
