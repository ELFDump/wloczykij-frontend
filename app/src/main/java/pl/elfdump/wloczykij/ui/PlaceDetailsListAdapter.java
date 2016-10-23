package pl.elfdump.wloczykij.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

    // Stores checkboxes state
    private boolean[] isChecked;

    /**
        We are using ViewHolder store layouts to reuse them
        on ListView updates which are happening during scrolling long lists
        Increases performance a lot
     **/
    private static class ViewHolder {
        private TextView titleView;
        private CheckBox checkBox;
    }

    public PlaceDetailsListAdapter(Context context, ArrayList<PlaceDetailsItem> modelsArrayList) {
        super(context, R.layout.place_details_item, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
        this.isChecked = new boolean[getCount()];
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View rowView = convertView;
        final int realPosition = ((ListView) parent).getFirstVisiblePosition() + position;

        if(rowView == null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(R.layout.place_details_item, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.titleView = (TextView) rowView.findViewById(R.id.item_title);
            viewHolder.checkBox = (CheckBox) rowView.findViewById(R.id.item_checkBox);

            // Checked status is changing on list scrolling
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
                    isChecked[realPosition] = checked; // .. so after user select checkbox we store state
                }
            });

            viewHolder.checkBox.setChecked(isChecked[position]);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        TextView titleView = holder.titleView;
        CheckBox checkBox = holder.checkBox;
        checkBox.setChecked(isChecked[realPosition]);

        Drawable img = ContextCompat.getDrawable(context, modelsArrayList.get(position).getIcon());
        img.setBounds( 0, 0, 100, 100 );
        titleView.setCompoundDrawables(img, null, null, null);

        titleView.setText(modelsArrayList.get(position).getTitle());
        checkBox.setVisibility((modelsArrayList.get(position).hasCheckBox() ? View.VISIBLE : View.GONE));

        if(modelsArrayList.get(position).getType() == PlaceDetailsItem.ITEM_CHILD){ //
            rowView.setPadding(100, 0, 0, 0);
        }else{
            rowView.setPadding(0, 0, 0, 0);
        }

        return rowView;
    }

}
