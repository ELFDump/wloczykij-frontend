package pl.elfdump.wloczykij.activity;

import android.os.Bundle;
import android.widget.ListView;
import com.klinker.android.sliding.SlidingActivity;
import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.network.api.models.Place;
import pl.elfdump.wloczykij.ui.PlaceDetailsItem;
import pl.elfdump.wloczykij.ui.TagsSelectorController;

import java.util.ArrayList;

public class SavedPlacesActivity extends SlidingActivity {

    private TagsSelectorController tagsSelectorController;

    @Override
    public void init(Bundle savedInstanceState) {
        disableHeader();
        enableFullscreen();
        setContent(R.layout.activity_saved_places);

        ListView listView = (ListView) findViewById(R.id.tag_selector_buttons_row);

        tagsSelectorController = new TagsSelectorController(listView, new ArrayList<String>());
        tagsSelectorController.loadData(/* Visited places ArrayList */);
    }

    private ArrayList<PlaceDetailsItem> generateData(){
        //PlaceDetailsItem item = new PlaceDetailsItem(icon, title);
        return null;
    }
}
