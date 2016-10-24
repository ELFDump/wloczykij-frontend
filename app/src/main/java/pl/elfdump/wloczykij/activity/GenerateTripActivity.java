package pl.elfdump.wloczykij.activity;

import android.os.Bundle;
import android.widget.ListView;
import com.klinker.android.sliding.SlidingActivity;
import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.ui.TagsSelectorController;

public class GenerateTripActivity extends SlidingActivity {

    private TagsSelectorController tagsSelectorController;

    @Override
    public void init(Bundle savedInstanceState) {
        disableHeader();
        enableFullscreen();

        setContent(R.layout.activity_generate_trip);

        ListView listView = (ListView) findViewById(R.id.tag_selector_buttons_row);

        tagsSelectorController = new TagsSelectorController(listView, null);
        tagsSelectorController.loadData();
    }
}

