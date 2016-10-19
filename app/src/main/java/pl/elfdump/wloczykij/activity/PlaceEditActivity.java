package pl.elfdump.wloczykij.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.klinker.android.sliding.SlidingActivity;

import java.io.File;
import java.io.IOException;

import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.network.api.APIManager;
import pl.elfdump.wloczykij.network.api.APIRequestException;
import pl.elfdump.wloczykij.network.api.models.Place;

public class PlaceEditActivity extends SlidingActivity implements View.OnClickListener {

    private static final int CAMERA_REQUEST = 1888;

    private Place place;
    private File photoFile;

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
                findViewById(R.id.add_place_button).setEnabled(false);
                if (photoFile == null) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    photoFile = new File(getExternalCacheDir(), "photo" + System.currentTimeMillis() + ".jpg");
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } else {
                    savePlace();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                savePlace();
            } else {
                findViewById(R.id.add_place_button).setEnabled(true);
                photoFile.delete();
                photoFile = null;
            }
        }
    }

    private void savePlace() {
        new AsyncTask<Place, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Place... params) {
                try {
                    APIManager api = Wloczykij.api;
                    place = api.cache(Place.class).save(place);
                    api.uploadImage(place.getPhotoUploadUrl(), "image/jpeg", photoFile);
                    photoFile.delete();

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
    }
}
