package com.songa.mandoline.artwork;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;

import java.util.HashMap;
import java.util.Map;

public class Artwork
{
    private static @Nullable ArtworkJobExecutor executor = null;

    private static @NonNull Map<String,Palette> paletteCache = new HashMap<>();

    private static @NonNull ArtworkJobExecutor getExecutor()
    {
        if (executor==null) {
            executor = new ArtworkJobExecutor();
        }
        return executor;
    }

    public static @NonNull ArtworkRequest load(@Nullable String artistName, @Nullable String albumName)
    {
        return new ArtworkRequest(getExecutor(), artistName, albumName);
    }

    public static void cachePalette(String artistName, String albumName, Palette palette)
    {
        // with a convenient artistName and albumName this can be spoofed
        // we ignore it, nothing dangerous here
        paletteCache.put(artistName + " + " + albumName, palette);
    }

    public static @Nullable Palette getPalette(String artistName, String albumName)
    {
        return paletteCache.get(artistName + " + " + albumName);
    }
}
