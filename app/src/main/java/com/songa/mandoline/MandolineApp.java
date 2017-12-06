package com.songa.mandoline;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.songa.mandoline.artwork.lastfm.LastFm;
import com.squareup.picasso.Picasso;

/**
 * Mandoline application class.
 */
public class MandolineApp extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        LastFm.init(getApplicationContext());

        Picasso.setSingletonInstance(
                new Picasso.Builder(getApplicationContext())
                        .downloader(new OkHttp3Downloader(LastFm.getHttpClient()))
                        .build());
    }

    /**
     * Checks for permissions that are absolutely necessary for the application to work.
     *
     * @param context
     * @return true if the permissions have been granted
     */
    public static boolean checkPermissions(@NonNull Context context)
    {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}
