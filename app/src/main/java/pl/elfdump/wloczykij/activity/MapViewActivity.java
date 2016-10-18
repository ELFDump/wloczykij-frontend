package pl.elfdump.wloczykij.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.Wloczykij;
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

        findViewById(R.id.action_a).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO

                Place place = new Place();
                place.setName("Jakieś testowe miejsce");
                place.setLat(12);
                place.setLng(34);
                place.addTag("test");

                new AsyncTask<Place, Void, Place>() {
                    @Override
                    protected Place doInBackground(Place... params) {
                        try {
                            return Wloczykij.api.manager(Place.class).save(params[0]);
                        } catch (APIRequestException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(Place place) {
                        if (place == null) {
                            Toast.makeText(MapViewActivity.this, "Failed to add place", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MapViewActivity.this, "Place added! " + place.getResourceUrl(), Toast.LENGTH_LONG).show();
                        }
                    }
                }.execute(place);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng lodz = new LatLng(51.759248, 19.455983);
        mMap.addMarker(new MarkerOptions().position(lodz).title("Łódź").snippet("Opis"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lodz, 15));

        new AsyncTask<Void, Void, List<Place>>() {
            @Override
            protected List<Place> doInBackground(Void... params) {
                try {
                    return Wloczykij.api.manager(Place.class).getAll();
                } catch (APIRequestException e) {
                    // TODO: handle errors
                    e.printStackTrace();
                    return null;
                }
            }

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
