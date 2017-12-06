package com.songa.mandoline.artwork.lastfm.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Album Info from LastFM. <br>
 * See https://www.last.fm/api/show/album.getInfo
 */
public class LastFmAlbumInfo
{
    @SerializedName("mbid")
    private String mbid;

    @SerializedName("name")
    private String albumName;

    @SerializedName("artist")
    private String artistName;

    @SerializedName("image")
    private List<LastFmImage> images;

    public String getMbid() {
        return mbid;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public List<LastFmImage> getImages() {
        return images;
    }
}
