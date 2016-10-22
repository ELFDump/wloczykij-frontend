package pl.elfdump.wloczykij.activity;

import android.os.Bundle;
import com.klinker.android.sliding.SlidingActivity;
import pl.elfdump.wloczykij.R;

public class FilterTagsActivity extends SlidingActivity {

    @Override
    public void init(Bundle savedInstanceState) {
        disableHeader();
        setContent(R.layout.activity_filter_tags);
    }
}
