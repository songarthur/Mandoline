package com.songa.mandoline.audio.service;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

/**
 * Listener for media button events.
 */
public class MediaButtonListener extends MediaSessionCompat.Callback
{
    private static final String TAG = MediaButtonListener.class.getSimpleName();

    private final @NonNull PlayerService service;

    public MediaButtonListener(@NonNull PlayerService service)
    {
        this.service = service;
    }

    @Override
    public void onCommand(String command, Bundle extras, ResultReceiver cb)
    {
        super.onCommand(command, extras, cb);
        Log.v(TAG, "RECEIVED command " + command);
    }

    @Override
    public void onPlay()
    {
        Log.d(TAG, "Received PLAY command");
        service.getPlayer().play();
    }

    @Override
    public void onPause()
    {
        Log.d(TAG, "Received PAUSE command");
        service.getPlayer().pause();
    }

    @Override
    public void onSkipToNext()
    {
        Log.d(TAG, "Received SKIP NEXT command");
        service.getPlayer().skipToNext();
    }

    @Override
    public void onSkipToPrevious()
    {
        Log.d(TAG, "Received SKIP PREVIOUS command");
        service.getPlayer().skipToPrevious();
    }
}
