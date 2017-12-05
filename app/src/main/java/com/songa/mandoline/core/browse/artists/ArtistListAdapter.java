package com.songa.mandoline.core.browse.artists;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.songa.mandoline.audio.entity.Artist;
import com.songa.mandoline.ui.viewholder.ArtistViewholder;
import com.songa.mandoline.ui.listener.ItemEventListener;
import com.songa.mandoline.ui.listener.ViewholderListener;

import java.util.ArrayList;
import java.util.List;

public class ArtistListAdapter extends RecyclerView.Adapter<ArtistViewholder> implements ViewholderListener<ArtistViewholder>
{
    private final List<Artist> artists = new ArrayList<>();

    private @Nullable ItemEventListener<Artist> listener;

    @Override
    public ArtistViewholder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return ArtistViewholder.inflate(parent).clickListener(this);
    }

    @Override
    public void onBindViewHolder(ArtistViewholder holder, int position)
    {
        if (position>=artists.size()) {
            holder.setup(null);
            return;
        }

        holder.setup(artists.get(position));
    }

    @Override
    public int getItemCount()
    {
        return artists.size();
    }

    @Override
    public void onClick(@NonNull ArtistViewholder holder, @NonNull View view)
    {
        if (listener==null || holder.getArtist()==null) { return; }
        listener.onClick(holder.getArtist(), view);
    }

    public void addAll(@NonNull List<Artist> items, boolean clearBefore)
    {
        if (clearBefore) { artists.clear(); }
        artists.addAll(items);
        notifyDataSetChanged();
    }

    public void setListener(@Nullable ItemEventListener<Artist> listener)
    {
        this.listener = listener;
    }
}
