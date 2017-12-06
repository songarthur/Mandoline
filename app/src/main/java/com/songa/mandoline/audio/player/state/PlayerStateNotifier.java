package com.songa.mandoline.audio.player.state;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.session.PlaybackStateCompat;

import com.songa.mandoline.audio.player.PlayerInterface;
import com.songa.mandoline.audio.entity.Track;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.ReplaySubject;

/**
 * Class used to notify to observers of the state of the player.
 */
public class PlayerStateNotifier
{
    private static final String TAG = PlaybackStateCompat.class.getSimpleName();

    private final @NonNull ReplaySubject<PlaybackStateEvent> playbackWatcher = ReplaySubject.createWithSize(1);

    public PlayerStateNotifier() {}


    public void notifyIsPlaying(@NonNull PlayerInterface player)
    {
        int playbackState = PlaybackStateCompat.STATE_PLAYING;
        int playbackPosition = player.getPlaybackPosition();
        int playlistPosition = player.getPlaylistPosition();

        notifyPlaybackState(playbackState, playbackPosition, null, playlistPosition);
    }

    public void notifyIsPaused(@NonNull PlayerInterface player)
    {
        int playbackState = PlaybackStateCompat.STATE_PAUSED;
        int playbackPosition = player.getPlaybackPosition();
        int playlistPosition = player.getPlaylistPosition();

        notifyPlaybackState(playbackState, playbackPosition, null, playlistPosition);
    }


    public void notifyIsStopped(@NonNull PlayerInterface player)
    {
        int playbackState = PlaybackStateCompat.STATE_PAUSED;
        int playbackPosition = 0;
        int playlistPosition = player.getPlaylistPosition();

        notifyPlaybackState(playbackState, playbackPosition, null, playlistPosition);
    }

    public void notifySkippedNext(@NonNull PlayerInterface player)
    {
        int playbackState = PlaybackStateCompat.STATE_SKIPPING_TO_NEXT;
        int playbackPosition = 0;
        int playlistPosition = player.getPlaylistPosition();

        notifyPlaybackState(playbackState, playbackPosition, null, playlistPosition);
    }

    public void notifySkippedPrevious(@NonNull PlayerInterface player)
    {
        int playbackState = PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS;
        int playbackPosition = 0;
        int playlistPosition = player.getPlaylistPosition();

        notifyPlaybackState(playbackState, playbackPosition, null, playlistPosition);
    }

    public void notifySkippedToArbitraryPosition(@NonNull PlayerInterface player)
    {
        int playbackState = PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM;
        int playbackPosition = 0;
        int playlistPosition = player.getPlaylistPosition();

        notifyPlaybackState(playbackState, playbackPosition, null, playlistPosition);
    }

    public void notifyPlaylistUpdate(@NonNull PlayerInterface player)
    {
        List<Track> playlist = player.getPlaylist();
        int playbackState = PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM;
        int playbackPosition = 0;
        int playlistPosition = player.getPlaylistPosition();

        notifyPlaybackState(playbackState, playbackPosition, playlist, playlistPosition);
    }

    private void notifyPlaybackState(int newState,
                                    int newPlaybackPosition,
                                    @Nullable List<Track> newPlaylist,
                                    int newPlaylistPosition)
    {
        PlaybackStateEvent notification = new PlaybackStateEvent(newState, newPlaybackPosition, newPlaylist, newPlaylistPosition);
        playbackWatcher.onNext(notification);
    }

    @NonNull
    public Observable<PlaybackStateEvent> getPlaybackWatcher()
    {
        return playbackWatcher;
    }

}
