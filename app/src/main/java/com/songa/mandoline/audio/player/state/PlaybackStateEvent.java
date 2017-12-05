package com.songa.mandoline.audio.player.state;

import android.support.annotation.Nullable;

import com.songa.mandoline.audio.entity.Track;

import java.util.List;

public class PlaybackStateEvent
{
    private int newState;
    private int newPlaybackPosition;

    private @Nullable List<Track> newPlaylist;
    private int newPlaylistPosition;

    public PlaybackStateEvent(int newState, int newPlaybackPosition, List<Track> newPlaylist, int newPlaylistPosition) {
        this.newState = newState;
        this.newPlaybackPosition = newPlaybackPosition;
        this.newPlaylist = newPlaylist;
        this.newPlaylistPosition = newPlaylistPosition;
    }

    public int getNewState() {
        return newState;
    }

    public int getNewPlaybackPosition() {
        return newPlaybackPosition;
    }

    @Nullable
    public List<Track> getNewPlaylist() {
        return newPlaylist;
    }

    public int getNewPlaylistPosition() {
        return newPlaylistPosition;
    }
}
