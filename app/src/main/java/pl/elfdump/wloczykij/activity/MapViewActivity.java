package pl.elfdump.wloczykij.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.data.DataManager;
import pl.elfdump.wloczykij.data.map.MarkerManager;
import pl.elfdump.wloczykij.utils.MapUtilities;

import java.util.List;

import pl.elfdump.wloczykij.network.api.models.Place;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    private GoogleMap mMap;

    private static final int MAP_LOCATION_PERMISSION_REQUEST = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        findViewById(R.id.action_a).setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            }, MAP_LOCATION_PERMISSION_REQUEST);
        } else {
            mMap.setMyLocationEnabled(true);
        }

        DataManager dataManager = Wloczykij.getSession().dataManager;
        List<Place> places = dataManager.placeStorage.getPlaces();

        for(Place p : places){
            LatLng position = MapUtilities.getPosition(p);
            MarkerOptions markerOptions = new MarkerOptions().position(position).title(p.getName());
            Marker marker = mMap.addMarker(markerOptions);
            marker.showInfoWindow();

            dataManager.markerManager.references.put(marker, p);
        }

        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MAP_LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(this, PlaceDetailsActivity.class);

        MarkerManager markerManager = Wloczykij.getSession().dataManager.markerManager;
        Place place = markerManager.references.get(marker);

        intent.putExtra("place", place);
        startActivity(intent);
        return false;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, AddPlaceActivity.class);
        startActivity(intent);
    }
}
