package pl.elfdump.wloczykij.activity;

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
import pl.elfdump.wloczykij.models.Place;
import pl.elfdump.wloczykij.network.tasks.UpdatePlacesTask;

import java.util.List;

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

        LatLng lodz = new LatLng(51.759248, 19.455983);
        mMap.addMarker(new MarkerOptions().position(lodz).title("Łódź").snippet("Opis"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lodz, 15));

        new UpdatePlacesTask() {
            @Override
            protected void onPostExecute(List<Place> places) {
                for(Place place : places) {
                    Log.d("Place", place.getName());
                    mMap.addMarker(new MarkerOptions().position(new LatLng(place.getLat(), place.getLng())).title(place.getName()));
                }
            }
        }.execute();

        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false; // TODO (Adikso)
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
