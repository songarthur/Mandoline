package com.songa.mandoline.core.player;

import android.support.annotation.NonNull;

import com.songa.mandoline.audio.entity.Track;
import com.songa.mandoline.audio.player.PlaybackMode;

import java.util.List;

public interface PlayerView
{
    void setPlaylist(@NonNull List<Track> playlist);

    void setCurrentTrack(int playlistPosition);

    void setIsPlaying(boolean isPlaying);

    void setIsShuffling(boolean isShuffling);

    void setRepeatMode(@NonNull PlaybackMode.RepeatMode repeatMode);

    void setPlaybackPosition(int positionInMs);

    boolean isPlaying();

    boolean isShuffling();

    @NonNull PlaybackMode.RepeatMode getRepeatMode();

    int getPlaybackPosition();

    void enableTouchListeners();

    void disableTouchListeners();
}
