package ca.uoit.csci4100.samples.locationsample;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class ShowMapActivity extends FragmentActivity {
    private GoogleMap map;
    private double latitude, longitude;
    private BikeDBHelper bikeDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        bikeDBHelper = new BikeDBHelper(getApplicationContext());

        Intent callingIntent = getIntent();
        latitude = callingIntent.getDoubleExtra("latitude", 0.0);
        longitude = callingIntent.getDoubleExtra("longitude", 0.0);

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (map != null) {
                showMapLocation();
            }
        }
    }
    
    private void showMapLocation() {
        List<Bike> bikes = bikeDBHelper.getAllBikes();

        LatLng position = new LatLng(latitude, longitude);
        for (Bike bike : bikes)
        {
            LatLng bikePos = new LatLng(bike.getLatitude(), bike.getLongitude());
            map.addMarker(new MarkerOptions().position(bikePos).title("Bike Station " + bike.getName()));
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 14f));

        map.setTrafficEnabled(true);
        map.setBuildingsEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
    }
}
