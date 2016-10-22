package pl.elfdump.wloczykij.network.api;

import android.support.annotation.Nullable;

import com.squareup.moshi.Json;

import java.io.Serializable;

public abstract class APIModel implements Serializable {
    @Json(name = "url")
    @Nullable
    private String resourceUrl;

    @Nullable
    public final String getResourceUrl() {
        return resourceUrl;
    }

    @Nullable
    public String getId() {
        return getResourceUrl();
    }
}
