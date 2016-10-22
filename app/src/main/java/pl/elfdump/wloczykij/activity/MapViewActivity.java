package pl.elfdump.wloczykij.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.network.api.APIRequestException;
import pl.elfdump.wloczykij.network.api.models.Place;
import pl.elfdump.wloczykij.utils.MapUtil;
import pl.elfdump.wloczykij.utils.NetworkUtil;
import pl.elfdump.wloczykij.utils.PlaceUtil;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, View.OnClickListener, GoogleMap.OnMyLocationChangeListener, GoogleMap.OnMyLocationButtonClickListener {

    private static final int MAP_LOCATION_PERMISSION_REQUEST = 1234;
    private static final int RC_PLACE_EDIT = 4321;
    private static final int RC_PLACE_DETAILS = 4322;

    private GoogleMap mMap;
    private HashMap<Marker, String> markers = new HashMap<>();

    private IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    private BroadcastReceiver broadcast = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            FrameLayout layout = (FrameLayout) findViewById(R.id.layout_offline);

            if(NetworkUtil.isOnline(context)){
                layout.setVisibility(View.GONE);
            }else{
                layout.setVisibility(View.VISIBLE);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);

        findViewById(R.id.action_add_place).setOnClickListener(this);
        findViewById(R.id.action_plan_trip).setOnClickListener(this);
        findViewById(R.id.action_refresh).setOnClickListener(this);
        findViewById(R.id.button_filter).setOnClickListener(this);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            }, MAP_LOCATION_PERMISSION_REQUEST);
        } else {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMyLocationChangeListener(this);
        mMap.setOnMyLocationButtonClickListener(this);

        // Set starting camera position
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
            /* position = */ new LatLng(51.759248, 19.455983), // Centrum Łodzi
            /* zoom = */ 12,
            /* tilt = */ 90,
            /* orientation = */ 0
        )));

        refreshMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcast, filter);

        if (mMap != null) {
            refreshMap();
        }
    }

    @Override
    public void onPause(){
        unregisterReceiver(broadcast);
        super.onPause();
    }

    private void refreshMap() {
        Log.d(Wloczykij.TAG, "Start cache update");
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    Wloczykij.api.manager(Place.class).cache().update();
                    return true;
                } catch (APIRequestException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    Log.i(Wloczykij.TAG, "Updated place cache from server");
                    updateMap();
                } else {
                    Toast.makeText(MapViewActivity.this, getString(R.string.map_load_error), Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void updateMap() {
        Log.d(Wloczykij.TAG, "Update map markers");

        // Add new markers
        for (Place p : Wloczykij.api.cache(Place.class).getAll()) {
            if (!markers.containsValue(p.getResourceUrl())) {
                Marker marker = mMap.addMarker(new MarkerOptions().position(MapUtil.getPosition(p)).title(p.getName()));
                marker.setIcon(PlaceUtil.getMatchingIcon(p));
                markers.put(marker, p.getResourceUrl());
            }
        }

        // Update/remove existing markers
        for (Marker marker : markers.keySet()) {
            Place p = Wloczykij.api.cache(Place.class).get(markers.get(marker));
            if (p == null) {
                marker.remove();
                markers.remove(marker);
            } else {
                marker.setPosition(MapUtil.getPosition(p));
                marker.setIcon(PlaceUtil.getMatchingIcon(p));
                marker.setTitle(p.getName());
            }
        }
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

    private CameraPosition prevCameraPosition;

    @Override
    public boolean onMarkerClick(Marker marker) {
        prevCameraPosition = mMap.getCameraPosition();
        final Place place = Wloczykij.api.cache(Place.class).get(markers.get(marker));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MapUtil.getPosition(place), 17.5f), 1500, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                Log.d(Wloczykij.TAG, "Animation finished");

                Intent intent = new Intent(MapViewActivity.this, PlaceDetailsActivity.class);
                intent.putExtra("place", place);
                startActivityForResult(intent, RC_PLACE_DETAILS);
            }

            @Override
            public void onCancel() {
                Log.d(Wloczykij.TAG, "Animation cancelled");
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private boolean addingPlace = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_plan_trip:
                Toast.makeText(this, getString(R.string.todo), Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_add_place:
                addingPlace = true;
                ((FloatingActionsMenu) findViewById(R.id.multiple_actions)).collapse();
                Toast.makeText(this, getString(R.string.place_add_click_map), Toast.LENGTH_LONG).show();
                break;

            case R.id.action_refresh:
                ((FloatingActionsMenu) findViewById(R.id.multiple_actions)).collapse();
                refreshMap();
                break;

            case R.id.button_filter:
                Intent intent = new Intent(this, FilterTagsActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onMapClick(LatLng coords) {
        if (addingPlace) {
            addingPlace = false;
            Log.d("Wloczykij", "Map click: " + coords);

            Place place = new Place();
            place.setLat(coords.latitude);
            place.setLng(coords.longitude);
            
            Intent intent = new Intent(this, PlaceEditActivity.class);
            intent.putExtra("place", place);
            startActivityForResult(intent, RC_PLACE_EDIT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_PLACE_EDIT) {
            if (resultCode == RESULT_OK) {
                String placeId = data.getStringExtra("place");
                Log.i(Wloczykij.TAG, "Finished editing place " + placeId);
                Place place = Wloczykij.api.cache(Place.class).get(placeId);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MapUtil.getPosition(place), 17.5f));
                updateMap();
            }
        }

        if (requestCode == RC_PLACE_DETAILS) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(prevCameraPosition), 1000, null);
        }
    }

    private boolean locationSet = false;
    private Location currentLocation;

    @Override
    public void onMyLocationChange(Location location) {
        currentLocation = location;
        if (!locationSet) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13.5f));
            locationSet = true;
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 17.5f));
        return true;
    }
}
