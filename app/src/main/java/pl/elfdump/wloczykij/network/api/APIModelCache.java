package pl.elfdump.wloczykij.network.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.elfdump.wloczykij.network.api.models.Place;

public class APIModelCache<T extends APIModel> {
    private APIModelManager<T> manager;
    private final Map<String, T> cache = new HashMap<>();

    public APIModelCache(APIModelManager<T> manager) {
        this.manager = manager;
    }

    public void update() throws APIRequestException {
        // TODO: incremental updates?
        Collection<T> allItems = manager.getAll();
        cache.clear();
        for (T item : allItems) {
            cache.put(item.getResourceUrl(), item);
        }
    }

    public T save(T item) throws APIRequestException {
        item = manager.save(item);
        cache.put(item.getResourceUrl(), item);
        return item;
    }

    public Collection<T> getAll() {
        return cache.values();
    }

    public T get(String resourceUrl) {
        return cache.get(resourceUrl);
    }

    public T get(T object) {
        if (object == null) return null;
        return cache.get(object.getResourceUrl());
    }
}
