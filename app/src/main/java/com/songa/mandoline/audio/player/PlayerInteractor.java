package com.songa.mandoline.audio.player;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.songa.mandoline.audio.player.state.PlayerStateNotifier;
import com.songa.mandoline.audio.entity.Track;

import java.util.List;

/**
 * Class used as to interact with an actual music player.
 * Allows us to serialize all calls and events sent to the player into a single separate thread.
 */
public class PlayerInteractor implements PlayerInterface
{
    private static final String TAG = PlayerInteractor.class.getSimpleName();

    private @NonNull PlayerManager player;

    private @NonNull Handler handler;

    public PlayerInteractor(@NonNull Handler handler, @NonNull PlayerManager mediaPlayer)
    {
        this.handler = handler;
        this.player = mediaPlayer;
    }

    @Override
    public void setPlaylist(@NonNull final List<Track> tracks, final int startingPosition, final boolean playNow)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                player.setPlaylist(tracks, startingPosition, playNow);
            }
        });
    }

    @Override
    public void setRepeatMode(@NonNull final PlaybackMode.RepeatMode repeatMode)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                player.setRepeatMode(repeatMode);
            }
        });
    }

    @Override
    public void setShuffle(final boolean shuffle)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                player.setShuffle(shuffle);
            }
        });
    }

    @Override
    public void play()
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                player.play();
            }
        });
    }

    @Override
    public void pause()
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                player.pause();
            }
        });
    }

    @Override
    public void seekTo(final int positionInMs)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                player.seekTo(positionInMs);
            }
        });
    }

    @Override
    public void skipToNext()
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                player.skipToNext();
            }
        });
    }

    @Override
    public void skipToPrevious()
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                player.skipToPrevious();
            }
        });
    }

    @Override
    public void skipToPlaylistPosition(final int playlistPosition)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                player.skipToPlaylistPosition(playlistPosition);
            }
        });
    }

    @NonNull
    @Override
    public List<Track> getPlaylist()
    {
        return player.getPlaylist();
    }

    @Override
    public int getPlaylistPosition()
    {
        return player.getPlaylistPosition();
    }

    @Nullable
    @Override
    public Track getCurrentTrack() {
        return player.getCurrentTrack();
    }

    @Override
    public int getPlaybackPosition()
    {
        return player.getPlaybackPosition();
    }

    @NonNull
    @Override
    public PlaybackMode.RepeatMode getRepeatMode() {
        return player.getRepeatMode();
    }

    @Override
    public boolean isShuffling() {
        return player.isShuffling();
    }

    @NonNull
    @Override
    public PlayerStateNotifier getNotifier()
    {
        return player.getNotifier();
    }
}
