package com.songa.mandoline.artwork.lastfm.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Image from the LastFM API
 */
public class LastFmImage
{
    @SerializedName("#text")
    private String url;

    @SerializedName("size")
    private String size;

    public String getUrl() {
        return url;
    }

    public String getSize() {
        return size;
    }
}