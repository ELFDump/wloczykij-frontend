package pl.elfdump.wloczykij.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import com.klinker.android.sliding.SlidingActivity;
import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.network.api.models.Tag;
import pl.elfdump.wloczykij.ui.PlaceDetailsItem;
import pl.elfdump.wloczykij.ui.TagsGroupItem;
import pl.elfdump.wloczykij.ui.TagsSelectorController;
import pl.elfdump.wloczykij.utils.PlaceUtil;

import java.util.*;

public class FilterTagsActivity extends SlidingActivity {

    private TagsSelectorController tagsSelectorController;

    @Override
    public void init(Bundle state) {
        disableHeader();
        setContent(R.layout.activity_filter_tags);

        ArrayList<String> previousFilter = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            previousFilter = extras.getStringArrayList("filter");
        }

        ListView listView = (ListView) findViewById(R.id.tag_selector_buttons_row);

        tagsSelectorController = new TagsSelectorController(listView, previousFilter);
        tagsSelectorController.loadData();

        if (state != null) {
            if(state.containsKey("selectedTags")){
                tagsSelectorController.applySelections(state.getStringArrayList("selectedTags"));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state){
        if(tagsSelectorController != null){
            state.putStringArrayList("selectedTags", tagsSelectorController.getSelectedTags());
        }
    }

    @Override
    public void onPause(){
        Intent intent = new Intent();
        Log.d("TEST", "SADASDASD");
        intent.putStringArrayListExtra("selectedTags", tagsSelectorController.getSelectedTags());
        for(String s : tagsSelectorController.getSelectedTags()){
            Log.d("AA", s);
        }

        setResult(RESULT_OK, intent);

        super.onPause();
    }

}
