package com.songa.mandoline.artwork;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;

/**
 * A generic interface for artwork providers.
 */
public interface ArtworkProvider
{
    /**
     * For a given artist/album, returns the url of its corresponding artwork.
     * To fetch the artwork url of an artist leave albumName null.
     * To fetch the artwork url of an album both parameters must be filled..
     *
     * @param artistName
     * @param albumName
     * @return
     */
    @Nullable
    ArtworkResult getArtworkUrl(@Nullable String artistName,
                                @Nullable String albumName);

    /**
     * Loads an artwork into the given imageView.
     * Should also compute and send the artwork's palette to the listener if not null.
     *
     * @param artwork The artwork to load
     * @param target Where to load the artwork
     * @param placeholderDrawable The placeholder to be used while loading
     * @param errorDrawable The picture to be used in case of an error
     * @param paletteListener If you want to also fetch the artwork's corresponding palette
     */
    void loadArtwork(@NonNull ArtworkResult artwork,
                     @NonNull ImageView target,
                     @DrawableRes @Nullable Integer placeholderDrawable,
                     @DrawableRes @Nullable Integer errorDrawable,
                     @Nullable Palette.PaletteAsyncListener paletteListener);

    /**
     * Cancel any artwork load scheduled for the given imageView.
     * @param target
     */
    void cancelLoad(@NonNull ImageView target);
}
