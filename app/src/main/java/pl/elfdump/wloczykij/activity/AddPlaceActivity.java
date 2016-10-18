package pl.elfdump.wloczykij.activity;

import android.os.Bundle;
import android.view.View;
import com.klinker.android.sliding.SlidingActivity;
import pl.elfdump.wloczykij.R;

public class AddPlaceActivity extends SlidingActivity implements View.OnClickListener {

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.activity_add_place);

        disableHeader();
        enableFullscreen();

        findViewById(R.id.add_place_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
}
