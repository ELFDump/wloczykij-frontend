package pl.elfdump.wloczykij.network.api;

import com.squareup.moshi.Types;

import java.util.List;

import pl.elfdump.wloczykij.network.api.models.APIModel;

public class APIModelManager<T extends APIModel> {
    private APIManager api;
    private Class<T> type;

    /* package-access */ APIModelManager(APIManager api, Class<T> type) {
        this.api = api;
        this.type = type;
    }

    private String getEndpointName() {
        return type.getSimpleName().toLowerCase() + "s";
    }

    private String getEndpointUrl() throws APIRequestException {
        return api.getEndpointUrl(getEndpointName());
    }

    /**
     * List all objects of given type from database
     * @return List of all objects
     */
    public List<T> getAll() throws APIRequestException {
        return api.sendJsonRequest("GET", getEndpointUrl(), null, Types.newParameterizedType(List.class, type));
    }

    /**
     * Get given object from the server
     * @param url URL of resource to retrieve
     * @return Object from server
     */
    public T get(String url) throws APIRequestException {
        if (!url.startsWith(getEndpointUrl())) {
            throw new IllegalArgumentException(String.format("Resource %s is not of type '%s'", url, getEndpointName()));
        }
        return api.sendJsonRequest("GET", url, null, type);
    }

    /**
     * Get updated version of given object from the server
     * @param object Object to get updated version of
     * @return Updated version of the object
     */
    public T get(T object) throws APIRequestException {
        if (object.getResourceUrl() == null) {
            throw new IllegalArgumentException("This object is not saved in the backend");
        }
        return get(object.getResourceUrl());
    }

    /**
     * Syntax sugar for creating new class of type managed by this APIModelManager
     * @return New class of managed type
     */
    public T create() {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Save the object to server's database<br>
     * <br>
     * After saving the object, the server returns a new version with read-only and implicit parameters added.
     * You should use the returned instance instead of the one passed into this function.<br>
     * <br>
     * Example usage:
     * <code><pre>{@code
     *     APIModelManager<Place> placeManager = api.manager(Place.class);
     *     Place place = placeManager.create();
     *     place.setPosition(1, 2);
     *     place.setName("abcd");
     *     place = placeManager.save(place);
     *     someStorageOfPlaces.add(place);
     * }</pre></code>
     *
     * @param object Object to save
     * @return New object from the server, see {@link #get(T)}
     */
    public T save(T object) throws APIRequestException {
        // TODO: Detect changes and use PATCH instead to save on network traffic
        if (object.getResourceUrl() == null) {
            return api.sendJsonRequest("POST", getEndpointUrl(), object, type);
        } else {
            return api.sendJsonRequest("PUT", object.getResourceUrl(), object, type);
        }
    }
}
