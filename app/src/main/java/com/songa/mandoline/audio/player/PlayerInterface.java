package com.songa.mandoline.audio.player;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.songa.mandoline.audio.player.state.PlayerStateNotifier;
import com.songa.mandoline.audio.entity.Track;

import java.util.List;

/**
 * Generic interface for a music player.
 */
public interface PlayerInterface
{
    /**
     * Changes the current playlist being played. Stops the current track currently being played
     * in the process.
     * Do not shuffle a playlist yourself, send your playlist ordered and then call setShuffle.
     *
     * @param tracks The new playlist to be played
     * @param startingPosition The first track to be played in the playlist
     * @param playNow true to start playing immediately
     */
    void setPlaylist(@NonNull List<Track> tracks, int startingPosition, boolean playNow);

    /**
     * Sets the repeat mode of the player (NONE, PLAYLIST, TRACK).
     *
     * @param repeatMode
     */
    void setRepeatMode(@NonNull PlaybackMode.RepeatMode repeatMode);

    /**
     * Sets the shuffle mode of the player.
     *
     * @param shuffle true to shuffle the current playlist, false to to restore the playlist in its original state
     */
    void setShuffle(boolean shuffle);

    /**
     * Resumes play. Does nothing if the player is already playing.
     */
    void play();

    /**
     * Pauses play. Does nothing is the player is already paused.
     */
    void pause();

    /**
     * Seeks the playback to the given position.
     *
     * @param positionInMs the new playback position in milliseconds
     */
    void seekTo(int positionInMs);

    /**
     * Skips to the next track.
     */
    void skipToNext();

    /**
     * Skips to the previous track.
     */
    void skipToPrevious();

    /**
     * Skips to an arbitrary position in the playlist.
     * @param playlistPosition
     */
    void skipToPlaylistPosition(int playlistPosition);

    /**
     * Returns the current playlist being played, with the right order (if the player is in shuffle mode,
     * will return the shuffled playlist being played).
     * @return
     */
    @NonNull List<Track> getPlaylist();

    /**
     * Return the position of the track being currently played in the playlist.
     * @return
     */
    int getPlaylistPosition();

    /**
     * Returns the current track being played.
     * @return
     */
    @Nullable Track getCurrentTrack();

    /**
     * Return the current playback position.
     *
     * @return
     */
    int getPlaybackPosition();

    /**
     * Return true if the player is shuffling.
     * @return
     */
    boolean isShuffling();

    /**
     * Return the current repeat mode of the playlist.
     * @return
     */
    @NonNull PlaybackMode.RepeatMode getRepeatMode();

    /**
     * Returns the notifier, used to notify changes in the player state.
     * Do not poll yourself info from the player except for when you initialize your class.
     *
     * @return
     */
    @NonNull
    PlayerStateNotifier getNotifier();
}
