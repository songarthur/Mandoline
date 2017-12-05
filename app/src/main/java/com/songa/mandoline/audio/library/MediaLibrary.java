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


    public static Single<MediaLibrary> getLibraryProvider(@NonNull final Context context)
    {
        return Single
                .create(new SingleOnSubscribe<MediaLibrary>() {
            @Override
            public void subscribe(SingleEmitter<MediaLibrary> e) throws Exception {e.onSuccess(getInstance(context));}
        })
                .subscribeOn(Schedulers.io());
    }

    public static @Nullable MediaLibrary tryToGetInstance()
    {
        return instance;
    }

    @WorkerThread
    private static synchronized MediaLibrary getInstance(@NonNull Context context)
    {
        if (instance==null) { instance = new MediaLibrary(context.getApplicationContext()); }
        return instance;

    }

    @WorkerThread
    private MediaLibrary(@NonNull Context context)
    {
        this.scanner = new MediaScanner(context.getApplicationContext());
        init();
    }

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

    public @NonNull List<Artist> allArtists()
    {
        return allArtists;
    }

    public @Nullable Artist artist(long artistId)
    {
        return artistCatalog.get(artistId);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ALBUMS
    ///////////////////////////////////////////////////////////////////////////

    public @NonNull List<Album> allAlbums()
    {
        return allAlbums;
    }

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

    public @Nullable Album album(long albumId)
    {
        return albumCatalog.get(albumId);
    }

    ///////////////////////////////////////////////////////////////////////////
    // TRACKS
    ///////////////////////////////////////////////////////////////////////////

    public @NonNull List<Track> allTracks()
    {
        return allTracks;
    }

    public @NonNull List<Track> tracksFromAlbum(long albumId)
    {
        List<Track> result = new ArrayList<>();

        Set<Track> albumTracks = tracksForAlbum.get(albumId);
        if (albumTracks==null) { return result; }

        result.addAll(albumTracks);
        Collections.sort(result, MediaLibrarySort.trackComparatorByTrackNumber());

        return result;
    }

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

    public @Nullable Track track(long trackId)
    {
        return trackCatalog.get(trackId);
    }
}
