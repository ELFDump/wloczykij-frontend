package pl.elfdump.wloczykij.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.klinker.android.sliding.SlidingActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.network.api.models.Place;
import pl.elfdump.wloczykij.network.api.models.Tag;
import pl.elfdump.wloczykij.planner.PathPlanner;
import pl.elfdump.wloczykij.planner.PlacePlanner;
import pl.elfdump.wloczykij.ui.PlaceDetailsItem;
import pl.elfdump.wloczykij.ui.TagsSelectorController;
import pl.elfdump.wloczykij.utils.PlaceUtil;

public class GenerateTripActivity extends SlidingActivity implements View.OnClickListener {

    private static final int RC_ADD_PLACE = 4444;
    private static final int RC_SELECT_START_POS = 4445;

    private TagsSelectorController tagsSelectorController;

    private LatLng startPoint;
    private Set<String> addedManually = new HashSet<>();
    private List<Place> suggestedPlaces = new LinkedList<>();

    @Override
    public void init(Bundle savedInstanceState) {
        disableHeader();
        enableFullscreen();

        setContent(R.layout.activity_generate_trip);

        findViewById(R.id.trip_change_starting_pos).setOnClickListener(this);
        findViewById(R.id.trip_goto_settings).setOnClickListener(this);
        findViewById(R.id.trip_add_place).setOnClickListener(this);
        findViewById(R.id.trip_generate).setOnClickListener(this);
        findViewById(R.id.trip_include_visited).setOnClickListener(this);
        findViewById(R.id.trip_hide_restaurants).setOnClickListener(this);

        assert getIntent() != null;
        startPoint = getIntent().getParcelableExtra("startPoint");

        ListView listView = (ListView) findViewById(R.id.tag_selector_buttons_row);
        tagsSelectorController = new TagsSelectorController(listView, null);

        updatePlaces();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.trip_goto_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.trip_add_place:
                startActivityForResult(new Intent(this, SavedPlacesActivity.class), RC_ADD_PLACE);
                break;

            case R.id.trip_generate:
                generateAndShowOnMap();
                break;

            case R.id.trip_change_starting_pos:
                Intent intent = new Intent(this, MapViewActivity.class);
                startActivityForResult(intent, RC_SELECT_START_POS);
                break;

            case R.id.trip_include_visited:
            case R.id.trip_hide_restaurants:
                updatePlaces();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_ADD_PLACE) {
            if (resultCode == RESULT_OK) {
                String placeId = data.getStringExtra("place");
                Log.d(Wloczykij.TAG, "Add " + placeId);
                addedManually.add(placeId);
                updatePlaces();
            }
        }

        if (requestCode == RC_SELECT_START_POS) {
            if (resultCode == RESULT_OK) {
                startPoint = data.getParcelableExtra("position");
                Log.d(Wloczykij.TAG, "New start point: " + startPoint);
                updatePlaces();
            }
        }
    }

    private List<String> getPlacesList() {
        List<String> placeList = new LinkedList<>();
        for (String placeId : addedManually) {
            placeList.add(placeId);
        }
        for (Place place : suggestedPlaces) {
            if (!placeList.contains(place.getId())) {
                placeList.add(place.getId());
            }
        }
        return placeList;
    }

    private List<PlaceDetailsItem> generateData() {
        ArrayList<PlaceDetailsItem> list = new ArrayList<>();
        for (String placeId : getPlacesList()) {
            Place place = Wloczykij.api.cache(Place.class).get(placeId);
            PlaceDetailsItem item = new PlaceDetailsItem(PlaceUtil.getMatchingIcon(place), place.getName(), true, PlaceDetailsItem.ITEM_GROUP);
            item.setChecked(true);
            item.setId(placeId);
            list.add(item);
        }
        return list;
    }

    private List<String> getSelectedPlacesList() {
        return tagsSelectorController.getSelectedTags();
    }

    private String getAddress(LatLng position) {
        String out = String.format("[%.6f, %.6f]", position.latitude, position.longitude);
        try {
            List<Address> addresses = new Geocoder(this).getFromLocation(position.latitude, position.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<>();

                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }

                out = TextUtils.join(", ", addressFragments);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    private void updatePlaces() {
        String posStr = startPoint != null ? getAddress(startPoint) : "-";
        ((TextView) findViewById(R.id.trip_starting_pos)).setText(getString(R.string.trip_starting_pos, posStr));

        boolean includeVisited = ((Switch) findViewById(R.id.trip_include_visited)).isChecked();
        boolean hideFood = ((Switch) findViewById(R.id.trip_hide_restaurants)).isChecked();
        Log.d(Wloczykij.TAG, includeVisited+" "+hideFood);

        if (startPoint != null) {
            List<Place> places = new LinkedList<>(Wloczykij.api.cache(Place.class).getAll());
            Iterator<Place> iter = places.iterator();
            while (iter.hasNext()) {
                Place place = iter.next();
                if (!includeVisited && place.getVisit() != null) {
                    iter.remove();
                } else if (hideFood) {
                    for (String tagId : place.getTags()) {
                        Tag tag = Wloczykij.api.cache(Tag.class).get(tagId);
                        Tag rootTag = PlaceUtil.findTopLevel(tag);
                        if (rootTag.getName().equals("Jedzenie")) {
                            iter.remove();
                            break;
                        }
                    }
                }
            }

            suggestedPlaces = new PlacePlanner()
                .setPlaces(places)
                .setIncludedTags(Wloczykij.session.loggedOnUser.getFollowedTags())
                .setStartPoint(startPoint)
                .findPlaces();
        }

        tagsSelectorController.loadData(generateData());
    }

    private void generateAndShowOnMap() {
        new AsyncTask<List<String>, Void, PathPlanner.PlannedPath>() {
            @Override
            protected void onPreExecute() {
                findViewById(R.id.trip_generate).setEnabled(false);
            }

            @Override
            protected PathPlanner.PlannedPath doInBackground(List<String>... params) {
                try {
                    return new PathPlanner(Wloczykij.httpClient, getString(R.string.google_maps_key))
                        .setStartPoint(startPoint)
                        .addPathPlaces(Wloczykij.api.cache(Place.class).getMultiple(params[0]))
                        .plan();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(PathPlanner.PlannedPath path) {
                findViewById(R.id.trip_generate).setEnabled(true);
                if (path == null) {
                    Toast.makeText(GenerateTripActivity.this, R.string.error_occurred, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(GenerateTripActivity.this, MapViewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.putParcelableArrayListExtra("plannedPath", path.getLine());
                    startActivity(intent);
                }
            }
        }.execute(getSelectedPlacesList());
    }
}

