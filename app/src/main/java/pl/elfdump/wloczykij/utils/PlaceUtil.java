package pl.elfdump.wloczykij.utils;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.network.api.models.Place;
import pl.elfdump.wloczykij.network.api.models.Tag;

import java.util.*;

public class PlaceUtil {

    private static final int DEFAULT_ICON = R.mipmap.ic_launcher;

    private static final Map<String, Integer> icons = new HashMap<String, Integer>(){{
        put("Jedzenie", R.drawable.ic_face_white_24dp);
        put("Zabytek", R.drawable.ic_mail_grey600_24dp);
    }};

    public static List<Place> filterPlaces(Collection<Place> places, String[] tags){
        List<Place> filtered = new ArrayList<>();

        for(Place p : places){
            // Collections.disjoint checks if there are no common values in lists
            // So we invert the result
            if(!Collections.disjoint(Arrays.asList(p.getTags()), Arrays.asList(tags))){
                filtered.add(p);
            }
        }

        return filtered;
    }

    public static BitmapDescriptor getMatchingIcon(Place place){
        List<String> tags = place.getTags();
        Collection<Tag> availableTags = Wloczykij.api.cache(Tag.class).getAll();

        Tag categoryTag = null;
        Tag subcategoryTag = null;

        for (Tag tag : availableTags) {
            if(tags.contains(tag.getName())){
                if(icons.containsKey(tag.getName())){
                    if(tag.getParent() == null){
                        categoryTag = tag;
                    }else{
                        subcategoryTag = tag;
                    }
                }else if(tag.getParent() != null){
                    categoryTag = findParentWithIcon(tag);
                }
            }
        }

        int icon = DEFAULT_ICON;

        if(subcategoryTag != null){
            icon = icons.get(subcategoryTag.getName());
        }else if(categoryTag != null){
            icon = icons.get(categoryTag.getName());
        }

        return BitmapDescriptorFactory.fromResource(icon);
    }

    private static Tag findParentWithIcon(Tag current){
        while(current.getParent() != null){
            current = Wloczykij.api.cache(Tag.class).get(current.getParent());

            if(current == null){
                break;
            }

            if(icons.containsKey(current.getName())){
                return current;
            }
        }

        return null;
    }

}
