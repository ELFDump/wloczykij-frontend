package pl.elfdump.wloczykij.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import pl.elfdump.wloczykij.data.PlaceStorage;
import pl.elfdump.wloczykij.data.map.MarkerManager;
import pl.elfdump.wloczykij.models.Place;
import pl.elfdump.wloczykij.network.tasks.UpdatePlacesTask;
import pl.elfdump.wloczykij.utils.MapUtilities;

import java.util.List;
import pl.elfdump.wloczykij.network.api.APIRequestException;
import pl.elfdump.wloczykij.network.api.models.Place;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);

        LatLng lodz = new LatLng(51.759248, 19.455983);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lodz, 15));

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
}
