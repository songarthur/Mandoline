package com.songa.mandoline.audio.player;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.songa.mandoline.audio.player.state.PlayerStateNotifier;
import com.songa.mandoline.audio.entity.Track;

import java.util.List;

public interface PlayerInterface
{
    void setPlaylist(@NonNull List<Track> tracks, int startingPosition, boolean playNow);

    void setRepeatMode(@NonNull PlaybackMode.RepeatMode repeatMode);

    void setShuffle(boolean shuffle);

    void play();

    void pause();

    void seekTo(int positionInMs);

    void skipToNext();

    void skipToPrevious();

    void skipToPlaylistPosition(int playlistPosition);

    @NonNull List<Track> getPlaylist();

    int getPlaylistPosition();

    @Nullable Track getCurrentTrack();

    int getPlaybackPosition();

    boolean isShuffling();

    @NonNull PlaybackMode.RepeatMode getRepeatMode();

    @NonNull
    PlayerStateNotifier getNotifier();
}
