package com.positv.maplocationapps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.positv.maplocationapps.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GetAddressTask.OnTaskCompleted {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    FusedLocationProviderClient fusedLocationProviderClient;
    static final int REQUEST_LOCATION_PERMISSION=1;
    private TextView locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        locationTextView=findViewById(R.id.lokasi);
        fusedLocationProviderClient=LocationServices
                .getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        /*LatLng solo = new LatLng(-7.55, 110.82);
        mMap.addMarker(new MarkerOptions().position(solo).title("Marker in Solo"));
        float zoom=15;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(solo,zoom));*/
        getLocation();
    }
    private void getLocation(){
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }else {
            /*fusedLocationProviderClient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!=null){

                    }
                }
            });*/
            fusedLocationProviderClient.requestLocationUpdates(getLocationRequest(),
                    new LocationCallback(){
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            Location location=locationResult.getLastLocation();
                            if(location!=null){
                                String lat=location.getLatitude()+"";
                                String lng=location.getLongitude()+"";
                                LatLng loc=new LatLng(location.getLatitude(),location.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(loc)
                                        .title("LAT:"+lat+" - LNG:"+lng));
                                float zoom=15;
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,zoom));
                                new GetAddressTask(MapsActivity.this,
                                        MapsActivity.this).execute(location);
                            }
                        }
                    }, null);
        }
    }
    private LocationRequest getLocationRequest(){
        LocationRequest locationRequest=new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION:
                if(grantResults.length>0 && grantResults[0]
                        ==PackageManager.PERMISSION_GRANTED){
                    getLocation();
                }
        }
    }

    @Override
    public void onTaskCompleted(String result) {
        locationTextView.setText("Alamat:"+result);
    }
}