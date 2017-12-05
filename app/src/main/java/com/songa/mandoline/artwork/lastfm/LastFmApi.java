package com.songa.mandoline.artwork.lastfm;

import com.songa.mandoline.artwork.lastfm.entity.LastFmApiWrapper;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LastFmApi
{
    @GET("2.0/?method=artist.getinfo&format=json")
    Single<LastFmApiWrapper> artistInfoAsync(@Query("artist") String artistName);

    @GET("2.0/?method=artist.getinfo&format=json")
    Call<LastFmApiWrapper> artistInfo(@Query("artist") String artistName);

    @GET("2.0/?method=album.getinfo&format=json")
    Call<LastFmApiWrapper> albumInfo(@Query("artist") String artistName, @Query("album") String albumName);

}
