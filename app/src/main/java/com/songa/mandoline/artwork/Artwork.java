package com.songa.mandoline.artwork;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

/**
 * Class used by the application to load artworks related to music tracks, albums and artists.
 * The class also stores artwork related data like {@link Palette}.<br><br>
 *
 * The api of this class is somewhat similar to what {@link com.squareup.picasso.Picasso} offers.<br><br>
 *
 * What this class does is to try to find an artwork for an artist/album from a remote service like
 * LastFm first. If the remote API is unreachable, or fails to find an artwork, this class will then
 * fallback to a local solution if possible.
 */
public class Artwork
{
    /**
     * Singleton.
     * Executes artwork load requests.
     */
    private static @Nullable ArtworkJobExecutor executor = null;

    /**
     * Cache used to store each {@link Palette} for each artwork.
     * We assume this map will remain reasonably small even with a fairly big music library.
     */
    private static @NonNull Map<String,Palette> paletteCache = new HashMap<>();

    /**
     * Returns the executor in charge of executing artwork loads.
     *
     * @return The singleton instance of the executor
     */
    private static @NonNull ArtworkJobExecutor getExecutor()
    {
        if (executor==null) {
            executor = new ArtworkJobExecutor();
        }
        return executor;
    }

    /**
     * Creates a load request. Once the request has been setup, launch the request by calling {@link ArtworkRequest#into(ImageView)}<br><br>
     * To load an artists' artwork leave albumName null.<br>
     * To load an album's artwork both parameters must be filled.<br>
     *
     * @param artistName The artist's name
     * @param albumName The album's name
     * @return The load request
     */
    public static @NonNull ArtworkRequest load(@Nullable String artistName, @Nullable String albumName)
    {
        return new ArtworkRequest(getExecutor(), artistName, albumName);
    }

    /**
     * Caches a palette for a given artist or album.
     * To cache a palette for an artist, leave albumName as null.
     * To cache a palette for an album, fill both parameters.
     *
     * @param artistName
     * @param albumName
     * @param palette The palette to cache, or null to clear the value
     */
    public static void cachePalette(@Nullable String artistName, @Nullable String albumName, @Nullable Palette palette)
    {
        // with a convenient artistName and albumName this can be spoofed
        // we ignore it, nothing dangerous here
        paletteCache.put(artistName + " + " + albumName, palette);
    }

    /**
     * Returns the cached palette for a given artist or album.
     * To fetch the palette for an artist, leave albumName as null.
     * To fetch the palette for an album, fill both parameters.
     *
     * @param artistName
     * @param albumName
     * @return The cached palette if it exists or null
     */
    public static @Nullable Palette getPalette(@Nullable String artistName, @Nullable String albumName)
    {
        return paletteCache.get(artistName + " + " + albumName);
    }
}
