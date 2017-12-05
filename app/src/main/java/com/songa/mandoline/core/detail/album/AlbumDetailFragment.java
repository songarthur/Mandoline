package com.songa.mandoline.core.detail.album;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songa.mandoline.R;
import com.songa.mandoline.artwork.Artwork;
import com.songa.mandoline.core.base.BaseFragment;
import com.songa.mandoline.databinding.FragmentDetailsAlbumBinding;
import com.songa.mandoline.audio.entity.Album;
import com.songa.mandoline.audio.entity.Track;
import com.songa.mandoline.audio.player.PlaybackMode;
import com.songa.mandoline.audio.library.PlaylistResolver;
import com.songa.mandoline.ui.listener.ItemEventListener;

import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class AlbumDetailFragment extends BaseFragment implements ItemEventListener<Track>, View.OnClickListener
{
    private static final String TAG = AlbumDetailFragment.class.getSimpleName();

    private long albumId = -1;

    private FragmentDetailsAlbumBinding binding;
    private LinearLayoutManager layoutManager;
    private AlbumDetailAdapter recyclerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null) {
            albumId = savedInstanceState.getLong("albumId", -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentDetailsAlbumBinding.inflate(inflater, container, false);

        binding.actionClose.setOnClickListener(this);
        binding.actionShuffle.setOnClickListener(this);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recycler.setLayoutManager(layoutManager);
        binding.recycler.setItemAnimator(new SlideInUpAnimator());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (getMediaLibrary()!=null) {
            onMediaLibraryInitialized();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putLong("albumId", albumId);
    }

    @Override
    public void onMediaLibraryInitialized()
    {
        if (getMediaLibrary()==null) { return; }

        Album album = getMediaLibrary().album(albumId);
        if (album==null) { return; }

        binding.albumName.setText(album.getAlbumName());

        Artwork.load(album.getArtistName(), album.getAlbumName())
                .placeholder(R.drawable.placeholder_album)
                .error(R.drawable.placeholder_album)
                .localArtworkFallback(album.getCoverArtUri())
                .into(binding.cover);

        recyclerAdapter = new AlbumDetailAdapter(album);
        recyclerAdapter.setListener(this);
        binding.recycler.setAdapter(recyclerAdapter);

        List<Track> tracks = getMediaLibrary().tracksFromAlbum(album.getAlbumId());
        recyclerAdapter.addTracks(tracks, true);

        /* ******************************** NASTY HACK ALERT ***************************************
         *
         * This is to prevent the coordinator from scrolling beyond the content of the recycler.
         * Without this the coordinator scrolls all the way up and if the recycler doesn't have enough
         * items to fill the screen, it will leave an empty space at the bottom.
         *
         * The CollapsingToolbarLayout is tied to the nearest Toolbar. If this toolbar reaches the top
         * of the CoordinatorLayout because of a scroll, the coordinator prevents any further scrolling.
         * All scroll events are then forwarded to the recycler.
         *
         * Therefore we introduce a Toolbar as a padding view, invisible, and sitting at the bottom
         * of the CollapsingToolbarLayout. And its height is set as the height of the coordinator
         * minus the height of the recycler (or 0 if negative).
         * This way as soon as we reach the end of the recycler content, the coordinator will stop
         * scrolling.
         *
         * Doesn't sound that clear but oh well...
         *
         */
        binding.recycler.post(new Runnable() {
            @Override
            public void run() {

                int screenHeight = binding.coordinator.getHeight();
                int contentHeight = binding.recycler.getHeight();
                int wiggle = screenHeight-contentHeight;

                if (binding.paddingView.getLayoutParams()!=null) {
                    // height is clamped between the max size allowed and 0
                    binding.paddingView.getLayoutParams().height = Math.max(Math.min(wiggle,binding.appbar.getHeight()),0);
                    binding.paddingView.requestLayout();
                }
            }
        });

        Log.v(TAG, "Fetched " + tracks.size() + " from the library");
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.action_close:
                getFragmentManager().popBackStack();
                break;

            case R.id.action_shuffle:
                if (getPlayerService()!=null && getMediaLibrary()!=null) {
                    Pair<List<Track>,Integer> playlist = PlaylistResolver.resolvePlaylistFromAlbum(getMediaLibrary(), albumId);
                    int randomPosition = (int)(Math.random()*playlist.first.size());
                    getPlayerService().getPlayer().setPlaylist(playlist.first, randomPosition, false);
                    getPlayerService().getPlayer().setShuffle(true);
                    getPlayerService().getPlayer().play();
                }
                break;
        }
    }

    @Override
    public void onClick(@NonNull final Track item, @NonNull View view)
    {
        if (getPlayerService()==null || getMediaLibrary()==null) { return; }

        Pair<List<Track>,Integer> playlist =
                PlaylistResolver.resolvePlaylistFromTrack(getMediaLibrary(), item.getTrackId(), PlaybackMode.PlaylistMode.ALBUM);
        getPlayerService().getPlayer().setPlaylist(playlist.first, playlist.second, true);
    }

    public void setAlbumId(long albumId)
    {
        this.albumId = albumId;
    }
}
