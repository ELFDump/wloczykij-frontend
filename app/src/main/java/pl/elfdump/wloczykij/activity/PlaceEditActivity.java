package pl.elfdump.wloczykij.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.*;

import com.klinker.android.sliding.SlidingActivity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import me.gujun.android.taggroup.TagGroup;
import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.network.api.APIManager;
import pl.elfdump.wloczykij.network.api.APIRequestException;
import pl.elfdump.wloczykij.network.api.models.Place;
import pl.elfdump.wloczykij.utils.Util;

public class PlaceEditActivity extends SlidingActivity implements View.OnClickListener {
    /**
     * Place instance edited by this activity
     */
    private Place place;
    /**
     * Target file for photo image (only if choosing from camera)
     */
    private File photoFile;
    /**
     * Uri of selected photo (both for camera and gallery)
     */
    private Uri photoUri;

    private static final int RC_SELECT_IMAGE = 1888;

    @Override
    public void init(Bundle state) {
        setContent(R.layout.activity_place_edit);
        place = (Place) getIntent().getSerializableExtra("place");

        disableHeader();
        enableFullscreen();

        findViewById(R.id.add_place_button).setOnClickListener(this);
        findViewById(R.id.button_add_image).setOnClickListener(this);

        if (state != null) {
            String photoFilePath = state.getString("photoFilePath");
            photoFile = photoFilePath == null ? null : new File(photoFilePath);

            photoUri = state.getParcelable("photoUri");
            if (photoUri != null) {
                loadThumbnail(photoUri);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state){
        if (photoFile != null) {
            state.putString("photoFilePath", photoFile.getPath());
        }

        if (photoUri != null) {
            state.putParcelable("photoUri", photoUri);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.add_place_button:
                String name = ((EditText) findViewById(R.id.place_name)).getText().toString();
                String errorMessage = null;

                if (photoUri == null) {
                    errorMessage = getString(R.string.error_photo_required);
                } else if (name.isEmpty()) {
                    errorMessage = getString(R.string.error_name_required);
                }

                if (errorMessage != null) {
                    Toast.makeText(PlaceEditActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    return;
                }

                findViewById(R.id.add_place_button).setEnabled(false);

                place.setName(name);
                place.setTags(((TagGroup) findViewById(R.id.tag_group)).getTags());

                savePlace();
                break;
            case R.id.button_add_image:
                photoFile = new File(getExternalCacheDir(), "photo" + System.currentTimeMillis() + ".jpg");
                List<Intent> cameraIntents = new ArrayList<>();
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                for (ResolveInfo res : getPackageManager().queryIntentActivities(captureIntent, 0)) {
                    Intent intent = new Intent(captureIntent);
                    intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    intent.setPackage(res.activityInfo.packageName);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    cameraIntents.add(intent);
                }

                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                Intent chooserIntent = Intent.createChooser(galleryIntent, "Select image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

                startActivityForResult(chooserIntent, RC_SELECT_IMAGE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data == null || data.getData() == null) {
                    // Camera
                    photoUri = Uri.fromFile(photoFile);
                } else {
                    // Gallery
                    photoFile = null;
                    photoUri = data.getData();
                }

                Log.d(Wloczykij.TAG, "Selected image URI = " + photoUri.toString());
                loadThumbnail(photoUri);
            } else {
                photoFile = null;
            }
        }
    }

    private void loadThumbnail(Uri uri){
        new AsyncTask<Uri, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Uri... paths) {
                try {
                    ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(paths[0], "r");
                    assert parcelFileDescriptor != null;
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    parcelFileDescriptor.close();
                    return bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                ((ImageView) findViewById(R.id.thumb_add_image)).setImageBitmap(bitmap);
                findViewById(R.id.button_add_image).setAlpha(0);
            }
        }.execute(uri);
    }

    private void savePlace() {
        new AsyncTask<Place, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Place... params) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(photoUri);
                    assert inputStream != null;
                    byte[] fileData = Util.readStreamToByteArray(inputStream);
                    inputStream.close();

                    String fileMimeType, fileName;
                    switch (photoUri.getScheme()) {
                        case "content":
                            fileMimeType = getContentResolver().getType(photoUri);
                            fileName =  "photo." + MimeTypeMap.getSingleton().getExtensionFromMimeType(fileMimeType);
                            break;

                        case "file":
                            fileName = photoUri.getLastPathSegment();
                            fileMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                                MimeTypeMap.getFileExtensionFromUrl(fileName));
                            break;

                        default:
                            // Shouldn't happen
                            throw new IOException("Unknown URI scheme");
                    }

                    APIManager api = Wloczykij.api;
                    place = api.cache(Place.class).save(place);
                    api.uploadImage(place.getPhotoUploadUrl(), fileMimeType, fileName, fileData);

                    if (photoFile != null) {
                        photoFile.delete();
                        photoFile = null;
                    }

                    return true;
                } catch (APIRequestException | IOException e) {
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
