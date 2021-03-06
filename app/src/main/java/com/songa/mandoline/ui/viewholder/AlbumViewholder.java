package com.songa.mandoline.ui.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songa.mandoline.R;
import com.songa.mandoline.artwork.Artwork;
import com.songa.mandoline.databinding.UiItemAlbumBinding;
import com.songa.mandoline.audio.entity.Album;
import com.songa.mandoline.ui.listener.ViewholderListener;

/**
 * Viewholder used to display an Album.
 */
public class AlbumViewholder extends RecyclerView.ViewHolder
{
    private final @NonNull UiItemAlbumBinding binding;

    private @Nullable ViewholderListener<AlbumViewholder> listener = null;

    private @Nullable Album album = null;

    private AlbumViewholder(@NonNull UiItemAlbumBinding binding)
    {
        super(binding.getRoot());
        this.binding = binding;
    }

    /**
     * Returns a viewholder already attached to its inflated view.
     *
     * @param parent
     * @return
     */
    public static @NonNull AlbumViewholder inflate(@NonNull ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final UiItemAlbumBinding binding = UiItemAlbumBinding.inflate(inflater, parent, false);
        final AlbumViewholder vh = new AlbumViewholder(binding);

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vh.listener!=null) {
                    vh.listener.onClick(vh, binding.coverFrame);
                }
            }
        });

        return vh;
    }

    /**
     * Displays the given album in this viewholder.
     * @param album
     */
    public void setup(@Nullable Album album)
    {
        this.album = album;

        if (album==null) {
            binding.name.setText("");
            binding.cover.setImageResource(R.drawable.placeholder_album);

        } else {
            binding.name.setText(album.getAlbumName());

            Artwork.load(album.getArtistName(), album.getAlbumName())
                    .placeholder(R.drawable.placeholder_album)
                    .error(R.drawable.placeholder_album)
                    .localArtworkFallback(album.getCoverArtUri())
                    .into(binding.cover);
        }
    }

    /**
     * Utility function. Binds the given viewholder with the given album if and only if it is an
     * instance of AlbumViewholder.
     *
     * @param holder
     * @param album
     */
    public static void bind(@NonNull RecyclerView.ViewHolder holder, @Nullable Album album)
    {
        if (holder instanceof AlbumViewholder) {
            AlbumViewholder vh = (AlbumViewholder) holder;
            vh.setup(album);
        }
    }

    /**
     * Sets up a click listener for this viewholder.
     *
     * @param listener
     * @return
     */
    public AlbumViewholder clickListener(@Nullable ViewholderListener<AlbumViewholder> listener)
    {
        this.listener = listener;
        return this;
    }

    /**
     * Returns the album bound to this viewholder.
     *
     * @return
     */
    @Nullable
    public Album getAlbum() {
        return album;
    }
}
