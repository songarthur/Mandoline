package com.songa.mandoline.artwork.lastfm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * LastFm API base class. Responsible for initializing resources necessary for it to work as an
 * {@link com.songa.mandoline.artwork.ArtworkProvider}.<br>
 *
 * Initializes the HTTP client, the API, and the image loader.
 */
public class LastFm
{
    private static final String LAST_FM_API_ROOT = "https://ws.audioscrobbler.com/";
    private static final String LAST_FM_API_KEY = "1075not5629a6016real293api4107key7395";

    private static final int DISK_CACHE_SIZE = 128 * 1024 * 1024; // 128mb

    private static OkHttpClient lastFmHttpClient = null;
    private static LastFmApi lastFmApi = null;
    private static Picasso picasso = null;

    /**
     * Initializes all the resources used by {@link LastFmArtworkProvider}
     * @param context
     */
    public static void init(@NonNull Context context)
    {
        lastFmHttpClient = buildHttpClient(context);
        lastFmApi = buildApi(lastFmHttpClient);
        picasso = buildImageLoader(context, lastFmHttpClient);
    }

    /**
     * Builds an http client for the LastFM API.
     * The client will cache data for 30 days, and will not make a network call if it can avoid it.
     * The client also automatically sets up the api key for each request.
     *
     * @param context
     * @return
     */
    private static OkHttpClient buildHttpClient(@NonNull Context context)
    {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                HttpUrl newUrl = chain.request().url()
                        .newBuilder()
                        .addQueryParameter("api_key", LAST_FM_API_KEY)
                        .build();

                Request newRequest = chain.request()
                        .newBuilder()
                        .url(newUrl)
                        .build();

                return chain.proceed(newRequest);
            }
        });

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                CacheControl.Builder cachePolicy = new CacheControl.Builder()
                        .maxAge(30, TimeUnit.DAYS)
                        .maxStale(30, TimeUnit.DAYS)
                        .onlyIfCached();

                Request cacheRequest = chain.request()
                        .newBuilder()
                        .cacheControl(cachePolicy.build())
                        .build();

                Response response = chain.proceed(cacheRequest);
                if (response.isSuccessful()) {
                    //Log.w("CACHE", "Cached request " + (response.cacheResponse()!=null) + " - " + (response.networkResponse()!=null));
                    return response;
                } else {
                    response.close();
                }

                response = chain.proceed(chain.request());
                //Log.w("CACHE", "Network request " + response.cacheControl().maxAgeSeconds());
                return response;
            }
        });

        Cache cache = new Cache(context.getCacheDir(), DISK_CACHE_SIZE);
        httpClient.cache(cache);

        return httpClient.build();
    }

    /**
     * Builds a LastFM API from Retrofit.
     *
     * @param httpClient
     * @return
     */
    private static LastFmApi buildApi(@NonNull OkHttpClient httpClient)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LAST_FM_API_ROOT)
                .client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(LastFmApi.class);
    }

    /**
     * Builds an ImageLoader from Picasso.
     *
     * @param context
     * @param client
     * @return
     */
    private static Picasso buildImageLoader(@NonNull Context context, @NonNull OkHttpClient client)
    {
        return new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(client))
                .build();
    }

    /**
     * @return Return the LastFM HTTP client.
     */
    public static OkHttpClient getHttpClient()
    {
        return lastFmHttpClient;
    }

    /**
     * @return Returns the LastFM API.
     */
    public static LastFmApi getApi()
    {
        return lastFmApi;
    }

    /**
     * @return Returns the LastFM Image Loader.
     */
    public static Picasso getImageLoader()
    {
        return picasso;
    }
}
