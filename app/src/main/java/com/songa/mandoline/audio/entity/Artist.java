package com.songa.mandoline.audio.entity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Artist
{
    private long artistId;
    private @NonNull String artistName;

    private @Nullable String coverArtUri;

    public Artist(long artistId, @NonNull String artistName) {
        this.artistId = artistId;
        this.artistName = artistName;
        this.coverArtUri = null;
    }

    public long getArtistId() {
        return artistId;
    }

    @NonNull
    public String getArtistName() {
        return artistName;
    }

    @Nullable
    public String getCoverArtUri() {
        return coverArtUri;
    }

    public void setCoverArtUri(@Nullable String coverArtUri) {
        this.coverArtUri = coverArtUri;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj!=null && (obj instanceof Artist) && artistId==((Artist)obj).artistId;
    }

    @Override
    public int hashCode()
    {
        return Long.valueOf(artistId).hashCode();
    }
}
