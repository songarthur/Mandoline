package com.songa.mandoline.audio.entity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Album
{
    private long albumId;
    private @NonNull String albumName;
    private long artistId;
    private @Nullable String artistName;

    private @Nullable String coverArtUri;

    public Album(long albumId, @NonNull String albumName, long artistId, String artistName, String coverArtUri) {
        this.albumId = albumId;
        this.albumName = albumName;
        this.artistId = artistId;
        this.artistName = artistName;
        this.coverArtUri = coverArtUri;
    }

    public long getAlbumId() {
        return albumId;
    }

    @NonNull
    public String getAlbumName() {
        return albumName;
    }

    public long getArtistId() {
        return artistId;
    }

    @Nullable
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
        return obj!=null && (obj instanceof Album) && albumId==((Album)obj).albumId;
    }

    @Override
    public int hashCode()
    {
        return Long.valueOf(albumId).hashCode();
    }
}
