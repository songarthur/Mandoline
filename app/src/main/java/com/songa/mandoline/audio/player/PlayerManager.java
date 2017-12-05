package com.songa.mandoline.audio.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Looper;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.songa.mandoline.audio.player.state.PlayerStateNotifier;
import com.songa.mandoline.audio.entity.Track;
import com.songa.mandoline.audio.player.state.PlaybackStateSaver;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class PlayerManager implements PlayerInterface, MediaPlayer.OnCompletionListener
{
    private static final String TAG = PlayerManager.class.getSimpleName();

    private final @NonNull Context context;

    private @Nullable MediaPlayer player;
    private final @NonNull PlaylistQueue playlist;
    private @NonNull PlaybackMode.RepeatMode repeatMode = PlaybackMode.RepeatMode.NONE;

    private @NonNull PlayerStateNotifier notifier;
    private @NonNull PlaybackStateSaver stateSaver;

    public PlayerManager(@NonNull Context context, @NonNull PlaylistQueue playlistQueue, @NonNull PlayerStateNotifier notifier, @NonNull PlaybackStateSaver stateSaver)
    {
        this.context = context;
        this.player = null;
        this.playlist = playlistQueue;
        this.stateSaver = stateSaver;
        this.notifier = notifier;

        this.repeatMode = playlist.isLoopingPlaylist() ? PlaybackMode.RepeatMode.PLAYLIST : PlaybackMode.RepeatMode.NONE;
    }

    ///////////////////////////////////////////////////////////////////////////
    // PLAYBACK FUNCTIONS
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void setPlaylist(@NonNull List<Track> tracks, int startingPosition, boolean playNow)
    {
        Log.v(TAG, "New playlist set of size " + tracks.size() + " starting at position " + startingPosition);

        release();

        playlist.setPlaylist(tracks);
        playlist.setPlaylistPosition(startingPosition);

        notifier.notifyPlaylistUpdate(this);

        if (playNow) {
            Track track = playlist.getCurrentTrack();
            if (track==null) { release(); return; }
            loadTrack(track, playNow);
        }
    }

    @Override
    public void setRepeatMode(@NonNull PlaybackMode.RepeatMode repeatMode)
    {
        this.repeatMode = repeatMode;
        playlist.setLoopPlaylist(repeatMode==PlaybackMode.RepeatMode.PLAYLIST);
        if (player!=null) {
            player.setLooping(repeatMode==PlaybackMode.RepeatMode.TRACK);
        }
    }

    @Override
    public void setShuffle(boolean shuffle)
    {
        playlist.setShuffled(shuffle);
        notifier.notifyPlaylistUpdate(this);

        // should not load a new track
    }

    @Override
    public void skipToPlaylistPosition(int playlistPosition)
    {
        Log.v(TAG, "Skipping to playlist item no " + playlistPosition);

        if (playlist.getPlaylistPosition()==playlistPosition) {
            Log.v(TAG, "Playlist already set at the right position");
            play();
            return;
        }

        playlist.setPlaylistPosition(playlistPosition);
        Track track = playlist.getCurrentTrack();
        if (track==null) {
            release();
            return;
        }

        notifier.notifySkippedToArbitraryPosition(this);
        loadTrack(track, true);
    }

    @Override
    public void play()
    {
        if (player!=null) {
            player.start();
            startReportingPlaybackPosition();
            //notifier.notifyIsPlaying(this);
            Log.v(TAG, "Resuming play");

        } else {
            Track track = playlist.getCurrentTrack();
            loadTrack(track, true);
        }
    }

    @Override
    public void pause()
    {
        if (player==null || !player.isPlaying()) { return; }

        stopReportingPlaybackPosition(false);
        //notifier.notifyIsPaused(this);
        player.pause();
        Log.v(TAG, "Pause");
    }

    @Override
    public void seekTo(int positionInMs)
    {
        if (player==null) {
            Track track = playlist.getCurrentTrack();
            if (!loadTrack(track, false)) {
                return;
            }
        }

        player.seekTo(positionInMs);

        if (player.isPlaying()) {
            notifier.notifyIsPlaying(this);
        } else {
            notifier.notifyIsPaused(this);
        }

        Log.v(TAG, "Seeking to " + positionInMs);
    }

    @Override
    public void skipToNext()
    {
        Log.v(TAG, "Skipping to next track");

        Track track = playlist.skipToNextTrack();
        if (track==null) {
            Log.v(TAG, "Reached the end of the playlist");
            release();
        } else {
            notifier.notifySkippedNext(this);
            loadTrack(track, true);
        }
    }

    @Override
    public void skipToPrevious()
    {
        Log.v(TAG, "Skipping to previous track");

        Track track = playlist.skipToPreviousTrack();
        if (track==null) {
            release();
        } else {
            notifier.notifySkippedPrevious(this);
            loadTrack(track, true);
        }
    }

    public void release()
    {
        if (player!=null) {
            stopReportingPlaybackPosition(true);
            //notifier.notifyIsStopped(this);
            player.release();
            player = null;
            Log.v(TAG, "Releasing MediaPlayer resources");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // LOADING FUNCTIONS
    ///////////////////////////////////////////////////////////////////////////

    private boolean loadMedia(@NonNull String mediaUri, boolean play)
    {
        if (player!=null) {
            player.reset();
        } else {
            player = new MediaPlayer();
        }

        try {
            player.setDataSource(mediaUri);
            player.prepare();
            player.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
            player.setOnCompletionListener(this);
            player.setLooping(repeatMode==PlaybackMode.RepeatMode.TRACK);

            Log.v(TAG, "Loaded " + mediaUri);

            if (play) {
                player.start();
                startReportingPlaybackPosition();
                //notifier.notifyIsPlaying(this);
                Log.v(TAG, "Playing " + mediaUri);
            }

            return true;

        } catch (IOException e) {
            Log.e(TAG, "Unable to load " + mediaUri, e);
            release();
            return false;
        }
    }

    private boolean loadTrack(@Nullable Track track, boolean play)
    {
        if (track==null) {
            Log.v(TAG, "No track to play");
            release();
            return false;

        } else if (track.getTrackUri()==null) {
            Log.w(TAG, "Current track has no uri attached. Unable to play.");
            release();
            return false;
        }

        return loadMedia(track.getTrackUri(), play);
    }

    ///////////////////////////////////////////////////////////////////////////
    // PLAYER LISTENERS
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        Log.v(TAG, "Track ended : " + (playlist.getCurrentTrack()!=null ? playlist.getCurrentTrack().getTrackTitle() : "null"));
        skipToNext();
    }

    ///////////////////////////////////////////////////////////////////////////
    // PLAYBACK INFO
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public @NonNull List<Track> getPlaylist()
    {
        return playlist.getPlaylist();
    }

    @Override
    public int getPlaylistPosition()
    {
        return playlist.getPlaylistPosition();
    }

    @Nullable
    @Override
    public Track getCurrentTrack() {
        return playlist.getCurrentTrack();
    }

    @Override
    public int getPlaybackPosition()
    {
        return player!=null ? player.getCurrentPosition() : 0;
    }

    @Override
    public boolean isShuffling() {
        return playlist.isUsingShuffledPlaylist();
    }

    @Override
    @NonNull
    public PlaybackMode.RepeatMode getRepeatMode() {
        return repeatMode;
    }

    @Override
    @NonNull
    public PlayerStateNotifier getNotifier() {
        return notifier;
    }

    ///////////////////////////////////////////////////////////////////////////
    // PLAYBACK UPDATE
    ///////////////////////////////////////////////////////////////////////////

    private @Nullable Disposable playbackPositionReporter = null;

    private void startReportingPlaybackPosition()
    {
        if (playbackPositionReporter!=null && !playbackPositionReporter.isDisposed()) { return; }

        Looper looper = Looper.myLooper();

        if (looper==null) {
            Log.w(TAG, "Cannot report playback position. Playback reporting must occur inside a looper.");
            return;
        }

        playbackPositionReporter = Observable
                .interval(90, 500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.from(looper))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long tick) throws Exception {
                        notifier.notifyIsPlaying(PlayerManager.this);
                        if ((tick%10)==0) {
                            stateSaver.savePlaybackPosition(getPlaybackPosition());
                        }
                    }
                });
    }

    private void stopReportingPlaybackPosition(boolean isStopped)
    {
        if (playbackPositionReporter!=null) {
            playbackPositionReporter.dispose();
            playbackPositionReporter = null;
        }

        if (isStopped) {
            notifier.notifyIsStopped(this);
        } else {
            notifier.notifyIsPaused(this);
        }
    }
}
