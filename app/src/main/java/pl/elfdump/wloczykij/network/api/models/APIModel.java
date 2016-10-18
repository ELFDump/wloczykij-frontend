package pl.elfdump.wloczykij.network.api.models;

import android.support.annotation.Nullable;

import com.squareup.moshi.Json;

import java.io.Serializable;

public abstract class APIModel implements Serializable {
    @Json(name = "url")
    @Nullable
    private String resourceUrl;

    @Nullable
    public String getResourceUrl() {
        return resourceUrl;
    }
}
