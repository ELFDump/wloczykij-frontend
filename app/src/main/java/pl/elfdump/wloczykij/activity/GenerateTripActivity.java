package pl.elfdump.wloczykij.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.klinker.android.sliding.SlidingActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.network.api.models.Place;
import pl.elfdump.wloczykij.planner.PathPlanner;
import pl.elfdump.wloczykij.planner.PlacePlanner;
import pl.elfdump.wloczykij.ui.PlaceDetailsItem;
import pl.elfdump.wloczykij.ui.TagsSelectorController;
import pl.elfdump.wloczykij.utils.PlaceUtil;

public class GenerateTripActivity extends SlidingActivity implements View.OnClickListener {

    private static final int RC_ADD_PLACE = 4444;

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
                Toast.makeText(this, R.string.todo, Toast.LENGTH_SHORT).show(); // TODO
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

    private void updatePlaces() {
        String posStr = startPoint != null ? startPoint.toString() : "-"; // TODO
        ((TextView) findViewById(R.id.trip_starting_pos)).setText(getString(R.string.trip_starting_pos, posStr));

        if (startPoint != null) {
            suggestedPlaces = new PlacePlanner()
                .setPlaces(Wloczykij.api.cache(Place.class).getAll())
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
                    intent.putParcelableArrayListExtra("plannedPath", path.getLine());
                    startActivity(intent);
                }
            }
        }.execute(getSelectedPlacesList());
    }
}

