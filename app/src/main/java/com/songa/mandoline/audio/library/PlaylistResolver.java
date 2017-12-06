package com.songa.mandoline.audio.library;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.songa.mandoline.audio.entity.Track;
import com.songa.mandoline.audio.player.PlaybackMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class used to generate playlists.
 */
public class PlaylistResolver
{
    /**
     * Generates a playlist when trying to play a certain track.<br><br>
     *
     * For instance when clicking on the 46th track on the "all tracks" tabs, this should generate
     * a playlist containing all tracks ordered alphabetically, with 45 as position.<br>
     *
     * But if you play a track while browsing a specific album, the generated playlist should only
     * contain the album's tracks, should be ordered by track number, and have the track's number
     * (minus one) as position.
     *
     * @param library the media library
     * @param trackId the track to be played
     * @param playlistMode the playlist mode
     * @return a pair made of the generated playlist, and the position of the track in the playlist.
     */
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

    /**
     * Generates a playlist for a given artist. Returns all tracks for a given artist ordered by title.
     *
     * @param library
     * @param artistId
     * @return
     */
    @NonNull
    public static Pair<List<Track>,Integer> resolvePlaylistFromArtist(@NonNull MediaLibrary library,
                                                                      long artistId)
    {
        List<Track> playlist = new ArrayList<>(library.tracksFromArtist(artistId));

        return new Pair<>(playlist, 0);
    }

    /**
     * Generates a playlist for a given album. Returns the album's tracks ordered by track number.
     *
     * @param library
     * @param albumId
     * @return
     */
    @NonNull
    public static Pair<List<Track>,Integer> resolvePlaylistFromAlbum(@NonNull MediaLibrary library,
                                                                     long albumId)
    {
        List<Track> playlist = new ArrayList<>(library.tracksFromAlbum(albumId));

        return new Pair<>(playlist, 0);
    }
}

