package com.songa.mandoline.core.player;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songa.mandoline.R;
import com.songa.mandoline.artwork.Artwork;
import com.songa.mandoline.databinding.UiItemPlayerTrackBinding;
import com.songa.mandoline.audio.entity.Track;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PlaylistPagerAdapter extends PagerAdapter
{
    private final @NonNull List<Track> playlist = new ArrayList<>();

    @Override
    public int getCount()
    {
        return playlist.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        if (object==null || !(object instanceof UiItemPlayerTrackBinding)) { return false; }

        UiItemPlayerTrackBinding binding = (UiItemPlayerTrackBinding) object;
        return binding.getRoot()==view;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position)
    {

        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        UiItemPlayerTrackBinding binding = UiItemPlayerTrackBinding.inflate(inflater, container, true);

        Picasso.with(container.getContext()).cancelRequest(binding.cover);

        if (position<0 || position>=playlist.size()) {
            binding.cover.setBackgroundResource(R.color.colorPrimary);
            binding.cover.setImageResource(R.drawable.placeholder_album);

        } else if (playlist.get(position)!=null) {
            Track track = playlist.get(position);
            Artwork.load(track.getArtistName(), track.getAlbumName())
                    .placeholder(R.drawable.placeholder_album)
                    .error(R.drawable.placeholder_album)
                    .localArtworkFallback(track.getCoverArtUri())
                    .into(binding.cover);
        }

        return binding;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        if (object==null || !(object instanceof UiItemPlayerTrackBinding)) { return; }

        UiItemPlayerTrackBinding binding = (UiItemPlayerTrackBinding) object;
        container.removeView(binding.getRoot());
    }

    public void setPlaylist(@NonNull List<Track> playlist)
    {
        this.playlist.clear();
        this.playlist.addAll(playlist);
        notifyDataSetChanged();
    }

    public @Nullable Track getTrack(int position)
    {
        return position>=0 && position<playlist.size() ? playlist.get(position) : null;
    }
}
