package com.songa.mandoline.artwork;

import android.support.annotation.NonNull;

public class ArtworkResult
{
    private @NonNull String url;
    private boolean isLocal;

    public ArtworkResult(@NonNull String url, boolean isLocal)
    {
        this.url = url;
        this.isLocal = isLocal;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    public boolean isLocal() {
        return isLocal;
    }
}
