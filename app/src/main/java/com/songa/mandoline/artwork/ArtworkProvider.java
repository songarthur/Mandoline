package com.songa.mandoline.artwork;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;

public interface ArtworkProvider
{
    @Nullable
    ArtworkResult getArtworkUrl(@Nullable String artistName,
                                @Nullable String albumName);

    void loadArtwork(@NonNull ArtworkResult artwork,
                     @NonNull ImageView target,
                     @DrawableRes @Nullable Integer placeholderDrawable,
                     @DrawableRes @Nullable Integer errorDrawable,
                     @Nullable Palette.PaletteAsyncListener paletteListener);

    void cancelLoad(@NonNull ImageView target);
}
