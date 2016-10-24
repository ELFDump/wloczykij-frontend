package pl.elfdump.wloczykij.activity;

import android.os.Bundle;
import com.klinker.android.sliding.SlidingActivity;
import pl.elfdump.wloczykij.R;

public class AboutActivity extends SlidingActivity {

    @Override
    public void init(Bundle savedInstanceState) {
        enableFullscreen();
        disableHeader();

        setContent(R.layout.activity_about);
    }
}