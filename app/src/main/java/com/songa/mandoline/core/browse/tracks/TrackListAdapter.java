package com.songa.mandoline.core.browse.tracks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.songa.mandoline.audio.entity.Track;
import com.songa.mandoline.ui.viewholder.TrackAltViewholder;
import com.songa.mandoline.ui.listener.ItemEventListener;
import com.songa.mandoline.ui.listener.ViewholderListener;

import java.util.ArrayList;
import java.util.List;

public class TrackListAdapter extends RecyclerView.Adapter<TrackAltViewholder> implements ViewholderListener<TrackAltViewholder>
{
    private final List<Track> tracks = new ArrayList<>();

    private @Nullable ItemEventListener<Track> listener = null;

    @Override
    public TrackAltViewholder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return TrackAltViewholder.inflate(parent).clickListener(this);
    }

    @Override
    public void onBindViewHolder(TrackAltViewholder holder, int position)
    {
        if (position>= tracks.size()) {
            holder.setup(null);
            return;
        }

        holder.setup(tracks.get(position));
    }

    @Override
    public int getItemCount()
    {
        return tracks.size();
    }

    @Override
    public void onClick(@NonNull TrackAltViewholder holder, @NonNull View view)
    {
        if (listener!=null && holder.getTrack()!=null) {
            listener.onClick(holder.getTrack(), view);
        }
    }

    public void addAll(@NonNull List<Track> items, boolean clearBefore)
    {
        if (clearBefore) { this.tracks.clear(); }
        this.tracks.addAll(items);
        notifyDataSetChanged();
    }

    public void setListener(@Nullable ItemEventListener<Track> listener)
    {
        this.listener = listener;
    }
}
