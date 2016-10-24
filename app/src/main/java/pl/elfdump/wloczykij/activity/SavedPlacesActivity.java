package pl.elfdump.wloczykij.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;

import com.klinker.android.sliding.SlidingActivity;

import java.util.ArrayList;

import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.network.api.models.Place;
import pl.elfdump.wloczykij.ui.PlaceDetailsItem;
import pl.elfdump.wloczykij.ui.TagsSelectorController;
import pl.elfdump.wloczykij.utils.PlaceUtil;

public class SavedPlacesActivity extends SlidingActivity implements TagsSelectorController.ClickListener {

    private TagsSelectorController tagsSelectorController;

    @Override
    public void init(Bundle savedInstanceState) {
        disableHeader();
        enableFullscreen();
        setContent(R.layout.activity_saved_places);

        ListView listView = (ListView) findViewById(R.id.tag_selector_buttons_row);

        tagsSelectorController = new TagsSelectorController(listView, new ArrayList<String>());
        tagsSelectorController.setClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tagsSelectorController.loadData(generateData());
    }

    private ArrayList<PlaceDetailsItem> generateData() {
        ArrayList<PlaceDetailsItem> list = new ArrayList<>();
        for (String placeId : Wloczykij.session.loggedOnUser.getSavedPlaces()) {
            Place place = Wloczykij.api.cache(Place.class).get(placeId);
            list.add(new PlaceDetailsItem(PlaceUtil.getMatchingIcon(place), place.getName()));
        }
        return list;
    }

    @Override
    public void onListItemClick(int index) {
        String placeId = Wloczykij.session.loggedOnUser.getSavedPlaces().get(index);
        if (getCallingActivity() == null) {
            Intent intent = new Intent(this, PlaceDetailsActivity.class);
            intent.setData(Uri.parse(placeId));
            startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.putExtra("place", placeId);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
