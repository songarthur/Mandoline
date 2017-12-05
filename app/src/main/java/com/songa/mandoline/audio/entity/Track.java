package com.songa.mandoline.audio.entity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Track
{
    private long trackId;
    private @NonNull String trackTitle;
    private long artistId;
    private @Nullable String artistName;
    private long albumId;
    private @Nullable String albumName;
    private long trackNumber;
    private long trackDuration;
    private long trackYear;
    private @Nullable String trackMimeType;
    private @Nullable String trackUri;

    private @Nullable String coverArtUri;

    public Track(long trackId, @NonNull String trackTitle,
                 long artistId, @Nullable String artistName,
                 long albumId, @Nullable String albumName,
                 long trackNumber,
                 long trackDuration,
                 long trackYear,
                 @Nullable String trackMimeType,
                 @Nullable String trackUri)
    {
        this.trackId = trackId;
        this.trackTitle = trackTitle;
        this.artistId = artistId;
        this.artistName = artistName;
        this.albumId = albumId;
        this.albumName = albumName;
        this.trackNumber = trackNumber;
        this.trackDuration = trackDuration;
        this.trackYear = trackYear;
        this.trackMimeType = trackMimeType;
        this.trackUri = trackUri;
        this.coverArtUri = null;
    }

    public long getTrackId() {
        return trackId;
    }

    @NonNull
    public String getTrackTitle() {
        return trackTitle;
    }

    public long getArtistId() {
        return artistId;
    }

    @Nullable
    public String getArtistName() {
        return artistName;
    }

    public long getAlbumId() {
        return albumId;
    }

    @Nullable
    public String getAlbumName() {
        return albumName;
    }

    public long getTrackNumber() {
        return trackNumber;
    }

    public long getTrackDuration() {
        return trackDuration;
    }

    public long getTrackYear() {
        return trackYear;
    }

    @Nullable
    public String getTrackMimeType() {
        return trackMimeType;
    }

    @Nullable
    public String getTrackUri() {
        return trackUri;
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
        return obj!=null && (obj instanceof Track) && trackId==((Track)obj).trackId;
    }

    @Override
    public int hashCode()
    {
        return Long.valueOf(trackId).hashCode();
    }
}
