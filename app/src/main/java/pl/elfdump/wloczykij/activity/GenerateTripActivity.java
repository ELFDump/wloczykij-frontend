package pl.elfdump.wloczykij.activity;

import android.os.Bundle;
import com.klinker.android.sliding.SlidingActivity;
import pl.elfdump.wloczykij.R;

public class GenerateTripActivity extends SlidingActivity {

    @Override
    public void init(Bundle savedInstanceState) {
        disableHeader();
        enableFullscreen();

        setContent(R.layout.activity_generate_trip);
    }
}

