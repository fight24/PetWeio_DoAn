package com.petweio.projectdoan.service;

import android.animation.TypeEvaluator;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class LatLngEvaluator implements TypeEvaluator<LatLng> {
    @Override
    public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
        double lat = (endValue.getLatitude() - startValue.getLatitude()) * fraction + startValue.getLatitude();
        double lng = (endValue.getLongitude() - startValue.getLongitude()) * fraction + startValue.getLongitude();
        Log.e("MapFragment", "LatLngEvaluator: "+"Lat: "+lat + ",Lng: "+lng);
        return new LatLng(lat, lng);
    }
}
