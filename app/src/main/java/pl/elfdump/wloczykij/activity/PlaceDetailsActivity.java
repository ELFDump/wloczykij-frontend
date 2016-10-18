package pl.elfdump.wloczykij.activity;

import android.os.Bundle;
import android.widget.ListView;
import com.klinker.android.sliding.SlidingActivity;
import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.models.ui.PlaceDetailsItem;
import pl.elfdump.wloczykij.models.ui.PlaceDetailsListAdapter;
import pl.elfdump.wloczykij.network.api.models.Place;

import java.util.ArrayList;
import java.util.Arrays;

public class PlaceDetailsActivity extends SlidingActivity {

    private Place place;

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.activity_place_details);
        place = (Place) getIntent().getSerializableExtra("place");

        setTitle(place.getName());
        setImage(R.drawable.background_login);
        enableFullscreen();

        PlaceDetailsListAdapter adapter = new PlaceDetailsListAdapter(this, generateData());
        ListView listView = (ListView) findViewById(R.id.details);
        listView.setAdapter(adapter);
    }

    private ArrayList<PlaceDetailsItem> generateData(){
        ArrayList<PlaceDetailsItem> models = new ArrayList<>();
        models.add(new PlaceDetailsItem(R.mipmap.ic_launcher, place.getAuthor()));
        models.add(new PlaceDetailsItem(R.mipmap.ic_launcher, Arrays.toString(place.getTags())));

        return models;
    }
}
