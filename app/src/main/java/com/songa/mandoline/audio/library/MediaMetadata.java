package com.songa.mandoline.audio.library;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.songa.mandoline.audio.entity.Album;
import com.songa.mandoline.audio.entity.Artist;
import com.songa.mandoline.audio.entity.Track;

/**
 * Class used to convert data extracted from the {@link android.provider.MediaStore} and delivered
 * by the {@link MediaScanner}.
 */
public class MediaMetadata
{
    public static final String METADATA_KEY_TITLE = "android.media.metadata.TITLE";
    public static final String METADATA_KEY_TITLE_ID = "android.media.metadata.TITLE_ID";

    public static final String METADATA_KEY_ARTIST = "android.media.metadata.ARTIST";
    public static final String METADATA_KEY_ARTIST_ID = "android.media.metadata.ARTIST_ID";

    public static final String METADATA_KEY_ALBUM = "android.media.metadata.ALBUM";
    public static final String METADATA_KEY_ALBUM_ID = "android.media.metadata.ALBUM_ID";

    public static final String METADATA_KEY_DURATION = "android.media.metadata.DURATION";
    public static final String METADATA_KEY_YEAR = "android.media.metadata.YEAR";
    public static final String METADATA_KEY_GENRE = "android.media.metadata.GENRE";
    public static final String METADATA_KEY_TRACK_NUMBER = "android.media.metadata.TRACK_NUMBER";
    public static final String METADATA_KEY_NUM_TRACKS = "android.media.metadata.NUM_TRACKS";
    public static final String METADATA_KEY_NUM_DISCS = "android.media.metadata.NUM_DISCS";
    public static final String METADATA_KEY_DISC_NUMBER = "android.media.metadata.DISC_NUMBER";

    public static final String METADATA_KEY_AUTHOR = "android.media.metadata.AUTHOR";
    public static final String METADATA_KEY_WRITER = "android.media.metadata.WRITER";
    public static final String METADATA_KEY_COMPOSER = "android.media.metadata.COMPOSER";

    public static final String METADATA_KEY_ART = "android.media.metadata.ART";
    public static final String METADATA_KEY_ART_URI = "android.media.metadata.ART_URI";
    public static final String METADATA_KEY_ALBUM_ART = "android.media.metadata.ALBUM_ART";
    public static final String METADATA_KEY_ALBUM_ART_URI = "android.media.metadata.ALBUM_ART_URI";

    public static final String METADATA_KEY_MEDIA_MIME_TYPE = "android.media.metadata.MEDIA_MIME_TYPE";
    public static final String METADATA_KEY_MEDIA_URI = "android.media.metadata.MEDIA_URI";

