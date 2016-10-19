package pl.elfdump.wloczykij.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.klinker.android.sliding.SlidingActivity;
import me.gujun.android.taggroup.TagGroup;
import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.network.api.APIRequestException;
import pl.elfdump.wloczykij.network.api.models.Place;

public class PlaceEditActivity extends SlidingActivity implements View.OnClickListener {

    private Place place;

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.activity_place_edit);
        place = (Place) getIntent().getSerializableExtra("place");

        disableHeader();
        enableFullscreen();

        findViewById(R.id.add_place_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.add_place_button:
                place.setName(((EditText) findViewById(R.id.place_name)).getText().toString());
                place.setTags(((TagGroup) findViewById(R.id.tag_group)).getTags());
                new AsyncTask<Place, Void, Boolean>() {
                    @Override
                    protected void onPreExecute() {
                        findViewById(R.id.add_place_button).setEnabled(false);
                    }

                    @Override
                    protected Boolean doInBackground(Place... params) {
                        try {
                            place = Wloczykij.api.manager(Place.class).cache().save(place);
                            return true;
                        } catch (APIRequestException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }

                    @Override
                    protected void onPostExecute(Boolean success) {
                        if (success) {
                            Toast.makeText(PlaceEditActivity.this, getString(R.string.place_saved), Toast.LENGTH_SHORT).show();
                            Intent result = new Intent();
                            result.putExtra("place", place.getResourceUrl());
                            setResult(Activity.RESULT_OK, result);
                            finish();
                        } else {
                            Toast.makeText(PlaceEditActivity.this, getString(R.string.place_save_error), Toast.LENGTH_SHORT).show();
                            findViewById(R.id.add_place_button).setEnabled(true);
                        }
                    }
                }.execute(place);
                break;
        }
    }
}
