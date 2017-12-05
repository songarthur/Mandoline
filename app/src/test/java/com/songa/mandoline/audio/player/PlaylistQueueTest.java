package com.songa.mandoline.audio.player;

import com.songa.mandoline.audio.entity.Track;
import com.songa.mandoline.audio.player.state.PlaybackStateSaver;
import com.songa.mandoline.util.TrackListMatcher;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PlaylistQueueTest
{
    @Mock
    public PlaybackStateSaver stateSaver;

    public List<Track> tracks = new ArrayList<>();
    public List<Track> tracksShuffled =new ArrayList<>();

    @Before
    public void initData()
    {
        Track track1 = new Track(
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
                "");

        Track track2 = new Track(
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
                "");

        Track track3 = new Track(
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
                "");

        Track track4 = new Track(
                4,
                "Track4",
                9,
                "Artist9",
                6,
                "Album6",
                11,
                20000,
                2003,
                null,
                "");

        tracks.add(track1);
        tracks.add(track2);
        tracks.add(track3);
        tracks.add(track4);

        tracksShuffled.addAll(tracks);
        Collections.shuffle(tracksShuffled);
    }

    @Test
    public void initialState()
    {
        PlaylistQueue playlistQueue = new PlaylistQueue(stateSaver);

        Assert.assertEquals(0, playlistQueue.getPlaylist().size());
        Assert.assertEquals(0, playlistQueue.getPlaylistPosition());
        Assert.assertNull(playlistQueue.getCurrentTrack());

        Assert.assertFalse(playlistQueue.isLoopingPlaylist());
        Assert.assertFalse(playlistQueue.isUsingShuffledPlaylist());
    }

    @Test
    public void restoredState()
    {
        PlaylistQueue playlistQueue = new PlaylistQueue(stateSaver);

        playlistQueue.restoreState(
                tracks,
                tracksShuffled,
                2,
                true,
                true);

        Assert.assertTrue(playlistQueue.isUsingShuffledPlaylist());
        Assert.assertTrue(playlistQueue.isLoopingPlaylist());

        Assert.assertEquals(2, playlistQueue.getPlaylistPosition());
        Assert.assertEquals(tracksShuffled.size(), playlistQueue.getPlaylist().size());

        for (int i=0; i<tracksShuffled.size(); i++) {
            Assert.assertEquals(tracksShuffled.get(i), playlistQueue.getPlaylist().get(i));
        }
    }

    @Test
    public void restoredState2()
    {
        PlaylistQueue playlistQueue = new PlaylistQueue(stateSaver);

        playlistQueue.restoreState(
                tracks,
                tracksShuffled,
                0,
                false,
                false);

        Assert.assertFalse(playlistQueue.isUsingShuffledPlaylist());
        Assert.assertFalse(playlistQueue.isLoopingPlaylist());

        Assert.assertEquals(0, playlistQueue.getPlaylistPosition());
        Assert.assertEquals(tracks.size(), playlistQueue.getPlaylist().size());

        for (int i=0; i<tracks.size(); i++) {
            Assert.assertEquals(tracks.get(i), playlistQueue.getPlaylist().get(i));
        }
    }

    @Test
    public void restoredStateWithEmptyPlaylist()
    {
        PlaylistQueue playlistQueue = new PlaylistQueue(stateSaver);

        playlistQueue.restoreState(
                new ArrayList<Track>(),
                new ArrayList<Track>(),
                0,
                true,
                false);

        Assert.assertTrue(playlistQueue.isUsingShuffledPlaylist());
        Assert.assertFalse(playlistQueue.isLoopingPlaylist());

        Assert.assertEquals(0, playlistQueue.getPlaylistPosition());
        Assert.assertNull(playlistQueue.getCurrentTrack());
        Assert.assertEquals(0, playlistQueue.getPlaylist().size());
    }

    @Test
    public void saveState()
    {
        PlaylistQueue playlistQueue = new PlaylistQueue(stateSaver);

        playlistQueue.restoreState(
                tracks,
                tracksShuffled,
                2,
                true,
                true);

        playlistQueue.saveState();

        Mockito.verify(stateSaver, Mockito.times(1))
                .savePlaylistSettings(
                        Matchers.argThat(new TrackListMatcher(tracks)),
                        Matchers.argThat(new TrackListMatcher(tracksShuffled)),
                        Matchers.eq(2),
                        Matchers.eq(true),
                        Matchers.eq(true));
    }

    @Test
    public void setPlaylist()
    {
        PlaylistQueue playlistQueue = new PlaylistQueue(stateSaver);
        PlaylistQueue spy = Mockito.spy(playlistQueue);

        spy.setPlaylist(tracks);

        Assert.assertEquals(0, spy.getPlaylistPosition());
        Assert.assertFalse(spy.isUsingShuffledPlaylist());
        Assert.assertEquals(tracks.size(), spy.getPlaylist().size());

        for (int i=0; i<tracks.size(); i++) {
            Assert.assertEquals(tracks.get(i), spy.getPlaylist().get(i));
        }

        Mockito.verify(spy, Mockito.times(1))
                .saveState();
    }

    @Test
    public void setLooping()
    {
        PlaylistQueue playlistQueue = new PlaylistQueue(stateSaver);
        playlistQueue.setLoopPlaylist(true);

        Mockito.verify(stateSaver, Mockito.times(1))
                .savePlaylistIsLooping(Matchers.eq(true));
    }

    @Test
    public void setShuffled()
    {
        PlaylistQueue playlistQueue = new PlaylistQueue(stateSaver);
        playlistQueue.setPlaylist(tracks);
        playlistQueue.setPlaylistPosition(1);

        Track track = playlistQueue.getCurrentTrack();

        playlistQueue.setShuffled(true);

        Assert.assertTrue(playlistQueue.isUsingShuffledPlaylist());
        Assert.assertEquals(0, playlistQueue.getPlaylistPosition());
        Assert.assertEquals(tracks.size(), playlistQueue.getPlaylist().size());
        Assert.assertEquals(track, playlistQueue.getCurrentTrack());

        // we trust that Collections.shuffle does its job correctly
        // we do not test if the playlist has actually been shuffled

        Mockito.verify(stateSaver, Mockito.times(1))
                .savePlaylistShuffledState(
                        Matchers.anyListOf(Track.class),
                        Matchers.eq(0));
    }

    @Test
    public void switchShuffle()
    {
        PlaylistQueue playlistQueue = new PlaylistQueue(stateSaver);
        playlistQueue.setPlaylist(tracks);
        playlistQueue.setPlaylistPosition(2);

        playlistQueue.setShuffled(true);
        playlistQueue.setShuffled(false);

        Assert.assertFalse(playlistQueue.isUsingShuffledPlaylist());
        Assert.assertEquals(2, playlistQueue.getPlaylistPosition());
        Assert.assertEquals(tracks.size(), playlistQueue.getPlaylist().size());

        for (int i=0; i<tracks.size(); i++) {
            Assert.assertEquals(tracks.get(i), playlistQueue.getPlaylist().get(i));
        }

        Mockito.verify(stateSaver, Mockito.times(1))
                .savePlaylistNotShuffledState(Matchers.eq(2));
    }

    @Test
    public void setPlaylistPosition()
    {
        PlaylistQueue playlistQueue = new PlaylistQueue(stateSaver);
        playlistQueue.setPlaylist(tracks);

        playlistQueue.setPlaylistPosition(1);

        Assert.assertEquals(1, playlistQueue.getPlaylistPosition());
        Assert.assertEquals(tracks.get(1), playlistQueue.getCurrentTrack());

        Mockito.verify(stateSaver, Mockito.times(1))
                .savePlaylistPosition(Matchers.eq(1));
    }

    @Test
    public void setPlaylistPositionOutOfBounds()
    {
        PlaylistQueue playlistQueue = new PlaylistQueue(stateSaver);
        playlistQueue.setPlaylist(tracks);

        playlistQueue.setPlaylistPosition(6);
        playlistQueue.setPlaylistPosition(-1);

        Assert.assertEquals(0, playlistQueue.getPlaylistPosition());

        Mockito.verify(stateSaver, Mockito.times(0))
                .savePlaylistPosition(Matchers.anyInt());
    }

    @Test
    public void skipNext()
    {
        PlaylistQueue playlistQueue = new PlaylistQueue(stateSaver);
        playlistQueue.setPlaylist(tracks);
        Track nextTrack = playlistQueue.skipToNextTrack();

        Assert.assertEquals(1, playlistQueue.getPlaylistPosition());
        Assert.assertEquals(tracks.get(1), nextTrack);
        Assert.assertEquals(tracks.get(1), playlistQueue.getCurrentTrack());

        Mockito.verify(stateSaver, Mockito.times(1))
                .savePlaylistPosition(Matchers.eq(1));
    }

    @Test
    public void skipNextOutOfBounds()
    {
        PlaylistQueue playlistQueue = new PlaylistQueue(stateSaver);
        playlistQueue.setPlaylist(tracks);
        playlistQueue.setPlaylistPosition(tracks.size()-1);

        Track nextTrack = playlistQueue.skipToNextTrack();

        Assert.assertEquals(tracks.size()-1, playlistQueue.getPlaylistPosition());
        Assert.assertNull(nextTrack);

        Mockito.verify(stateSaver, Mockito.times(1))
                .savePlaylistPosition(Matchers.anyInt());
    }

    @Test
    public void skipNextLoop()
    {
        PlaylistQueue playlistQueue = new PlaylistQueue(stateSaver);
        playlistQueue.setPlaylist(tracks);
        playlistQueue.setPlaylistPosition(tracks.size()-1);
        playlistQueue.setLoopPlaylist(true);

        Track nextTrack = playlistQueue.skipToNextTrack();

        Assert.assertEquals(0, playlistQueue.getPlaylistPosition());
        Assert.assertEquals(playlistQueue.getPlaylist().get(0), nextTrack);

        Mockito.verify(stateSaver, Mockito.times(1))
                .savePlaylistPosition(Matchers.eq(0));
    }

    @Test
    public void skipPrevious()
    {
        PlaylistQueue playlistQueue = new PlaylistQueue(stateSaver);
        playlistQueue.setPlaylist(tracks);
        playlistQueue.setPlaylistPosition(2);

        Track previousTrack = playlistQueue.skipToPreviousTrack();

        Assert.assertEquals(1, playlistQueue.getPlaylistPosition());
        Assert.assertEquals(tracks.get(1), previousTrack);
        Assert.assertEquals(tracks.get(1), playlistQueue.getCurrentTrack());

        Mockito.verify(stateSaver, Mockito.times(1))
                .savePlaylistPosition(Matchers.eq(1));
    }

    @Test
    public void skipPreviousOutOfBounds()
    {
        PlaylistQueue playlistQueue = new PlaylistQueue(stateSaver);
        playlistQueue.setPlaylist(tracks);

        Track previousTrack = playlistQueue.skipToPreviousTrack();

        Assert.assertEquals(0, playlistQueue.getPlaylistPosition());
        Assert.assertNull(previousTrack);

        Mockito.verify(stateSaver, Mockito.times(0))
                .savePlaylistPosition(Matchers.anyInt());
    }

    @Test
    public void skipPreviousLoop()
    {
        PlaylistQueue playlistQueue = new PlaylistQueue(stateSaver);
        playlistQueue.setPlaylist(tracks);
        playlistQueue.setLoopPlaylist(true);

        Track previousTrack = playlistQueue.skipToPreviousTrack();

        Assert.assertEquals(tracks.size()-1, playlistQueue.getPlaylistPosition());
        Assert.assertEquals(playlistQueue.getCurrentTrack(), previousTrack);

        Mockito.verify(stateSaver, Mockito.times(1))
                .savePlaylistPosition(Matchers.eq(tracks.size()-1));
    }

    @Test
    public void getCurrentTrack()
    {
        PlaylistQueue playlistQueue = new PlaylistQueue(stateSaver);
        playlistQueue.setPlaylist(tracks);
        playlistQueue.setPlaylistPosition(1);

        Track track = playlistQueue.getCurrentTrack();

        Assert.assertNotNull(track);
        Assert.assertEquals(playlistQueue.getPlaylist().get(1), track);
    }

}
