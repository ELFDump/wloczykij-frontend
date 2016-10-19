package pl.elfdump.wloczykij.activity.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pl.elfdump.wloczykij.R;

public class PlaceDetailsHeaderButtonFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.place_details_header_button, container, false);

        /*Button b = (Button) v.findViewById(R.id.imageButton);
        b.setOnClickListener(this);*/
        return v;
    }

}
