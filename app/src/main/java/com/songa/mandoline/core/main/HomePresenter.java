package com.songa.mandoline.core.main;

import android.support.annotation.NonNull;
import android.support.v4.media.session.PlaybackStateCompat;

import com.songa.mandoline.audio.player.state.PlaybackStateEvent;
import com.songa.mandoline.audio.player.PlayerInterface;
import com.songa.mandoline.audio.entity.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class HomePresenter
{
    private final @NonNull HomeView view;
    private final @NonNull PlayerInterface player;

    private final @NonNull List<Track> playlist = new ArrayList<>();
    private int playlistPosition = 0;

    private Disposable playerUpdater = null;

    public HomePresenter(@NonNull HomeView view, @NonNull PlayerInterface player)
    {
        this.view = view;
        this.player = player;
    }

    public void start()
    {
        if (playerUpdater!=null && !playerUpdater.isDisposed()) {
            return;
        }

        playlist.clear();
        playlist.addAll(player.getPlaylist());
        playlistPosition = player.getPlaylistPosition();

        if (playlistPosition>=0 && playlistPosition<playlist.size()) {
            view.setCurrentTrack(playlist.get(playlistPosition));
        }

        playerUpdater = player.getNotifier().getPlaybackWatcher()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PlaybackStateEvent>() {
                    @Override
                    public void accept(PlaybackStateEvent playbackStateEvent) throws Exception {
                        if (playbackStateEvent==null) { return; }
                        onPlaybackEvent(playbackStateEvent);
                    }
                });
    }

    public void stop()
    {
        if (playerUpdater!=null) {
            playerUpdater.dispose();
            playerUpdater = null;
        }
    }

    public void actionPlayPause(boolean isPlaying)
    {
        if (isPlaying) {
            player.pause();
        } else {
            player.play();
        }
    }

    private void onPlaybackEvent(@NonNull PlaybackStateEvent event)
    {
        switch (event.getNewState()) {

            case PlaybackStateCompat.STATE_PLAYING:
                view.setPlaying(true);
                break;

            case PlaybackStateCompat.STATE_PAUSED:
                view.setPlaying(false);
                break;

            case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT:
            case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS:
            case PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM:

                if (event.getNewPlaylist()!=null) {
                    playlist.clear();
                    playlist.addAll(event.getNewPlaylist());
                }

                playlistPosition = event.getNewPlaylistPosition();
                if (playlistPosition<0 || playlistPosition>=playlist.size()) {
                    view.setCurrentTrack(null);
                } else {
                    view.setCurrentTrack(playlist.get(playlistPosition));
                }
        }
    }


}
