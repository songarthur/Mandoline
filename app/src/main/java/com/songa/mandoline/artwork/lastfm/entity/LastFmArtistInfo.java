package com.songa.mandoline.artwork.lastfm.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastFmArtistInfo
{
    @SerializedName("mbid")
    private String mbid;

    @SerializedName("name")
    private String name;

    @SerializedName("image")
    private List<LastFmImage> images;

    @SerializedName("bio")
    private Bio bio;

    public String getMbid() {
        return mbid;
    }

    public String getName() {
        return name;
    }

    public List<LastFmImage> getImages() {
        return images;
    }

    public Bio getBio() {
        return bio;
    }

    public static class Bio
    {
        @SerializedName("summary")
        private String summary;

        @SerializedName("content")
        private String content;

        public String getSummary() {
            return summary;
        }

        public String getContent() {
            return content;
        }
    }
}
