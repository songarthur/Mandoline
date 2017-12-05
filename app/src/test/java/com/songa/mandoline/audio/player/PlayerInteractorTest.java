package com.songa.mandoline.audio.player;

import android.os.Handler;

import com.songa.mandoline.audio.entity.Track;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PlayerInteractorTest
{
    @Mock
    public PlayerManager mockPlayer;

    @Mock
    public Handler mockHandler;

    List<Track> list = new ArrayList<>();

    @Before
    public void initMocks()
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

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Runnable task = invocation.getArgumentAt(0, Runnable.class);
                if (task!=null) { task.run(); }
                return null;
            }
        })
                .when(mockHandler)
                .post(Matchers.any(Runnable.class));
    }

    @Test
    public void repeatMode()
    {
        PlayerInteractor interactor = new PlayerInteractor(mockHandler, mockPlayer);

        interactor.setRepeatMode(PlaybackMode.RepeatMode.TRACK);

        Mockito.verify(mockPlayer, Mockito.times(1))
                .setRepeatMode(Matchers.eq(PlaybackMode.RepeatMode.TRACK));
    }

    @Test
    public void shuffle()
    {
        PlayerInteractor interactor = new PlayerInteractor(mockHandler, mockPlayer);

        interactor.setShuffle(true);

        Mockito.verify(mockPlayer, Mockito.times(1))
                .setShuffle(Matchers.eq(true));
    }

    @Test
    public void play()
    {
        PlayerInteractor interactor = new PlayerInteractor(mockHandler, mockPlayer);

        interactor.play();

        Mockito.verify(mockPlayer, Mockito.times(1))
                .play();
    }

    @Test
    public void pause()
    {
        PlayerInteractor interactor = new PlayerInteractor(mockHandler, mockPlayer);

        interactor.pause();

        Mockito.verify(mockPlayer, Mockito.times(1))
                .pause();
    }

    @Test
    public void seek()
    {
        PlayerInteractor interactor = new PlayerInteractor(mockHandler, mockPlayer);

        interactor.seekTo(456);

        Mockito.verify(mockPlayer, Mockito.times(1))
                .seekTo(Matchers.eq(456));
    }

    @Test
    public void skipNext()
    {
        PlayerInteractor interactor = new PlayerInteractor(mockHandler, mockPlayer);

        interactor.skipToNext();

        Mockito.verify(mockPlayer, Mockito.times(1))
                .skipToNext();
    }

    @Test
    public void skipPrevious()
    {
        PlayerInteractor interactor = new PlayerInteractor(mockHandler, mockPlayer);

        interactor.skipToPrevious();

        Mockito.verify(mockPlayer, Mockito.times(1))
                .skipToPrevious();
    }

    @Test
    public void skipPosition()
    {
        PlayerInteractor interactor = new PlayerInteractor(mockHandler, mockPlayer);

        interactor.skipToPlaylistPosition(19);

        Mockito.verify(mockPlayer, Mockito.times(1))
                .skipToPlaylistPosition(Matchers.eq(19));
    }
}
