package com.songa.mandoline.audio.library;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.util.LongSparseArray;

import com.songa.mandoline.audio.entity.Album;
import com.songa.mandoline.audio.entity.Artist;
import com.songa.mandoline.audio.entity.Track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Singleton class.<br><br>
 *
 * Media library that stores and caches all the data extracted from {@link android.provider.MediaStore}
 * by {@link MediaScanner}.<br>
 *
 * Use this class to access quickly to the device's current music library.
 */
public class MediaLibrary
{
    private static final String TAG = MediaLibrary.class.getSimpleName();

    private static MediaLibrary instance = null;

    private final @NonNull MediaScanner scanner;

    private final @NonNull LongSparseArray<Track> trackCatalog = new LongSparseArray<>();
    private final @NonNull LongSparseArray<Artist> artistCatalog = new LongSparseArray<>();
    private final @NonNull LongSparseArray<Album> albumCatalog = new LongSparseArray<>();

    private final @NonNull LongSparseArray<Set<Album>> albumsForArtist = new LongSparseArray<>();
    private final @NonNull LongSparseArray<Set<Track>> tracksForAlbum = new LongSparseArray<>();

    private final @NonNull List<Track> allTracks = new ArrayList<>();
    private final @NonNull List<Artist> allArtists = new ArrayList<>();
    private final @NonNull List<Album> allAlbums = new ArrayList<>();

    /**
     * Returns an observable that emits the library instance when it is ready.
     * The library is generated asynchronously.
     *
     * @param context
     * @return
     */
    public static Single<MediaLibrary> getLibraryProvider(@NonNull final Context context)
    {
        return Single
                .create(new SingleOnSubscribe<MediaLibrary>() {
            @Override
            public void subscribe(SingleEmitter<MediaLibrary> e) throws Exception {e.onSuccess(getInstance(context));}
        })
                .subscribeOn(Schedulers.io());
    }

    /**
     * Tries to get the instance synchronously. If not available return null immediately.
     * @return
     */
    public static @Nullable MediaLibrary tryToGetInstance()
    {
        return instance;
    }

    /**
     * Returns a singleton instance of MediaLibrary. Generates a new one if necessary.
     *
     * @param context
     * @return
     */
    @WorkerThread
    private static synchronized MediaLibrary getInstance(@NonNull Context context)
    {
        if (instance==null) { instance = new MediaLibrary(context.getApplicationContext()); }
        return instance;

    }

    /**
     * Generates a MediaLibrary instance and initializes it.
     * Beware : IO and computation heavy
     *
     * @param context
     * @return
     */
    @WorkerThread
    private MediaLibrary(@NonNull Context context)
    {
        this.scanner = new MediaScanner(context.getApplicationContext());
        init();
    }

    /**
     * Initializes the library.
     */
    private void init()
    {
        Log.d(TAG, "Initializing MediaLibrary");

        List<Bundle> tracks = scanner.tracks();
        LongSparseArray<String> covers = scanner.albumCovers();

        for (Bundle trk : tracks) {
            Track t = MediaMetadata.trackFromMetadata(trk);
            if (t!=null) {
                t.setCoverArtUri(covers.get(t.getAlbumId()));
                trackCatalog.put(t.getTrackId(), t);
                allTracks.add(t);
            }
        }

        for (Bundle t : tracks) {
            if (artistCatalog.indexOfKey(t.getLong(MediaMetadata.METADATA_KEY_ARTIST_ID))>0) { continue; }
            Artist a = MediaMetadata.artistFromMetadata(t);
            if (a!=null) {
                artistCatalog.put(a.getArtistId(), a);
                allArtists.add(a);
            }
        }

        for (Bundle t : tracks) {
            if (albumCatalog.indexOfKey(t.getLong(MediaMetadata.METADATA_KEY_ALBUM_ID))>0) { continue; }
            Album a = MediaMetadata.albumFromMetadata(t);
            if (a!=null) {
                a.setCoverArtUri(covers.get(a.getAlbumId()));
                albumCatalog.put(a.getAlbumId(), a);
                allAlbums.add(a);
            }
        }

        Collections.sort(allTracks, new Comparator<Track>() {
            @Override
            public int compare(Track o1, Track o2) {
                return o1.getTrackTitle().compareTo(o2.getTrackTitle());
            }
        });

        Collections.sort(allArtists, new Comparator<Artist>() {
            @Override
            public int compare(Artist o1, Artist o2) {
                return o1.getArtistName().compareTo(o2.getArtistName());
            }
        });

        Collections.sort(allAlbums, new Comparator<Album>() {
            @Override
            public int compare(Album o1, Album o2) {
                return o1.getAlbumName().compareTo(o2.getAlbumName());
            }
        });

        for (Track t : allTracks) {
            Album alb = albumCatalog.get(t.getAlbumId());

            Set<Album> albumSet = albumsForArtist.get(t.getArtistId());
            if (albumSet==null) {
                albumSet = new HashSet<>();
                albumsForArtist.put(t.getArtistId(), albumSet);
            }
            albumSet.add(alb);

            Set<Track> trackSet = tracksForAlbum.get(t.getAlbumId());
            if (trackSet==null) {
                trackSet = new HashSet<>();
                tracksForAlbum.put(t.getAlbumId(), trackSet);
            }
            trackSet.add(t);
        }

        Log.d(TAG, "MediaLibrary initialized");
    }

