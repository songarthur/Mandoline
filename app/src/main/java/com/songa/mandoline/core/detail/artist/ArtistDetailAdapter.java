package com.songa.mandoline.core.detail.artist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.songa.mandoline.audio.entity.Album;
import com.songa.mandoline.audio.entity.Artist;
import com.songa.mandoline.ui.viewholder.AlbumViewholder;
import com.songa.mandoline.ui.listener.ItemEventListener;
import com.songa.mandoline.ui.listener.ViewholderListener;

import java.util.ArrayList;
import java.util.List;

public class ArtistDetailAdapter extends RecyclerView.Adapter implements ViewholderListener<AlbumViewholder>
{
    private @NonNull Artist artist;
    private final @NonNull List<Album> albums = new ArrayList<>();

    private @Nullable ItemEventListener<Album> listener = null;

    public ArtistDetailAdapter(@NonNull Artist artist)
    {
        this.artist = artist;
    }

    @Override
    public int getItemViewType(int position)
    {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType) {
            //case 0 : return ArtistHeaderViewholder.inflate(parent);
            default : return AlbumViewholder.inflate(parent).clickListener(this);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        /*
        switch (position) {
            case 0:
                ArtistHeaderViewholder.bind(holder, artist);
                break;
            default:
                if (position-1<allAlbums.size()) {
                    AlbumViewholder.bind(holder, allAlbums.get(position-1));
                } else {
                    AlbumViewholder.bind(holder, null);
                }
        }
        */

        if (position<albums.size()) {
            AlbumViewholder.bind(holder, albums.get(position));
        } else {
            AlbumViewholder.bind(holder, null);
        }
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


    public void setListener(@Nullable ItemEventListener<Album> listener)
    {
        this.listener = listener;
    }

    @UiThread
    public void addAlbums(@NonNull List<Album> items, boolean clearBefore)
    {
        if (clearBefore) { this.albums.clear(); }
        this.albums.addAll(items);
        notifyDataSetChanged();
    }
}
