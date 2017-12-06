package com.songa.mandoline.ui.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songa.mandoline.R;
import com.songa.mandoline.artwork.Artwork;
import com.songa.mandoline.databinding.UiItemArtistBinding;
import com.songa.mandoline.audio.entity.Artist;
import com.songa.mandoline.ui.listener.ViewholderListener;

/**
 * Viewholder used to display an Artist.
 */
public class ArtistViewholder extends RecyclerView.ViewHolder
{
    private final @NonNull UiItemArtistBinding binding;
    private @Nullable ViewholderListener<ArtistViewholder> listener = null;

    private @Nullable Artist artist = null;

    private ArtistViewholder(@NonNull UiItemArtistBinding binding)
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
    public static @NonNull ArtistViewholder inflate(@NonNull ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final UiItemArtistBinding binding = UiItemArtistBinding.inflate(inflater, parent, false);

        final ArtistViewholder vh = new ArtistViewholder(binding);

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vh.listener==null || vh.getArtist()==null) { return; }
                vh.listener.onClick(vh, binding.coverFrame);
            }
        });

        return vh;
    }

    /**
     * Binds the given artist to this viewholder.
     *
     * @param artist
     */
    public void setup(@Nullable Artist artist)
    {
        this.artist = artist;

        if (artist==null) {
            binding.name.setText("");
            binding.cover.setImageResource(R.drawable.placeholder_artist);

        } else {
            binding.name.setText(artist.getArtistName());
            Artwork.load(artist.getArtistName(), null)
                    .placeholder(R.drawable.placeholder_artist)
                    .error(R.drawable.placeholder_artist)
                    .into(binding.cover);
        }
    }

    /**
     * Sets a click listener for this viewholder.
     * @param listener
     * @return
     */
    public ArtistViewholder clickListener(@Nullable ViewholderListener<ArtistViewholder> listener)
    {
        this.listener = listener;
        return this;
    }

    /**
     * Returns the artist bound to this viewholder.
     *
     * @return
     */
    @Nullable
    public Artist getArtist()
    {
        return artist;
    }
}
