package com.songa.mandoline.audio.player.state;

import android.support.annotation.Nullable;

import com.songa.mandoline.audio.entity.Track;

import java.util.List;

/**
 * Class encompassing the player's state.
 */
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

    /**
     * Return the player's playing state.
     * See {@link android.support.v4.media.session.PlaybackStateCompat}
     *
     * @return
     */
    public int getNewState() {
        return newState;
    }

    /**
     * The current playback position (how much of the current track that has been played already)
     *
     * @return
     */
    public int getNewPlaybackPosition() {
        return newPlaybackPosition;
    }

    /**
     * The new playlist being played. Null if the playlist hasn't changed.
     * Should be null unless the state is STATE_SKIPPING_TO_QUEUE_ITEM
     *
     * @return
     */
    @Nullable
    public List<Track> getNewPlaylist() {
        return newPlaylist;
    }

    /**
     * The current track being played in the playlist.
     * @return
     */
    public int getNewPlaylistPosition() {
        return newPlaylistPosition;
    }
}
