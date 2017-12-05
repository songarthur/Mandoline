package com.songa.mandoline.ui.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.songa.mandoline.R;
import com.songa.mandoline.databinding.UiHeaderAlbumBinding;
import com.songa.mandoline.audio.entity.Album;
import com.squareup.picasso.Picasso;

import java.io.File;

public class AlbumHeaderViewHolder extends RecyclerView.ViewHolder
{
    private final UiHeaderAlbumBinding binding;

    public AlbumHeaderViewHolder(@NonNull UiHeaderAlbumBinding binding)
    {
        super(binding.getRoot());
        this.binding = binding;
    }

    public static @NonNull
    AlbumHeaderViewHolder inflate(@NonNull ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        UiHeaderAlbumBinding binding = UiHeaderAlbumBinding.inflate(inflater, parent, false);
        return new AlbumHeaderViewHolder(binding);
    }

    public void setup(@Nullable Album album)
    {
        if (album==null) {
            binding.name.setText(album.getAlbumName());
            binding.cover.setImageResource(R.drawable.placeholder_album);

        } else {
            binding.name.setText(album.getAlbumName());

            Picasso.with(binding.getRoot().getContext()).cancelRequest(binding.cover);

            if (album.getCoverArtUri()!=null) {
                binding.cover.setBackground(null);
                Picasso.with(binding.getRoot().getContext())
                        .load(new File(album.getCoverArtUri()))
                        .placeholder(R.drawable.placeholder_album)
                        .error(R.drawable.placeholder_album)
                        .into(binding.cover);
            } else {
                binding.cover.setImageResource(R.drawable.placeholder_album);
            }
        }
    }

    public static void bind(@NonNull RecyclerView.ViewHolder holder, @Nullable Album album)
    {
        if (holder instanceof AlbumHeaderViewHolder) {
            AlbumHeaderViewHolder vh = (AlbumHeaderViewHolder) holder;
            vh.setup(album);
        }
    }
}
