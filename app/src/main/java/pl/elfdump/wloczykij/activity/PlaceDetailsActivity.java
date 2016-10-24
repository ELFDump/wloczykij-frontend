package pl.elfdump.wloczykij.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.klinker.android.sliding.SlidingActivity;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.network.api.APIManager;
import pl.elfdump.wloczykij.network.api.models.User;
import pl.elfdump.wloczykij.ui.PlaceDetailsItem;
import pl.elfdump.wloczykij.ui.PlaceDetailsListAdapter;
import pl.elfdump.wloczykij.network.api.APIRequestException;
import pl.elfdump.wloczykij.network.api.models.Place;
import pl.elfdump.wloczykij.ui.views.ButtonAction;

import java.util.ArrayList;
import java.util.Random;

public class PlaceDetailsActivity extends SlidingActivity implements View.OnClickListener {

    private Place place;
    private AsyncTask<String, Void, Bitmap> downloadImageTask;

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.activity_place_details);

        setTitle(getIntent().getData().toString());
        setImage(R.drawable.background_login); // TODO: Replace with PLEASE WAIT, LOADING image

        setPrimaryColors(
            ContextCompat.getColor(this, R.color.colorPrimary),
            ContextCompat.getColor(this, R.color.colorPrimaryDark)
        );

        enableFullscreen();

        Wloczykij.session.reloginIfNeeded(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (place == null) {
            place = Wloczykij.api.cache(Place.class).get(getIntent().getData().toString());
            if (place == null)
                return;
        }

        setTitle(place.getName());

        PlaceDetailsListAdapter adapter = new PlaceDetailsListAdapter(this, generateData());
        ListView listView = (ListView) findViewById(R.id.details);
        listView.setAdapter(adapter);

        findViewById(R.id.place_action_visited).setOnClickListener(this);
        findViewById(R.id.place_action_save).setOnClickListener(this);
        findViewById(R.id.place_action_share).setOnClickListener(this);
        findViewById(R.id.place_action_report).setOnClickListener(this);
        findViewById(R.id.rating_super).setOnClickListener(this);
        findViewById(R.id.rating_good).setOnClickListener(this);
        findViewById(R.id.rating_ok).setOnClickListener(this);
        findViewById(R.id.rating_awful).setOnClickListener(this);

        updateVisitedButton(place.getVisit() != null);
        updateRatingButtons(place.getMyRating());
        updateAverageRatingText(place.getRatingAverage(), place.getRatingCount());
        updateSaved(isSaved());

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
                        Toast.makeText(PlaceDetailsActivity.this, R.string.image_load_error, Toast.LENGTH_LONG).show();
                    }
                }
            }.execute(photo);
        }
    }

    @Override
    public void finish() {
        if (downloadImageTask != null) {
            downloadImageTask.cancel(false);
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
        if (!place.getTags().isEmpty()) {
            boolean first = true;
            for (String tag : place.getTags()) {
                if (!first) tags += ", ";
                tags += "#" + tag;
                first = false;
            }
        } else {
            tags = getString(R.string.no_tags);
        }
        models.add(new PlaceDetailsItem(R.drawable.ic_local_offer_black_24dp, tags));

        return models;
    }

    private void updateVisitedButton(boolean visited) {
        ButtonAction button = (ButtonAction) findViewById(R.id.place_action_visited);
        button.setText(getString(visited ? R.string.place_action_not_visited : R.string.place_action_visited));
        button.setDrawable(ActivityCompat.getDrawable(this, visited ? R.drawable.ic_clear_white_24dp : R.drawable.ic_done_white_24dp));

        findViewById(R.id.your_rating).setVisibility(visited ? View.VISIBLE : View.GONE);
    }

    private void updateSaved(boolean saved) {
        ButtonAction button = (ButtonAction) findViewById(R.id.place_action_save);
        button.setText(getString(saved ? R.string.place_action_saved : R.string.place_action_save));
        button.setDrawable(ActivityCompat.getDrawable(this, saved ? R.drawable.ic_turned_in_white_24dp : R.drawable.ic_turned_in_not_white_24dp));
    }

    private void updateRatingButtons(int myRating) {
        ButtonAction[] buttons = {
            (ButtonAction) findViewById(R.id.rating_awful),
            (ButtonAction) findViewById(R.id.rating_ok),
            (ButtonAction) findViewById(R.id.rating_good),
            (ButtonAction) findViewById(R.id.rating_super),
        };

        for(ButtonAction button : buttons) {
            button.getImageView().clearAnimation();
            button.getTextView().setTypeface(null, Typeface.NORMAL);
        }

        if (myRating > 0) {
            buttons[myRating - 1].startBounceAnimation();
            buttons[myRating - 1].getTextView().setTypeface(null, Typeface.BOLD);
        }
    }

    private void updateAverageRatingText(Float ratingAverage, int ratingCount) {
        ((TextView) findViewById(R.id.avg_rating)).setText(ratingCount > 0 ? getString(R.string.avg_rating, ratingAverage) : getString(R.string.no_rating));
        ((TextView) findViewById(R.id.num_rating)).setText(getString(R.string.num_rating, ratingCount));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.place_action_visited:
                changeVisited(place.getVisit() == null);
                break;

            case R.id.place_action_save:
                changeSaved(!isSaved());
                break;

            case R.id.place_action_share:
                sharePlace();
                break;

            case R.id.place_action_report:
                reportPlace();
                break;

            case R.id.rating_super:
                changeRating(4);
                break;
            case R.id.rating_good:
                changeRating(3);
                break;
            case R.id.rating_ok:
                changeRating(2);
                break;
            case R.id.rating_awful:
                changeRating(1);
                break;
        }
    }

    private void reportPlace() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ Wloczykij.REPORT_EMAIL_ADDRESS });
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Zgłaszanie punktu - " + place.getName());
        User user = Wloczykij.session.loggedOnUser;
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
            "ID: " + place.getId() + "\n" +
            "Nazwa: " + place.getName() + "\n" +
            "Powód:\n" +
            "(proszę wpisać tutaj powód zgłoszenia)\n" +
            "\n" +
            user.getUsername() + "\n" +
            user.getFirstName() + " " + user.getLastName()
        );
        startActivity(Intent.createChooser(emailIntent, getString(R.string.place_action_report)));
    }

    private void sharePlace() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, place.getName());
        sendIntent.putExtra(Intent.EXTRA_TEXT, place.getId());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.place_action_share)));
    }

    private void changeVisited(boolean visited) {
        if ((place.getVisit() != null) == visited) return;

        new AsyncTask<Boolean, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Boolean... params) {
                try {
                    APIManager api = Wloczykij.api;
                    if (params[0]) {
                        Place.Visit visit = api.sendJsonRequest("POST", place.getVisitUrl(), new Place.Visit(), Place.Visit.class);
                        place.setVisit(visit);
                    } else {
                        api.sendRequest("DELETE", place.getVisitUrl(), null);
                        place.setVisit(null);
                    }
                    return true;
                } catch (APIRequestException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (!success) {
                    Toast.makeText(PlaceDetailsActivity.this, R.string.error_occurred, Toast.LENGTH_SHORT).show();
                }
                updateVisitedButton(place.getVisit() != null);
                updateRatingButtons(place.getMyRating());
            }
        }.execute(visited);
    }

    private void changeRating(int rating) {
        if (place.getVisit() == null)
            throw new RuntimeException("Tried to change rating on not visited place");

        new AsyncTask<Integer, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Integer... params) {
                try {
                    Place.Visit visit = place.getVisit();
                    visit.setRating(params[0]);
                    visit = Wloczykij.api.sendJsonRequest("PUT", place.getVisitUrl(), visit, Place.Visit.class);
                    place.setVisit(visit);
                    return true;
                } catch (APIRequestException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (!success) {
                    Toast.makeText(PlaceDetailsActivity.this, R.string.error_occurred, Toast.LENGTH_SHORT).show();
                }
                updateRatingButtons(place.getMyRating());
            }
        }.execute(rating);
    }

    private boolean isSaved() {
        return Wloczykij.session.loggedOnUser.getSavedPlaces().contains(place.getId());
    }

    private void changeSaved(boolean saved) {
        if (saved == isSaved())
            return;

        new AsyncTask<Boolean, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Boolean... params) {
                try {
                    if (params[0]) {
                        Wloczykij.api.sendRequest("PUT", place.getSaveUrl(), RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{}"));
                        Wloczykij.session.loggedOnUser.getSavedPlaces().add(place.getId());
                    } else {
                        Wloczykij.api.sendRequest("DELETE", place.getSaveUrl(), null);
                        Wloczykij.session.loggedOnUser.getSavedPlaces().remove(place.getId());
                    }
                    return true;
                } catch (APIRequestException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (!success) {
                    Toast.makeText(PlaceDetailsActivity.this, R.string.error_occurred, Toast.LENGTH_SHORT).show();
                    return;
                }

                updateSaved(isSaved());
            }
        }.execute(saved);
    }
}
