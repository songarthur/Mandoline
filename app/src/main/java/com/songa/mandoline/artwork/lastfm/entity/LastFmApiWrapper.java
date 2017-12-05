package com.songa.mandoline.artwork.lastfm.entity;

import com.google.gson.annotations.SerializedName;

public class LastFmApiWrapper
{
    @SerializedName("artist")
    private LastFmArtistInfo artistInfo;

    @SerializedName("album")
    private LastFmAlbumInfo albumInfo;

    public LastFmArtistInfo getArtistInfo() {
        return artistInfo;
    }

    public LastFmAlbumInfo getAlbumInfo() {
        return albumInfo;
    }
}
