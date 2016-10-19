package pl.elfdump.wloczykij.network.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class APIModelCache<T extends APIModel> {
    private APIModelManager<T> manager;
    private Map<String, T> cache = new HashMap<>();

    public APIModelCache(APIModelManager<T> manager) {
        this.manager = manager;
    }

    public void update() throws APIRequestException {
        // TODO: incremental updates?
        cache.clear();
        for (T item : manager.getAll()) {
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
