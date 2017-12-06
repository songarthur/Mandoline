package com.songa.mandoline.audio.library;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.LongSparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to fetch data from MediaStore.
 * In other words, fetches from the system the list of musics currently stored on the phone.
 */
public class MediaScanner
{
    private final Context context;

    public MediaScanner(@NonNull Context context)
    {
        this.context = context.getApplicationContext();
    }

    /**
     * Fetches all tracks from the {@link MediaStore}.
     *
     * @return
     */
    public @NonNull List<Bundle> tracks()
    {
        Uri storage = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] columns = new String[] {
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.DATA
        };

        String selection = null;

        String[] selectionArgs = null;

        String sortOrder = null;

        Cursor cursor = context.getContentResolver().query(
                storage,
                columns,
                selection,
                selectionArgs,
                sortOrder);

        List<Bundle> result = new ArrayList<>();
        if (cursor==null) { return result; }

        while (cursor.moveToNext()) {

            Bundle meta = new Bundle();
            meta.putLong(MediaMetadata.METADATA_KEY_ARTIST_ID, cursor.getInt(0));
            meta.putString(MediaMetadata.METADATA_KEY_ARTIST, cursor.getString(1));
            meta.putLong(MediaMetadata.METADATA_KEY_ALBUM_ID, cursor.getInt(2));
            meta.putString(MediaMetadata.METADATA_KEY_ALBUM, cursor.getString(3));
            meta.putLong(MediaMetadata.METADATA_KEY_TITLE_ID, cursor.getInt(4));
            meta.putString(MediaMetadata.METADATA_KEY_TITLE, cursor.getString(5));
            meta.putLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER, cursor.getInt(6));
            meta.putLong(MediaMetadata.METADATA_KEY_DURATION, cursor.getInt(7));
            meta.putLong(MediaMetadata.METADATA_KEY_YEAR, cursor.getInt(8));
            meta.putString(MediaMetadata.METADATA_KEY_MEDIA_MIME_TYPE, cursor.getString(9));
            meta.putString(MediaMetadata.METADATA_KEY_MEDIA_URI, cursor.getString(10));

            result.add(meta);
        }

        cursor.close();
        return result;
    }

    /**
     * Fetches all album covers from the {@link MediaStore}
     * @return a sparse array, each index corresponding to an albumId
     */
    public LongSparseArray<String> albumCovers()
    {
        LongSparseArray<String> result = new LongSparseArray<>();

        Uri storage = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

        String[] columns = new String[] {  MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART };
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        Cursor cursor = context.getContentResolver().query(
                storage,
                columns,
                selection,
                selectionArgs,
                sortOrder);

        if (cursor==null) { return result; }

        while (cursor.moveToNext()) {
            Long albumId = cursor.getLong(0);
            String coverUri = cursor.getString(1);
            if (coverUri!=null) {
                result.put(albumId, coverUri);
            }
        }

        cursor.close();
        return result;
    }

    /**
     * Fetches the cover for a given album.
     *
     * @param albumId
     * @return the cover or null if not found
     */
    public @Nullable String albumCover(long albumId)
    {
        Uri storage = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

        String[] columns = new String[] { MediaStore.Audio.Albums.ALBUM_ART };
        String selection = MediaStore.Audio.Albums._ID + " = ?";
        String[] selectionArgs = new String[] { Long.toString(albumId) };
        String sortOrder = null;

        Cursor cursor = context.getContentResolver().query(
                storage,
                columns,
                selection,
                selectionArgs,
                sortOrder);

        String result = cursor!=null && cursor.moveToNext() ? cursor.getString(0) : null;

        if (cursor!=null) { cursor.close(); }
        return result;
    }
}
