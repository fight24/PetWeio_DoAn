//package com.petweio.projectdoan.fragments;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.IntentSender;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Bundle;
//import android.os.Looper;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//
//import com.google.android.gms.common.api.ApiException;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.LocationSettingsRequest;
//import com.google.android.gms.location.LocationSettingsResponse;
//import com.google.android.gms.location.LocationSettingsStatusCodes;
//import com.google.android.gms.location.Priority;
//import com.google.android.gms.maps.CameraUpdate;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.Polyline;
//import com.google.android.gms.maps.model.PolylineOptions;
//import com.google.android.gms.tasks.Task;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.petweio.projectdoan.R;
//import com.petweio.projectdoan.route.AbstractRouting;
//import com.petweio.projectdoan.route.Route;
//import com.petweio.projectdoan.route.RouteException;
//import com.petweio.projectdoan.route.Routing;
//import com.petweio.projectdoan.route.RoutingListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class PetFragment extends Fragment implements OnMapReadyCallback, RoutingListener {
//    private static final String TAG = "PetFragment";
//
//    private static final int LOCATION_INTERVAL = 10000;
//
//    private static final int LOCATION_FASTEST_INTERVAL = 5000;
//    private Context mContext;
//    private GoogleMap mMap;
//    private FusedLocationProviderClient fusedLocationClient;
//    LatLng myLastLocation,start,end;
//    List<Polyline> polyLines;
//
//    public PetFragment() {
//
//    }
//    /*
//    * TODO: Ham override
//    * */
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        this.mContext = context;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View rootView = inflater.inflate(R.layout.fragment_pet, container, false);
//        init(rootView);
//        return rootView;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        assert mapFragment != null;
//        mapFragment.getMapAsync(this);
//    }
//
//    @Override
//    public void onMapReady(@NonNull GoogleMap googleMap) {
//        Log.d(TAG, "onMapReady ");
//        mMap = googleMap;
//        userLocationChanged(mContext,false);
//
//    }
//
//    @Override
//    public void onRoutingFailure(RouteException e) {
//        Log.e(TAG, "onRoutingFailure:  "+ e.getMessage());
//    }
//
//    @Override
//    public void onRoutingStart() {
//        Log.d(TAG, "onRoutingStart: Finding Route...");
//    }
//
//    @Override
//    public void onRoutingSuccess(List<Route> route, int shortestRouteIndex) {
//        if(polyLines!=null) {
//            polyLines.clear();
//        }
//        PolylineOptions polyOptions = new PolylineOptions();
//        LatLng polylineStartLatLng = null,polylineEndLatLng = null;
//
//
//        polyLines = new ArrayList<>();
//        //add route(s) to the map using polyline
//        for (int i = 0; i <route.size(); i++) {
//
//            if(i==shortestRouteIndex)
//            {
//                polyOptions.color(ContextCompat.getColor(requireActivity(),R.color.teal_200));
//                polyOptions.width(7);
//                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
//                Polyline polyline = mMap.addPolyline(polyOptions);
//                polylineStartLatLng=polyline.getPoints().get(0);
//                int k=polyline.getPoints().size();
//                polylineEndLatLng=polyline.getPoints().get(k-1);
//                polyLines.add(polyline);
//
//            }
//            else {
//                Log.d(TAG, "i != shortestRouteIndex");
//            }
//
//        }
//
//        //Add Marker on route starting position
//        MarkerOptions startMarker = new MarkerOptions();
//        assert polylineStartLatLng != null;
//        startMarker.position(polylineStartLatLng);
//        startMarker.title("My Location");
//        mMap.addMarker(startMarker);
//
//        //Add Marker on route ending position
//        MarkerOptions endMarker = new MarkerOptions();
//        endMarker.position(polylineEndLatLng);
//        endMarker.title("Destination");
//        mMap.addMarker(endMarker);
//    }
//
//    @Override
//    public void onRoutingCancelled() {
//        findRoutes(start,end);
//    }
//
//    /*
//    *TODO: Ham tu dinh nghia
//    */
//    public void findRoutes(LatLng Start, LatLng End)
//    {
//
//        if(Start==null || End==null) {
//            Log.d(TAG,"FindRoutes: Unable to get location");
//        }
//        else
//        {
//
//            Routing routing = new Routing.Builder()
//                    .travelMode(AbstractRouting.TravelMode.WALKING)
//                    .withListener(this)
//                    .alternativeRoutes(true)
//                    .waypoints(Start, End)
//                    .key(getString(R.string.google_api_key_2))  //also define your api key here.
//                    .build();
//            routing.execute();
//
//        }
//    }
//    private void userLocationChanged(Context mContext,boolean checkUpdate) {
//
//        LocationCallback locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(@NonNull LocationResult locationResult) {
//                Location location = locationResult.getLastLocation();
//                if (location != null) {
//                    LatLng ltLng = new LatLng(location.getLatitude(), location.getLongitude());
//                     myLastLocation = ltLng;
//                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ltLng, 18f);
//                    mMap.animateCamera(cameraUpdate);
//                }
//            }
//        };
//
//        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_INTERVAL)
//                .setWaitForAccurateLocation(false)
//                .setMinUpdateIntervalMillis(LOCATION_FASTEST_INTERVAL)
//                .build();
//        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        mMap.setOnMapClickListener(latLng ->  {
//
//                end=latLng;
//                mMap.clear();
//                start=myLastLocation;
//                //start route finding
//                findRoutes(start,end);
//        });
//
//
//        if(checkUpdate){
//            mMap.getUiSettings().setMyLocationButtonEnabled(false);
//            mMap.setMyLocationEnabled(true);
//            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//        }
//       else{
//            mMap.getUiSettings().setMyLocationButtonEnabled(false);
//            mMap.setMyLocationEnabled(false);
//            fusedLocationClient.removeLocationUpdates(locationCallback);
//
//        }
//
//    }
//
//
//    private void init(View rootView) {
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
//        FloatingActionButton floatingActionButton = rootView.findViewById(R.id.btnUserLocation);
//        floatingActionButton.setOnClickListener(v -> {
//            Log.d(TAG, "On clicked");
//            checkAndRequestGps(mContext);
//            userLocationChanged(mContext,true);
//        });
//    }
//
//    private void checkAndRequestGps(Context mContext) {
//        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,10000)
//                .setWaitForAccurateLocation(false)
//                .setMinUpdateIntervalMillis(2000)
//                .build();
//
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest);
//
//        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(mContext)
//                .checkLocationSettings(builder.build());
//
//        task.addOnCompleteListener(task1 -> {
//            try {
//                task1.getResult(ApiException.class);
//                // GPS is enabled, proceed with map functionality.
//            } catch (ApiException exception) {
//                switch (exception.getStatusCode()) {
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        try {
//                            // Prompt the user to enable GPS by displaying a dialog.
//                            exception
//                                    .getStatus()
//                                    .startResolutionForResult(
//                                            requireActivity(),
//                                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED
//                                    );
//                        } catch (IntentSender.SendIntentException e) {
//                            // Ignore the error.
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        // GPS settings are not available on the device, handle accordingly.
//                        break;
//                }
//            }
//        });
//    }
//
//}