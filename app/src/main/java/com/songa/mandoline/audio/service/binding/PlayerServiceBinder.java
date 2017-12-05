package com.songa.mandoline.audio.service.binding;

import android.os.Binder;
import android.support.annotation.NonNull;

import com.songa.mandoline.audio.service.PlayerService;

public class PlayerServiceBinder extends Binder
{
    private @NonNull
    PlayerService service;

    public PlayerServiceBinder(@NonNull PlayerService service) {
        this.service = service;
    }

    @NonNull
    public PlayerService getService() {
        return service;
    }
}
