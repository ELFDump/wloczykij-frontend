package pl.elfdump.wloczykij.ui;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.network.api.models.Tag;
import pl.elfdump.wloczykij.utils.PlaceUtil;
import pl.elfdump.wloczykij.utils.Util;

/**
 * Handles tag selector list (clicks, selections) and generates list of tags
 */
public class TagsSelectorController {

    private ListView listView;
    private List<PlaceDetailsItem> data;
    private List<String> previousFilter;

    private PlaceDetailsListAdapter adapter;

    public interface ClickListener {
        void onListItemClick(int index);
    }

    private ClickListener clickListener;

    public TagsSelectorController(final ListView listView, List<String> filter) {
        this.listView = listView;
        this.previousFilter = filter;

        // Interaction with checkbox is disabled to get selection based on element index rather than checkbox id
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.item_checkBox);
                checkBox.setChecked(!checkBox.isChecked());

                data.get(i).setChecked(checkBox.isChecked());

                if (clickListener != null) {
                    clickListener.onListItemClick(i);
                }
            }
        });

        // We have to cancel touch event on SlidingActivity when touching listView
        listView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    public void loadData(){
        loadData(generateData());
    }

    public void loadData(List<PlaceDetailsItem> data){
        this.data = data;

        adapter = new PlaceDetailsListAdapter(listView.getContext(), data);
        listView.setAdapter(adapter);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public List<PlaceDetailsItem> getData(){
        return data;
    }

    public ArrayList<String> getSelectedTags(){
        ArrayList<String> selectedTags = new ArrayList<>();

        for(PlaceDetailsItem item : data){
            if(item.isChecked()){
                selectedTags.add(item.getId());
            }
        }

        return selectedTags;
    }

    // Apply previous selections after ListView scrolling
    public void applySelections(List<String> selections){
        for(int index = 0; index < data.size(); index++){
            if(selections.contains(data.get(index).getId())){
                data.get(index).setChecked(true);
                ((CheckBox) (Util.getViewByPosition(index, listView).findViewById(R.id.item_checkBox))).setChecked(true);
            }
        }
    }

    private List<PlaceDetailsItem> generateData(){

        Collection<Tag> availableTags = Wloczykij.api.cache(Tag.class).getAll();
        Map<String, TagsGroupItem> tagsGroupItems = new HashMap<>();

        for(Tag tag : availableTags){
            if(tag.getParent() == null){
                if(!tagsGroupItems.containsKey(tag.getName())){
                    tagsGroupItems.put(tag.getName(), new TagsGroupItem(tag.getName(), new ArrayList<PlaceDetailsItem>()));
                }
            }else{
                if(!tagsGroupItems.containsKey(tag.getParent())){
                    String parent = PlaceUtil.findTopLevel(tag).getName();
                    if(!tagsGroupItems.containsKey(parent)){
                        tagsGroupItems.put(parent, new TagsGroupItem(tag.getParent(), new ArrayList<PlaceDetailsItem>()));
                    }

                    tagsGroupItems.get(parent).getItems().add(createPlaceDetailsItem(tag.getName()));
                }else{
                    tagsGroupItems.get(tag.getParent()).getItems().add(createPlaceDetailsItem(tag.getName()));
                }
            }
        }

        List<PlaceDetailsItem> sortedList = new ArrayList<>();

        for (Map.Entry<String, TagsGroupItem> entry : tagsGroupItems.entrySet()) {
            PlaceDetailsItem group = createPlaceDetailsItem(entry.getKey());
            sortedList.add(group);
            if(previousFilter == null || previousFilter.contains(group.getId())){
                group.setChecked(true);
            }

            for(PlaceDetailsItem item : entry.getValue().getItems()){
                item.setType(PlaceDetailsItem.ITEM_CHILD);
                group.childs.add(item);
                sortedList.add(item);
                if(previousFilter == null || previousFilter.contains(item.getId())){
                    item.setChecked(true);
                }
            }
        }

        return new ArrayList<>(sortedList);
    }

    private PlaceDetailsItem createPlaceDetailsItem(String name){
        int itemIcon;

        if(PlaceUtil.icons.containsKey(name)){
            itemIcon = PlaceUtil.icons.get(name);
        }else{
            Tag parent = PlaceUtil.findParentWithIcon(Wloczykij.api.cache(Tag.class).get(name));
            if(parent != null){
                itemIcon = PlaceUtil.icons.get(parent.getName());
            }else{
                itemIcon = PlaceUtil.DEFAULT_ICON;
            }
        }

        return new PlaceDetailsItem(itemIcon, name, true);
    }

}
