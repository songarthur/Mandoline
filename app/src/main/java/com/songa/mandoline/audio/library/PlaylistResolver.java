package com.songa.mandoline.audio.library;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.songa.mandoline.audio.entity.Track;
import com.songa.mandoline.audio.player.PlaybackMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistResolver
{
    @NonNull
    public static Pair<List<Track>,Integer> resolvePlaylistFromTrack(@NonNull MediaLibrary library,
                                                                     long trackId,
                                                                     @NonNull PlaybackMode.PlaylistMode playlistMode)
    {
        List<Track> playlist = new ArrayList<>();
        int position = 0;

        Track track = library.track(trackId);
        if (track==null) { return new Pair<>(playlist, position); }

        switch (playlistMode) {
            case ARTIST:
                // TODO : play all tracksFromAlbum for a given artist
                // for now we just fallback to ALL_TRACKS

            case ALL_TRACKS:
                playlist.addAll(library.allTracks());
                position = Collections.binarySearch(playlist, track, MediaLibrarySort.trackComparatorByTitle());
                break;

            case ALBUM:
                playlist.addAll(library.tracksFromAlbum(track.getAlbumId()));
                position = Collections.binarySearch(playlist, track, MediaLibrarySort.trackComparatorByTrackNumber());
                break;
        }

        return new Pair<>(playlist, position);
    }

    @NonNull
    public static Pair<List<Track>,Integer> resolvePlaylistFromArtist(@NonNull MediaLibrary library,
                                                                      long artistId)
    {
        List<Track> playlist = new ArrayList<>(library.tracksFromArtist(artistId));

        return new Pair<>(playlist, 0);
    }

    @NonNull
    public static Pair<List<Track>,Integer> resolvePlaylistFromAlbum(@NonNull MediaLibrary library,
                                                                     long albumId)
    {
        List<Track> playlist = new ArrayList<>(library.tracksFromAlbum(albumId));

        return new Pair<>(playlist, 0);
    }
}

