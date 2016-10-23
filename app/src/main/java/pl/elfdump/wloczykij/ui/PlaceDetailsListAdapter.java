package pl.elfdump.wloczykij.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.w3c.dom.Text;
import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.utils.Util;

import java.util.ArrayList;

public class PlaceDetailsListAdapter extends ArrayAdapter<PlaceDetailsItem> {

    private final Context context;
    private final ArrayList<PlaceDetailsItem> modelsArrayList;
    private boolean[] checked;

    public PlaceDetailsListAdapter(Context context, ArrayList<PlaceDetailsItem> modelsArrayList) {
        super(context, R.layout.place_details_item, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
        this.checked = new boolean[modelsArrayList.size()];
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(R.layout.place_details_item, parent, false);
        }

        TextView titleView = (TextView) rowView.findViewById(R.id.item_title);
        CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.item_checkBox);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
                PlaceDetailsListAdapter.this.checked[position] = checked;
            }
        });

        Drawable img = ContextCompat.getDrawable(context, modelsArrayList.get(position).getIcon());
        img.setBounds( 0, 0, 100, 100 );
        titleView.setCompoundDrawables(img, null, null, null);

        titleView.setText(modelsArrayList.get(position).getTitle() + " [" + String.valueOf(position) + "]"); // TODO: ID for debugging only
        checkBox.setVisibility((modelsArrayList.get(position).hasCheckBox() ? View.VISIBLE : View.GONE));
        checkBox.setChecked(modelsArrayList.get(position).isChecked());

        if(modelsArrayList.get(position).getType() == PlaceDetailsItem.ITEM_CHILD){ //
            rowView.setPadding(100, 0, 0, 0);
        }else{
            rowView.setPadding(0, 0, 0, 0);
        }

        return rowView;
    }

}
