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
        put("Jedzenie", R.drawable.ic_restaurant);
        put("Kebab", drawable.ic_kebab);
        put("Bar", R.drawable.ic_bar);
        put("Pizzeria", R.drawable.ic_pizzeria);
        put("Sushi", R.drawable.ic_sushi);
        put("Kawiarnia", R.drawable.ic_cafe);
        put("Lody", R.drawable.ic_icecream);
        put("Historia", R.drawable.ic_history);
        put("Muzeum", R.drawable.ic_museum);
        put("Skansen", R.drawable.ic_openairmuseum);
        put("Pomnik/rzeźba", R.drawable.ic_monument);
        put("Zabytek", R.drawable.ic_antique);
        put("Kościół", R.drawable.ic_church);
        put("Cmentarz", R.drawable.ic_cemetery);
        put("Zamek", R.drawable.ic_castle);
        put("Sport", R.drawable.ic_sport);
        put("Stadion", R.drawable.ic_stadium);
        put("Boisko", R.drawable.ic_field);
        put("Piłka nożna", R.drawable.ic_soccer);
        put("Piłka ręczna", R.drawable.ic_handball);
        put("Piłka koszykowa", R.drawable.ic_basketball);
        put("Piłka siatkowa", R.drawable.ic_volleyball);
        put("Siłownia", R.drawable.ic_gym);
        put("Basen/pływalnia", R.drawable.ic_swimming1);
        put("Kąpielisko", R.drawable.ic_swimming2);
        put("Lodowisko", R.drawable.ic_iceskating);
        put("Kort tenisowy", R.drawable.ic_tennis);
        put("Ścianka wspinaczkowa", R.drawable.ic_climbing);
        put("Park linowy", R.drawable.ic_ropespark);
        put("Park trampolin", R.drawable.ic_sponge);
        put("Przystań kajakowa", R.drawable.ic_kayaking);
        put("Strzelnica", R.drawable.ic_shooting);
        put("Bilard", R.drawable.ic_billiard);
        put("Kręgielnia", R.drawable.ic_bowling);
        put("Tenis stołowy", R.drawable.ic_tabletennis);
        put("Rozrywka", R.drawable.ic_entertainment);
        put("Teatr", R.drawable.ic_theater);
        put("Film", R.drawable.ic_cinema);
        put("Muzyka", R.drawable.ic_music);
        put("Klub", R.drawable.ic_dancinghall);
        put("Park rozrywki", R.drawable.ic_themepark);
        put("Paintball", R.drawable.ic_paintball);
        put("Park", R.drawable.ic_park);
        put("Przyroda", R.drawable.ic_nature);
        put("Zwierzęta", R.drawable.ic_animal);
        put("Park dinozaurów", R.drawable.ic_dinopark);
        put("Rośliny", R.drawable.ic_flowers);
        put("Palmiarnia", R.drawable.ic_palmtree);
        put("Nauka", R.drawable.ic_science);
        put("Planetarium", R.drawable.ic_planetarium);
        put("Sztuka", R.drawable.ic_art);
        put("Zakupy", R.drawable.ic_shopping);

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
