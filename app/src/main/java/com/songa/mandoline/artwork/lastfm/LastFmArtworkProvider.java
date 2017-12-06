package com.songa.mandoline.artwork.lastfm;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.widget.ImageView;

import com.songa.mandoline.artwork.Artwork;
import com.songa.mandoline.artwork.ArtworkProvider;
import com.songa.mandoline.artwork.ArtworkResult;
import com.songa.mandoline.artwork.lastfm.entity.LastFmAlbumInfo;
import com.songa.mandoline.artwork.lastfm.entity.LastFmApiWrapper;
import com.songa.mandoline.artwork.lastfm.entity.LastFmArtistInfo;
import com.songa.mandoline.artwork.lastfm.entity.LastFmImage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import retrofit2.Response;

/**
 * Artwork Provider for LastFM.
 */
public class LastFmArtworkProvider implements ArtworkProvider
{
    @Override
    public void loadArtwork(@NonNull final ArtworkResult artwork,
                            @NonNull final ImageView target,
                            @DrawableRes @Nullable final Integer placeholderDrawable,
                            @DrawableRes @Nullable final Integer errorDrawable,
                            @Nullable final Palette.PaletteAsyncListener paletteListener)
    {
        if (artwork.getUrl()==null || artwork.getUrl().isEmpty()) { return; }

        // picasso must be called from the main thread
        Handler mainThread = new Handler(Looper.getMainLooper());
        mainThread.post(new Runnable() {
            @Override
            public void run() {

                Picasso picasso = LastFm.getImageLoader();
                RequestCreator r = artwork.isLocal() ? picasso.load(new File(artwork.getUrl())) : picasso.load(artwork.getUrl());

                if (placeholderDrawable!=null) { r.placeholder(placeholderDrawable); }
                if (errorDrawable!=null) { r.error(errorDrawable); }

                // to generate a Palette we need a Bitmap
                // we use Transform as a way to access the downloaded artwork's bitmap
                r.transform(new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        if (paletteListener!=null) {
                            Palette.from(source).generate(paletteListener);
                        }
                        return source;
                    }

                    @Override
                    public String key() { return "palette"; }
                });

                r.into(target);
            }
        });
    }

    @Override
    public void cancelLoad(@NonNull ImageView target)
    {
        LastFm.getImageLoader().cancelRequest(target);
    }

    @Nullable
    @Override
    public ArtworkResult getArtworkUrl(@Nullable String artistName, @Nullable String albumName)
    {
        if (artistName==null) {
            return null;
        } if (albumName==null) {
            return getArtistArtworkUrl(artistName);
        } else {
            return getAlbumArtworkUrl(artistName, albumName);
        }
    }

    /**
     * Fetches an artwork for a given artist.
     *
     * @param artistName
     * @return
     */
    @Nullable
    public ArtworkResult getArtistArtworkUrl(@NonNull String artistName)
    {
        try {
            LastFmApi api = LastFm.getApi();
            Response<LastFmApiWrapper> response = api.artistInfo(artistName).execute();

            if (!response.isSuccessful()) {
                return null;
            }

            LastFmApiWrapper wrapper = response.body();
            LastFmArtistInfo info = wrapper!=null ? wrapper.getArtistInfo() : null;
            List<LastFmImage> images = info!=null ? info.getImages() : null;

            LastFmImage bestImage = getBestImage(images);
            String bestImageUrl = bestImage!=null ? bestImage.getUrl() : null;

            return bestImageUrl!=null && !bestImageUrl.isEmpty() ? new ArtworkResult(bestImage.getUrl(), false) : null;

        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Fetches an artwork for a given album.
     *
     * @param artistName
     * @param albumName
     * @return
     */
    @Nullable
    public ArtworkResult getAlbumArtworkUrl(@NonNull String artistName, @NonNull String albumName)
    {
        try {
            LastFmApi api = LastFm.getApi();
            Response<LastFmApiWrapper> response = api.albumInfo(artistName, albumName).execute();

            if (!response.isSuccessful()) {
                return null;
            }

            LastFmApiWrapper wrapper = response.body();
            LastFmAlbumInfo info = wrapper!=null ? wrapper.getAlbumInfo() : null;
            List<LastFmImage> images = info!=null ? info.getImages() : null;

            LastFmImage bestImage = getBestImage(images);
            String bestImageUrl = bestImage!=null ? bestImage.getUrl() : null;

            return bestImageUrl!=null && !bestImageUrl.isEmpty() ? new ArtworkResult(bestImage.getUrl(), false) : null;

        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Returns the biggest image among a list of images from LastFM.
     *
     * @param images the list of images to process
     * @return
     */
    @Nullable
    private LastFmImage getBestImage(@Nullable List<LastFmImage> images)
    {
        if (images==null || images.size()==0) { return null; }

        LastFmImage choice = images.get(images.size()-1);
        int currentSize = -1;

        for (LastFmImage img : images) {

            if (img==null || img.getSize()==null || img.getUrl()==null || img.getUrl().isEmpty()) {
                continue;
            }

            switch (img.getSize()) {
                case "small":
                    if (currentSize<0) {
                        currentSize = 0;
                        choice = img;
                    }
                    break;
                case "medium":
                    if (currentSize<1) {
                        currentSize = 1;
                        choice = img;
                    }
                    break;
                case "large":
                    if (currentSize<2) {
                        currentSize = 2;
                        choice = img;
                    }
                    break;
                case "extralarge":
                    if (currentSize<3) {
                        currentSize = 3;
                        choice = img;
                    }
                    break;
                case "mega":
                    if (currentSize<4) {
                        currentSize = 4;
                        choice = img;
                    }
                    break;
            }
        }

        return choice;
    }
}
