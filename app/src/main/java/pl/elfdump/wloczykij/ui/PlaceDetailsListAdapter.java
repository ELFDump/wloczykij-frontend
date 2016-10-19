package pl.elfdump.wloczykij.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import pl.elfdump.wloczykij.R;

import java.util.ArrayList;

public class PlaceDetailsListAdapter extends ArrayAdapter<PlaceDetailsItem> {

    private final Context context;
    private final ArrayList<PlaceDetailsItem> modelsArrayList;

    public PlaceDetailsListAdapter(Context context, ArrayList<PlaceDetailsItem> modelsArrayList) {

        super(context, R.layout.place_details_item, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.place_details_item, parent, false);

        ImageView imgView = (ImageView) rowView.findViewById(R.id.item_icon);
        TextView titleView = (TextView) rowView.findViewById(R.id.item_title);

        imgView.setImageResource(modelsArrayList.get(position).getIcon());
        titleView.setText(modelsArrayList.get(position).getTitle());

        return rowView;
    }

}