    ///////////////////////////////////////////////////////////////////////////
    // ARTISTS
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns the list of all artists, ordered by name.
     * @return
     */
    public @NonNull List<Artist> allArtists()
    {
        return allArtists;
    }

    /**
     * Return the artist with the given id.
     *
     * @param artistId
     * @return
     */
    public @Nullable Artist artist(long artistId)
    {
        return artistCatalog.get(artistId);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ALBUMS
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Return the list of all albums, ordered by name.
     * @return
     */
    public @NonNull List<Album> allAlbums()
    {
        return allAlbums;
    }

    /**
     * Return all the albums for a given artist, ordered by title.
     *
     * @param artistId
     * @return
     */
    public @NonNull List<Album> albumsFromArtist(long artistId)
    {
        List<Album> result = new ArrayList<>();

        Set<Album> albums = albumsForArtist.get(artistId);
        if (albums!=null) {
            result.addAll(albums);
        }

        Collections.sort(result, MediaLibrarySort.albumComparatorByTitle());

        return result;
    }

    /**
     * Return the album with the given id.
     *
     * @param albumId
     * @return
     */
    public @Nullable Album album(long albumId)
    {
        return albumCatalog.get(albumId);
    }

    ///////////////////////////////////////////////////////////////////////////
    // TRACKS
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns all songs stored on the device, ordered by name.
     * @return
     */
    public @NonNull List<Track> allTracks()
    {
        return allTracks;
    }

    /**
     * Returns all tracks from a given album, ordered by track number.
     *
     * @param albumId
     * @return
     */
    public @NonNull List<Track> tracksFromAlbum(long albumId)
    {
        List<Track> result = new ArrayList<>();

        Set<Track> albumTracks = tracksForAlbum.get(albumId);
        if (albumTracks==null) { return result; }

        result.addAll(albumTracks);
        Collections.sort(result, MediaLibrarySort.trackComparatorByTrackNumber());

        return result;
    }

    /**
     * Returns all songs by a given artist, ordered by title.
     *
     * @param artistId
     * @return
     */
    public @NonNull List<Track> tracksFromArtist(long artistId)
    {
        List<Track> result = new ArrayList<>();

        Set<Album> albums = albumsForArtist.get(artistId);
        if (albums==null) { return result; }

        for (Album a : albums) {
            result.addAll(tracksFromAlbum(a.getAlbumId()));
        }

        Collections.sort(result, MediaLibrarySort.trackComparatorByTitle());

        return result;
    }

    /**
     * Return a list of tracks having the given ids.
     *
     * @param trackIds
     * @return
     */
    public @NonNull List<Track> tracksFromIdList(List<Long> trackIds)
    {
        List<Track> result = new ArrayList<>(trackIds.size());

        for (Long id : trackIds) {
            if (id==null) { continue; }
            Track t = trackCatalog.get(id);
            if (t==null) { continue; }
            result.add(t);
        }

        return result;
    }

    /**
     * Returns the track with the given id.
     *
     * @param trackId
     * @return
     */
    public @Nullable Track track(long trackId)
    {
        return trackCatalog.get(trackId);
    }
}
