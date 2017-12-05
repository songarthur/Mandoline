package com.songa.mandoline.core.main;

import android.support.annotation.Nullable;

import com.songa.mandoline.audio.entity.Track;

public interface HomeView
{
    void setCurrentTrack(@Nullable Track track);
    void setPlaying(boolean isPlaying);
}
