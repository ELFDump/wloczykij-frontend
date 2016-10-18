package pl.elfdump.wloczykij.network.api.models;

import android.support.annotation.Nullable;

import com.squareup.moshi.Json;

public abstract class APIModel {
    @Json(name = "url")
    @Nullable
    private String resourceUrl;

    @Nullable
    public String getResourceUrl() {
        return resourceUrl;
    }
}
