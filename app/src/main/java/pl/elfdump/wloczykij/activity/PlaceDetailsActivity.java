package pl.elfdump.wloczykij.activity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.Toast;

import com.klinker.android.sliding.SlidingActivity;
import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.ui.PlaceDetailsItem;
import pl.elfdump.wloczykij.ui.PlaceDetailsListAdapter;
import pl.elfdump.wloczykij.network.api.APIRequestException;
import pl.elfdump.wloczykij.network.api.models.Place;
import pl.elfdump.wloczykij.ui.views.ButtonAction;

import java.util.ArrayList;
import java.util.Random;

public class PlaceDetailsActivity extends SlidingActivity {

    private Place place;
    private AsyncTask<String, Void, Bitmap> downloadImageTask;

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.activity_place_details);
        place = (Place) getIntent().getSerializableExtra("place");

        setTitle(place.getName());
        setImage(R.drawable.background_login); // TODO: Replace with PLEASE WAIT, LOADING image

        setPrimaryColors(
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimaryDark)
        );

        enableFullscreen();

        PlaceDetailsListAdapter adapter = new PlaceDetailsListAdapter(this, generateData());
        ListView listView = (ListView) findViewById(R.id.details);
        listView.setAdapter(adapter);

        String[] photos = place.getPhotos();
        if (photos.length > 0) {
            // TODO: Display all photos, not just one at random
            String photo = photos[new Random().nextInt(photos.length)];
            downloadImageTask = new AsyncTask<String, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... urls) {
                    if (urls.length != 1) return null;
                    try {
                        return Wloczykij.api.downloadImage(urls[0]);
                    } catch (APIRequestException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    if (bitmap != null) {
                        setImage(bitmap);
                    } else {
                        Toast.makeText(PlaceDetailsActivity.this, getString(R.string.image_load_error), Toast.LENGTH_LONG).show();
                    }
                }
            }.execute(photo);
        }

    }

    @Override
    public void onStart(){
        super.onStart();
        //Animation move_from_top = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_from_top);
        //Animation move_from_bottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_from_bottom);
        Animation bounce = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        // TODO
        //findViewById(R.id.rating_super).startAnimation(move_from_top);
        ButtonAction buttonAction = (ButtonAction) findViewById(R.id.rating_good);
        buttonAction.getImageView().startAnimation(bounce);
        //findViewById(R.id.rating_ok).startAnimation(move_from_top);
        //findViewById(R.id.rating_awful).startAnimation(move_from_bottom);
    }

    @Override
    public void finish() {
        if (downloadImageTask != null) {
            downloadImageTask.cancel(true);
        }
        super.finish();
    }

    private ArrayList<PlaceDetailsItem> generateData() {
        ArrayList<PlaceDetailsItem> models = new ArrayList<>();

        String description = place.getDescription();
        if(!description.isEmpty()){
            models.add(new PlaceDetailsItem(R.drawable.ic_info_outline_black_24dp, description));
        }

        models.add(new PlaceDetailsItem(R.drawable.ic_face_black_24dp, place.getAuthor()));

        String tags = "";
        boolean first = true;
        for (String tag : place.getTags()) {
            if (!first) tags += ", ";
            tags += "#"+tag;
            first = false;
        }
        models.add(new PlaceDetailsItem(R.drawable.ic_local_offer_black_24dp, tags));

        return models;
    }
}
