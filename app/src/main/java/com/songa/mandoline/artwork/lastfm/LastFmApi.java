package com.songa.mandoline.artwork.lastfm;

import com.songa.mandoline.artwork.lastfm.entity.LastFmApiWrapper;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * LastFM API for Retrofit.
 */
public interface LastFmApi
{
    /**
     * Fetches an artist's info from LastFM.
     *
     * @param artistName the artist's name
     * @return a wrapper for the response. Must check for null in case the api has nothing to return.
     */
    @GET("2.0/?method=artist.getinfo&format=json")
    Call<LastFmApiWrapper> artistInfo(@Query("artist") String artistName);

    /**
     * Fetches an album's info from LastFM.
     *
     * @param artistName the artist's name
     * @param albumName the album's name
     * @return a wrapper for the response. Must check for null in case the api has nothing to return.
     */
    @GET("2.0/?method=album.getinfo&format=json")
    Call<LastFmApiWrapper> albumInfo(@Query("artist") String artistName, @Query("album") String albumName);

}
