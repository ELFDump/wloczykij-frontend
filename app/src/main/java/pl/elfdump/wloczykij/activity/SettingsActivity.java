package pl.elfdump.wloczykij.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.klinker.android.sliding.SlidingActivity;

import java.util.Arrays;
import java.util.List;

import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.Session;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.network.api.APIRequestException;
import pl.elfdump.wloczykij.network.api.models.User;
import pl.elfdump.wloczykij.ui.TagsSelectorController;

public class SettingsActivity extends SlidingActivity implements View.OnClickListener {

    private TagsSelectorController tagsSelectorController;

    @Override
    public void init(Bundle savedInstanceState) {
        disableHeader();
        enableFullscreen();

        setContent(R.layout.activity_settings);

        findViewById(R.id.save).setOnClickListener(this);

        ListView listView = (ListView) findViewById(R.id.tag_selector_buttons_row);

        tagsSelectorController = new TagsSelectorController(listView, Wloczykij.session.loggedOnUser.getFollowedTags());
        tagsSelectorController.loadData();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.save:
                save();
                break;
        }
    }

    private void save() {
        List<String> tagsList = tagsSelectorController.getSelectedTags();
        String[] tags = tagsList.toArray(new String[tagsList.size()]);
        new AsyncTask<String[], Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                findViewById(R.id.save).setEnabled(false);
            }

            @Override
            protected Boolean doInBackground(String[]... params) {
                try {
                    Wloczykij.session.loggedOnUser.setFollowedTags(params[0]);
                    Wloczykij.api.manager(User.class).save(Wloczykij.session.loggedOnUser);
                    return true;
                } catch (APIRequestException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (!success) {
                    findViewById(R.id.save).setEnabled(true);
                    Toast.makeText(SettingsActivity.this, R.string.settings_save_error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, R.string.saved, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }.execute(tags);
    }
}
