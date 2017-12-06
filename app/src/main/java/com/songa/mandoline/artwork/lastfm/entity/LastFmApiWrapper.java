package com.songa.mandoline.artwork.lastfm.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Generic response from the LastFM API. <br>
 * The value of each field depends on the kind of data you are requesting. 
 */
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
