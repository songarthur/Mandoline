package com.songa.mandoline.core.player;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songa.mandoline.R;
import com.songa.mandoline.artwork.Artwork;
import com.songa.mandoline.databinding.UiItemPlayerTrackBinding;
import com.songa.mandoline.audio.entity.Track;

public class PlaylistTrackFragment extends Fragment
{
    private UiItemPlayerTrackBinding binding;
    private @Nullable Track track;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = UiItemPlayerTrackBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        if (track==null) {
            binding.cover.setImageResource(R.drawable.placeholder_album);

        } else {

            Artwork.load(track.getArtistName(), track.getAlbumName())
                    .placeholder(R.drawable.placeholder_album)
                    .error(R.drawable.placeholder_album)
                    .localArtworkFallback(track.getCoverArtUri())
                    .into(binding.cover);

            /*
            if (track.getCoverArtUri() != null) {
                File coverFile = new File(track.getCoverArtUri());
                Picasso.with(getContext())
                        .load(coverFile)
                        .placeholder(R.drawable.placeholder_album)
                        .error(R.drawable.placeholder_album)
                        .into(binding.cover);

            } else {
                binding.cover.setImageResource(R.drawable.placeholder_album);
            }*/
        }
    }

    public void setTrack(@Nullable Track track) {
        this.track = track;
    }
}
