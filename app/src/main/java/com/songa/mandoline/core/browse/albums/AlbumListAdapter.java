package com.songa.mandoline.core.browse.albums;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.songa.mandoline.audio.entity.Album;
import com.songa.mandoline.ui.viewholder.AlbumViewholder;
import com.songa.mandoline.ui.listener.ItemEventListener;
import com.songa.mandoline.ui.listener.ViewholderListener;

import java.util.ArrayList;
import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumViewholder> implements ViewholderListener<AlbumViewholder>
{
    private final List<Album> albums = new ArrayList<>();

    private @Nullable ItemEventListener<Album> listener = null;

    @Override
    public AlbumViewholder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return AlbumViewholder.inflate(parent).clickListener(this);
    }

    @Override
    public void onBindViewHolder(AlbumViewholder holder, int position)
    {
        if (position>= albums.size()) {
            holder.setup(null);
            return;
        }

        holder.setup(albums.get(position));
    }

    @Override
    public int getItemCount()
    {
        return albums.size();
    }

    @Override
    public void onClick(@NonNull AlbumViewholder holder, @NonNull View view)
    {
        if (listener!=null && holder.getAlbum()!=null) {
            listener.onClick(holder.getAlbum(), view);
        }
    }

    public void addAll(@NonNull List<Album> items, boolean clearBefore)
    {
        if (clearBefore) { albums.clear(); }
        albums.addAll(items);
        notifyDataSetChanged();
    }

    public void setListener(@Nullable ItemEventListener<Album> listener)
    {
        this.listener = listener;
    }
}
