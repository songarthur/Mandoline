package com.songa.mandoline.ui.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.songa.mandoline.R;
import com.songa.mandoline.databinding.UiHeaderArtistBinding;
import com.songa.mandoline.audio.entity.Artist;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ArtistHeaderViewholder extends RecyclerView.ViewHolder
{
    private final UiHeaderArtistBinding binding;

    public ArtistHeaderViewholder(@NonNull UiHeaderArtistBinding binding)
    {
        super(binding.getRoot());
        this.binding = binding;
    }

    public static @NonNull ArtistHeaderViewholder inflate(@NonNull ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        UiHeaderArtistBinding binding = UiHeaderArtistBinding.inflate(inflater, parent, false);
        return new ArtistHeaderViewholder(binding);
    }

    public void setup(@Nullable Artist artist)
    {
        if (artist==null) {
            binding.name.setText("");
            binding.cover.setImageResource(R.drawable.placeholder_artist);

        } else {
            binding.name.setText(artist.getArtistName());

            Picasso.with(binding.getRoot().getContext()).cancelRequest(binding.cover);

            if (artist.getCoverArtUri()!=null) {
                Picasso.with(binding.getRoot().getContext())
                        .load(new File(artist.getCoverArtUri()))
                        .placeholder(R.drawable.placeholder_artist)
                        .error(R.drawable.placeholder_artist)
                        .into(binding.cover);

            } else {
                binding.cover.setImageResource(R.drawable.placeholder_artist);
            }
        }
    }

    public static void bind(@NonNull RecyclerView.ViewHolder holder, @Nullable Artist artist)
    {
        if (holder instanceof ArtistHeaderViewholder) {
            ArtistHeaderViewholder vh = (ArtistHeaderViewholder) holder;
            vh.setup(artist);
        }
    }
}
