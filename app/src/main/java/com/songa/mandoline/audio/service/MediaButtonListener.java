package com.songa.mandoline.audio.service;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

public class MediaButtonListener extends MediaSessionCompat.Callback
{
    private static final String TAG = MediaButtonListener.class.getSimpleName();

    private final @NonNull PlayerService service;

    public MediaButtonListener(@NonNull PlayerService service)
    {
        this.service = service;
    }

    @Override
    public void onCommand(String command, Bundle extras, ResultReceiver cb) {
        super.onCommand(command, extras, cb);
        Log.w(TAG, "RECEIVED command " + command);
    }

    @Override
    public void onPlay()
    {
        Log.i(TAG, "Received PLAY command");
        service.getPlayer().play();
    }

    @Override
    public void onPause()
    {
        Log.i(TAG, "Received PAUSE command");
        service.getPlayer().pause();
    }

    @Override
    public void onSkipToNext()
    {
        Log.i(TAG, "Received SKIP NEXT command");
        service.getPlayer().skipToNext();
    }

    @Override
    public void onSkipToPrevious()
    {
        Log.i(TAG, "Received SKIP PREVIOUS command");
        service.getPlayer().skipToPrevious();
    }
}
