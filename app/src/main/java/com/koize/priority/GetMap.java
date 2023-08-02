package com.koize.priority;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

public class GetMap extends AppCompatActivity implements OnMapReadyCallback {
    private TextView currentLatLon;
    private TextView locationLatLon;
    private double locationLat;
    private double locationLon;
    private double currentLat;
    private double currentLon;
    private Chip getCurrentLocation;
    private Chip save;
    private Chip cancel;
    private static final int get_map_request_code= 998;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_map);
        currentLatLon = findViewById(R.id.gmap_current_lat_long);
        locationLatLon = findViewById(R.id.gmap_location_lat_long);
        getCurrentLocation = findViewById(R.id.gmap_get_current_location);
        GPSTracker gpsTracker = new GPSTracker(GetMap.this);
        currentLat = gpsTracker.getLatitude();
        currentLon = gpsTracker.getLongitude();
        currentLatLon.setText("Current Location: " + currentLat + ", " + currentLon);
        getCurrentLocation.setOnClickListener(v -> {
            currentLat = gpsTracker.getLatitude();
            currentLon = gpsTracker.getLongitude();
            currentLatLon.setText("Current Location: " + currentLat + ", " + currentLon);
        });
        save = findViewById(R.id.gmap_save);
        save.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("lat", locationLat);
            intent.putExtra("lon", locationLon);
            setResult(RESULT_OK, intent);
            finish();
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
                .compassEnabled(true)
                .rotateGesturesEnabled(true)
                .tiltGesturesEnabled(true);


        locationLat = getIntent().getDoubleExtra("lat", currentLat);
        locationLon = getIntent().getDoubleExtra("lon", currentLon);
        locationLatLon.setText("Selected Location: " + locationLat + ", " + locationLon);



    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng currentLocation = new LatLng(locationLat, locationLon);
        map.addMarker(new MarkerOptions()
                .position(currentLocation)
                        .draggable(true)
                .title("Selected location"));
        map.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

        map.setOnMarkerDragListener( new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(com.google.android.gms.maps.model.Marker marker) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onMarkerDragEnd(com.google.android.gms.maps.model.Marker marker) {
                // TODO Auto-generated method stub
                LatLng dragPosition = marker.getPosition();
                double dragLat = dragPosition.latitude;
                double dragLong = dragPosition.longitude;
                locationLat = dragLat;
                locationLon = dragLong;
                locationLatLon.setText("Selected Location: " + locationLat + ", " + locationLon);

            }

            @Override
            public void onMarkerDrag(com.google.android.gms.maps.model.Marker marker) {
                // TODO Auto-generated method stub
                LatLng dragPosition = marker.getPosition();
                double dragLat = dragPosition.latitude;
                double dragLong = dragPosition.longitude;
                locationLat = dragLat;
                locationLon = dragLong;
                locationLatLon.setText("Selected Location: " + locationLat + ", " + locationLon);

            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make((findViewById(android.R.id.content)), "Please enable location permission", Snackbar.LENGTH_LONG).show();
            return;
        }
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this::onMyLocationButtonClick);
        map.setOnMyLocationClickListener(this::onMyLocationClick);

    }
    public void onMyLocationClick(@NonNull Location location) {

    }

    public boolean onMyLocationButtonClick() {

        return false;
    }
}