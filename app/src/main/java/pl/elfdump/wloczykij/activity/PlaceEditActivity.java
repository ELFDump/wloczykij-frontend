package pl.elfdump.wloczykij.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.klinker.android.sliding.SlidingActivity;

import java.io.File;

import me.gujun.android.taggroup.TagGroup;
import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.network.api.APIManager;
import pl.elfdump.wloczykij.network.api.APIRequestException;
import pl.elfdump.wloczykij.network.api.models.Place;

public class PlaceEditActivity extends SlidingActivity implements View.OnClickListener {

    private static final int CAMERA_REQUEST = 1888;

    private Place place;
    private File photoFile;

    private Bitmap bitmap;

    @Override
    public void init(Bundle state) {
        setContent(R.layout.activity_place_edit);
        place = (Place) getIntent().getSerializableExtra("place");

        disableHeader();
        enableFullscreen();

        findViewById(R.id.add_place_button).setOnClickListener(this);
        findViewById(R.id.button_add_image).setOnClickListener(this);

        if(state != null){
            String path = state.getString("filePath");
            if(path != null){
                photoFile = new File(path);
                setThumbnail(path);

                bitmap = state.getParcelable("thumbnail_bitmap");
                if(bitmap != null){
                    setThumbnail(path);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state){
        if(photoFile != null){
            String filePath = photoFile.getPath();
            state.putString("filePath", filePath);
            if(bitmap != null){
                state.putParcelable("thumbnail_bitmap", bitmap);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.add_place_button:
                String name = ((EditText) findViewById(R.id.place_name)).getText().toString();

                if (photoFile == null) {
                    Toast.makeText(PlaceEditActivity.this, getString(R.string.photo) + " " + getString(R.string.error_required), Toast.LENGTH_SHORT).show();
                    return;
                }else if(name.isEmpty()){
                    Toast.makeText(PlaceEditActivity.this, getString(R.string.name) + " " + getString(R.string.error_required_a), Toast.LENGTH_SHORT).show();
                    return;
                }

                findViewById(R.id.add_place_button).setEnabled(false);

                place.setName(name);
                place.setTags(((TagGroup) findViewById(R.id.tag_group)).getTags());

                savePlace();
                break;
            case R.id.button_add_image:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                photoFile = new File(getExternalCacheDir(), "photo" + System.currentTimeMillis() + ".jpg");
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                setThumbnail(photoFile.getPath());
            } else {
                findViewById(R.id.add_place_button).setEnabled(true);
                photoFile.delete();
                photoFile = null;
            }
        }
    }

    private void setThumbnail(String path){
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... paths) {
                if(bitmap == null || !photoFile.getPath().equals(paths[0])){
                    bitmap = BitmapFactory.decodeFile(paths[0]);
                }

                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                ((ImageView) findViewById(R.id.thumb_add_image)).setImageBitmap(bitmap);
                findViewById(R.id.button_add_image).setAlpha(0);
            }
        }.execute(path);
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
