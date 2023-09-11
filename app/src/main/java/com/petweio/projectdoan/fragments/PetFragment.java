package com.petweio.projectdoan.fragments;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.petweio.projectdoan.R;

public class PetFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "PetFragment";

    private static final int LOCATION_INTERVAL = 10000;

    private static final int LOCATION_FASTEST_INTERVAL = 5000;

    Context mContext;
    GoogleMap mMap;
    FusedLocationProviderClient fusedLocationClient;


    public PetFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_pet, container, false);
        init(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d(TAG, "onMapReady ");
        mMap = googleMap;


    }

    private void userLocationChanged(Context mContext) {

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    LatLng ltlng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ltlng, 18f);
                    mMap.animateCamera(cameraUpdate);
                }
            }
        };

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_INTERVAL)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(LOCATION_FASTEST_INTERVAL)
                .build();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(true);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        // fusedLocationClient.removeLocationUpdates(locationCallback); hủy yêu cầu cập nhật vị trí
    }
    private void userLocation(Context mContext){
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

// Kiểm tra và yêu cầu quyền truy cập vị trí (nếu cần)
        Log.d(TAG, mContext.toString());
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Lấy vị trí hiện tại của người dùng
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    // Tạo LatLng từ vị trí
                    LatLng userLocation = new LatLng(latitude, longitude);

                    // Thêm Marker (đánh dấu vị trí) lên bản đồ (tương tự như trước đó)
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(userLocation)
                            .title("Vị trí của bạn")
                            .snippet("Mô tả vị trí của bạn");

                    mMap.addMarker(markerOptions);

                    // Di chuyển camera đến vị trí người dùng
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(userLocation)
                            .zoom(15)
                            .build();

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            });

        }
    }
    private void init(View rootView) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        FloatingActionButton floatingActionButton = rootView.findViewById(R.id.btnUserLocation);
        floatingActionButton.setOnClickListener(v -> {
            Log.d(TAG, "On clicked");
//            userLocation(mContext);
            checkAndRequestGps(mContext);
            userLocationChanged(mContext);
        });
    }

    private void checkAndRequestGps(Context mContext) {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(2000)
                .build();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(mContext)
                .checkLocationSettings(builder.build());

        task.addOnCompleteListener(task1 -> {
            try {
                task1.getResult(ApiException.class);
                // GPS is enabled, proceed with map functionality.
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Prompt the user to enable GPS by displaying a dialog.
                            exception
                                    .getStatus()
                                    .startResolutionForResult(
                                            requireActivity(),
                                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED
                                    );
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // GPS settings are not available on the device, handle accordingly.
                        break;
                }
            }
        });
        /*
        *
result.addOnSuccessListener(locationSettingsResponse -> {
    // TODO GPS đã được bật
});

result.addOnFailureListener(e -> {
    if (e instanceof ResolvableApiException) {
        // TODO GPS bị tắt, yêu cầu người dùng bật GPS
        try {
            ResolvableApiException resolvable = (ResolvableApiException) e;
            resolvable.startResolutionForResult(requireActivity(), REQUEST_ENABLE_GPS);
        } catch (IntentSender.SendIntentException sendEx) {
            // Không thể hiển thị cửa sổ yêu cầu bật GPS
        }
    }
});

        * */
    }

}