    private MediaMetadata() {}

    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Extracts a tracks from metadata.
     *
     * @param metadata
     * @return The extracted track or null if the metadata are not valid
     */
    public static @Nullable Track trackFromMetadata(@NonNull Bundle metadata)
    {
        long trackId = metadata.getLong(MediaMetadata.METADATA_KEY_TITLE_ID,-1);
        String trackTitle = metadata.getString(MediaMetadata.METADATA_KEY_TITLE);
        long artistId  = metadata.getLong(MediaMetadata.METADATA_KEY_ARTIST_ID, -1);
        String artistName = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST);
        long albumId = metadata.getLong(MediaMetadata.METADATA_KEY_ALBUM_ID, -1);
        String albumName = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM);
        long trackNumber = metadata.getLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER, -1);
        long trackDuration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION, -1);
        long trackYear = metadata.getLong(MediaMetadata.METADATA_KEY_YEAR, -1);
        String trackMime = metadata.getString(MediaMetadata.METADATA_KEY_MEDIA_MIME_TYPE);
        String trackUri = metadata.getString(MediaMetadata.METADATA_KEY_MEDIA_URI);

        if (trackId<1 || trackTitle==null) { return null; }

        return new Track(
                trackId, trackTitle,
                artistId, artistName,
                albumId, albumName,
                trackNumber,
                trackDuration,
                trackYear,
                trackMime,
                trackUri);
    }

    /**
     * Extracts an artist from metadata info.
     *
     * @param metadata
     * @return the extracted artists or null if the metadata is not valid
     */
    public static @Nullable Artist artistFromMetadata(@NonNull Bundle metadata)
    {
        long artistId = metadata.getLong(MediaMetadata.METADATA_KEY_ARTIST_ID, -1);
        String artistName = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST);

        if (artistId<1 || artistName==null) { return null; }

        return new Artist(artistId, artistName);
    }

    /**
     * Extracts an album from metadata info.
     *
     * @param meta
     * @return the extracted album or null if the metadata is not valid
     */
    public static @Nullable Album albumFromMetadata(@NonNull Bundle meta)
    {
        long albumId = meta.getLong(MediaMetadata.METADATA_KEY_ALBUM_ID, -1);
        String albumName = meta.getString(MediaMetadata.METADATA_KEY_ALBUM);
        long artistId = meta.getLong(MediaMetadata.METADATA_KEY_ARTIST_ID, -1);
        String artistName = meta.getString(MediaMetadata.METADATA_KEY_ARTIST);

        if (albumId<1 || albumName==null) { return null; }

        return new Album(albumId, albumName, artistId, artistName, null);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Converts a track into a metadata bundle.
     *
     * @param track
     * @return
     */
    public static Bundle extractMetadata(@NonNull Track track)
    {
        Bundle metadata = new Bundle();
        metadata.putLong(MediaMetadata.METADATA_KEY_TITLE_ID, track.getTrackId());
        metadata.putString(MediaMetadata.METADATA_KEY_TITLE, track.getTrackTitle());
        metadata.putLong(MediaMetadata.METADATA_KEY_ARTIST_ID, track.getArtistId());
        metadata.putString(MediaMetadata.METADATA_KEY_ARTIST, track.getArtistName());
        metadata.putLong(MediaMetadata.METADATA_KEY_ALBUM_ID, track.getAlbumId());
        metadata.putString(MediaMetadata.METADATA_KEY_ALBUM, track.getAlbumName());
        metadata.putLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER, track.getTrackNumber());
        metadata.putLong(MediaMetadata.METADATA_KEY_DURATION, track.getTrackDuration());
        metadata.putLong(MediaMetadata.METADATA_KEY_YEAR, track.getTrackYear());
        metadata.putString(MediaMetadata.METADATA_KEY_MEDIA_MIME_TYPE, track.getTrackMimeType());
        metadata.putString(MediaMetadata.METADATA_KEY_MEDIA_URI, track.getTrackUri());

        return metadata;
    }

    /**
     * Converts an artist into a metadata bundle.
     *
     * @param artist
     * @return
     */
    public static Bundle extractMetadata(@NonNull Artist artist)
    {
        Bundle metadata = new Bundle();
        metadata.putLong(MediaMetadata.METADATA_KEY_ARTIST_ID, artist.getArtistId());
        metadata.putString(MediaMetadata.METADATA_KEY_ARTIST, artist.getArtistName());

        return metadata;
    }

    /**
     * Converts an album into a metadata bundle.
     *
     * @param album
     * @return
     */
    public static Bundle extractMetadata(@NonNull Album album)
    {
        Bundle metadata = new Bundle();
        metadata.putLong(MediaMetadata.METADATA_KEY_ARTIST_ID, album.getArtistId());
        metadata.putString(MediaMetadata.METADATA_KEY_ARTIST, album.getArtistName());
        metadata.putLong(MediaMetadata.METADATA_KEY_ALBUM_ID, album.getAlbumId());
        metadata.putString(MediaMetadata.METADATA_KEY_ALBUM, album.getAlbumName());

        return metadata;
    }

}
