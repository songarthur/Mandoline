package com.songa.mandoline.core.player;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.session.PlaybackStateCompat;

import com.songa.mandoline.audio.player.PlaybackMode;
import com.songa.mandoline.audio.service.PlayerService;
import com.songa.mandoline.audio.player.state.PlaybackStateEvent;
import com.songa.mandoline.audio.player.PlayerInterface;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class PlayerPresenter
{
    private final @NonNull PlayerView view;

    private @Nullable PlayerService playerService;

    private Disposable playbackUpdater = null;

    public PlayerPresenter(@NonNull PlayerView view)
    {
        this.view = view;
    }

    public void onStart()
    {
        if (playerService!=null) {
            onPlayerServiceAvailable(playerService);
        } else {
            view.disableTouchListeners();
        }
    }

    public void onStop()
    {
        view.disableTouchListeners();
        stopPollingState();
    }

    public void onPlayerServiceAvailable(@NonNull PlayerService service)
    {
        this.playerService = service;
        PlayerInterface player = service.getPlayer();

        view.setPlaylist(player.getPlaylist());
        view.setCurrentTrack(player.getPlaylistPosition());

        view.setIsShuffling(player.isShuffling());
        view.setRepeatMode(player.getRepeatMode());

        startPollingState();
        view.enableTouchListeners();
    }

    public void actionPlayPause()
    {
        if (playerService==null) { return; }

        if (view.isPlaying()) {
            playerService.getPlayer().pause();
        } else {
            playerService.getPlayer().play();
        }
    }

    public void actionNextTrack()
    {
        if (playerService==null) { return; }
        playerService.getPlayer().skipToNext();
    }

    public void actionPreviousTrack()
    {
        if (playerService==null) { return; }
        playerService.getPlayer().skipToPrevious();
    }

    public void actionPageSelected(int position)
    {
        if (playerService==null) { return; }
        if (playerService.getPlayer().getPlaylistPosition()==position) { return; }
        playerService.getPlayer().skipToPlaylistPosition(position);
    }

    public void actionSeek()
    {
        if (playerService==null) { return; }

        int seek = view.getPlaybackPosition();
        playerService.getPlayer().seekTo(seek);
        view.setPlaybackPosition(seek);
    }

    public void actionShuffle()
    {
        if (playerService==null) { return; }

        boolean isShuffling = view.isShuffling();
        view.setIsShuffling(!isShuffling);
        playerService.getPlayer().setShuffle(!isShuffling);
    }

    public void actionRepeatMode()
    {
        if (playerService==null) { return; }

        PlaybackMode.RepeatMode mode = view.getRepeatMode();

        switch (mode) {
            case NONE:
                mode = PlaybackMode.RepeatMode.PLAYLIST;
                break;
            case PLAYLIST:
                mode = PlaybackMode.RepeatMode.TRACK;
                break;
            case TRACK:
                mode = PlaybackMode.RepeatMode.NONE;
                break;
        }

        playerService.getPlayer().setRepeatMode(mode);
        view.setRepeatMode(mode);
    }

    ///////////////////////////////////////////////////////////////////////////
    // STATE POLLING
    ///////////////////////////////////////////////////////////////////////////

    public void startPollingState()
    {
        if (playerService==null) { return; }
        if (playbackUpdater !=null && !playbackUpdater.isDisposed()) { return; }

        playbackUpdater = playerService.getPlayer().getNotifier().getPlaybackWatcher()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PlaybackStateEvent>() {
                    @Override
                    public void accept(PlaybackStateEvent playbackStateEvent) throws Exception {
                        if (playbackStateEvent==null) { return; }
                        updatePlayer(playbackStateEvent);
                    }
                });
    }

    public void stopPollingState()
    {
        if (playbackUpdater!=null) {
            playbackUpdater.dispose();
            playbackUpdater = null;
        }
    }

    public void updatePlayer(@NonNull PlaybackStateEvent event)
    {
        switch (event.getNewState()) {

            case PlaybackStateCompat.STATE_PLAYING:
                view.setIsPlaying(true);
                view.setPlaybackPosition(event.getNewPlaybackPosition());
                break;

            case PlaybackStateCompat.STATE_PAUSED:
                view.setIsPlaying(false);
                view.setPlaybackPosition(event.getNewPlaybackPosition());
                break;

            case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT:
            case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS:
                view.setCurrentTrack(event.getNewPlaylistPosition());
                view.setIsPlaying(true);
                view.setPlaybackPosition(event.getNewPlaybackPosition());
                break;

            case PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM:
                if (event.getNewPlaylist()!=null) {
                    view.setPlaylist(event.getNewPlaylist());
                }
                view.setCurrentTrack(event.getNewPlaylistPosition());
                break;
        }
    }

}
