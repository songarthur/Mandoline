package com.songa.mandoline.audio.player;

import android.support.v4.media.session.PlaybackStateCompat;

import com.songa.mandoline.audio.player.state.PlaybackStateEvent;
import com.songa.mandoline.audio.player.state.PlayerStateNotifier;
import com.songa.mandoline.audio.entity.Track;
import com.songa.mandoline.util.TrackListMatcher;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Predicate;
import io.reactivex.observers.TestObserver;

@RunWith(MockitoJUnitRunner.class)
public class PlayerStateNotifierTest
{
    @Mock
    PlayerInterface mockPlayer;

    List<Track> list = new ArrayList<>();

    @Before
    public void initPlayer()
    {
        list.add(new Track(
                1,
                "Track1",
                3,
                "Artist3",
                8,
                "Album8",
                2,
                10000,
                1999,
                null,
                ""));

        list.add(new Track(
                2,
                "Track2",
                3,
                "Artist3",
                8,
                "Album8",
                7,
                10000,
                2001,
                null,
                ""));

        list.add(new Track(
                3,
                "Track3",
                1,
                "Artist1",
                3,
                "Album3",
                1,
                10000,
                1996,
                null,
                ""));

        Mockito.doReturn(1234)
                .when(mockPlayer)
                .getPlaybackPosition();

        Mockito.doReturn(8)
                .when(mockPlayer)
                .getPlaylistPosition();

        Mockito.doReturn(list)
                .when(mockPlayer)
                .getPlaylist();
    }

    @Test
    public void playNotification()
    {
        PlayerStateNotifier notifier = new PlayerStateNotifier();
        TestObserver<PlaybackStateEvent> observer = notifier.getPlaybackWatcher().test();

        notifier.notifyIsPlaying(mockPlayer);

        observer.assertNotComplete();
        observer.assertNoErrors();
        observer.assertValueCount(1);

        observer.assertValueAt(0, new Predicate<PlaybackStateEvent>() {
            @Override
            public boolean test(PlaybackStateEvent event) throws Exception {
                return event.getNewState() == PlaybackStateCompat.STATE_PLAYING
                        && event.getNewPlaybackPosition() == 1234
                        && event.getNewPlaylistPosition() == 8
                        && event.getNewPlaylist() == null;
            }
        });
    }

    @Test
    public void pauseNotification()
    {
        PlayerStateNotifier notifier = new PlayerStateNotifier();
        TestObserver<PlaybackStateEvent> observer = notifier.getPlaybackWatcher().test();

        notifier.notifyIsPaused(mockPlayer);

        observer.assertNotComplete();
        observer.assertNoErrors();
        observer.assertValueCount(1);

        observer.assertValueAt(0, new Predicate<PlaybackStateEvent>() {
            @Override
            public boolean test(PlaybackStateEvent event) throws Exception {
                return event.getNewState() == PlaybackStateCompat.STATE_PAUSED
                        && event.getNewPlaybackPosition() == 1234
                        && event.getNewPlaylistPosition() == 8
                        && event.getNewPlaylist() == null;
            }
        });
    }

    @Test
    public void stopNotification()
    {
        PlayerStateNotifier notifier = new PlayerStateNotifier();
        TestObserver<PlaybackStateEvent> observer = notifier.getPlaybackWatcher().test();

        notifier.notifyIsStopped(mockPlayer);

        observer.assertNotComplete();
        observer.assertNoErrors();
        observer.assertValueCount(1);

        observer.assertValueAt(0, new Predicate<PlaybackStateEvent>() {
            @Override
            public boolean test(PlaybackStateEvent event) throws Exception {
                return event.getNewState() == PlaybackStateCompat.STATE_PAUSED
                        && event.getNewPlaybackPosition() == 0
                        && event.getNewPlaylistPosition() == 8
                        && event.getNewPlaylist() == null;
            }
        });
    }

    @Test
    public void skipNextNotification()
    {
        PlayerStateNotifier notifier = new PlayerStateNotifier();
        TestObserver<PlaybackStateEvent> observer = notifier.getPlaybackWatcher().test();

        notifier.notifySkippedNext(mockPlayer);

        observer.assertNotComplete();
        observer.assertNoErrors();
        observer.assertValueCount(1);

        observer.assertValueAt(0, new Predicate<PlaybackStateEvent>() {
            @Override
            public boolean test(PlaybackStateEvent event) throws Exception {
                return event.getNewState() == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT
                        && event.getNewPlaybackPosition() == 0
                        && event.getNewPlaylistPosition() == 8
                        && event.getNewPlaylist() == null;
            }
        });
    }

    @Test
    public void skipPreviousNotification()
    {
        PlayerStateNotifier notifier = new PlayerStateNotifier();
        TestObserver<PlaybackStateEvent> observer = notifier.getPlaybackWatcher().test();

        notifier.notifySkippedPrevious(mockPlayer);

        observer.assertNotComplete();
        observer.assertNoErrors();
        observer.assertValueCount(1);

        observer.assertValueAt(0, new Predicate<PlaybackStateEvent>() {
            @Override
            public boolean test(PlaybackStateEvent event) throws Exception {
                return event.getNewState() == PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS
                        && event.getNewPlaybackPosition() == 0
                        && event.getNewPlaylistPosition() == 8
                        && event.getNewPlaylist() == null;
            }
        });
    }

    @Test
    public void arbitrarySkipNotification()
    {
        PlayerStateNotifier notifier = new PlayerStateNotifier();
        TestObserver<PlaybackStateEvent> observer = notifier.getPlaybackWatcher().test();

        notifier.notifySkippedToArbitraryPosition(mockPlayer);

        observer.assertNotComplete();
        observer.assertNoErrors();
        observer.assertValueCount(1);

        observer.assertValueAt(0, new Predicate<PlaybackStateEvent>() {
            @Override
            public boolean test(PlaybackStateEvent event) throws Exception {
                return event.getNewState() == PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM
                        && event.getNewPlaybackPosition() == 0
                        && event.getNewPlaylistPosition() == 8
                        && event.getNewPlaylist() == null;
            }
        });
    }

    @Test
    public void newPlaylistNotification()
    {
        PlayerStateNotifier notifier = new PlayerStateNotifier();
        TestObserver<PlaybackStateEvent> observer = notifier.getPlaybackWatcher().test();

        notifier.notifyPlaylistUpdate(mockPlayer);

        observer.assertNotComplete();
        observer.assertNoErrors();
        observer.assertValueCount(1);

        observer.assertValueAt(0, new Predicate<PlaybackStateEvent>() {
            @Override
            public boolean test(PlaybackStateEvent event) throws Exception {

                boolean playlistVerified = new TrackListMatcher(list).matches(event.getNewPlaylist());

                return event.getNewState() == PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM
                        && event.getNewPlaybackPosition() == 0
                        && event.getNewPlaylistPosition() == 8
                        && event.getNewPlaylist() != null
                        && playlistVerified;
            }
        });
    }
}
