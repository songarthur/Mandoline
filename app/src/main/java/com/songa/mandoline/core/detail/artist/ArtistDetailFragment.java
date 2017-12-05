package com.songa.mandoline.core.detail.artist;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songa.mandoline.R;
import com.songa.mandoline.artwork.Artwork;
import com.songa.mandoline.audio.library.PlaylistResolver;
import com.songa.mandoline.core.base.BaseFragment;
import com.songa.mandoline.core.detail.album.AlbumDetailFragment;
import com.songa.mandoline.databinding.FragmentDetailsArtistBinding;
import com.songa.mandoline.audio.entity.Album;
import com.songa.mandoline.audio.entity.Artist;
import com.songa.mandoline.audio.entity.Track;
import com.songa.mandoline.ui.listener.ItemEventListener;
import com.songa.mandoline.ui.recycler.GridSpacingDecoration;

import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class ArtistDetailFragment extends BaseFragment implements View.OnClickListener, ItemEventListener<Album>
{
    public static final String TAG = ArtistDetailFragment.class.getSimpleName();

    private long artistId = -1;

    private FragmentDetailsArtistBinding binding;
    private GridLayoutManager layoutManager;
    private ArtistDetailAdapter recyclerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null) {
            artistId = savedInstanceState.getLong("artistId", -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentDetailsArtistBinding.inflate(inflater, container, false);

        binding.actionClose.setOnClickListener(this);
        binding.actionShuffle.setOnClickListener(this);

        layoutManager = new GridLayoutManager(getContext(), 2);
        binding.recycler.setLayoutManager(layoutManager);
        binding.recycler.setItemAnimator(new SlideInUpAnimator());
        binding.recycler.addItemDecoration(new GridSpacingDecoration(12, getResources()));

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
        outState.putLong("artistId", artistId);
    }

    @Override
    public void onMediaLibraryInitialized()
    {
        if (getMediaLibrary()==null) { return; }

        Artist artist = getMediaLibrary().artist(artistId);
        if (artist==null) { return; }

        binding.artistName.setText(artist.getArtistName());

        Artwork.load(artist.getArtistName(), null)
                .placeholder(R.drawable.placeholder_artist)
                .error(R.drawable.placeholder_artist)
                .into(binding.cover);

        recyclerAdapter = new ArtistDetailAdapter(artist);
        recyclerAdapter.setListener(this);
        binding.recycler.setAdapter(recyclerAdapter);

        List<Album> albums = getMediaLibrary().albumsFromArtist(artist.getArtistId());
        recyclerAdapter.addAlbums(albums, true);

        /* ******************************** NASTY HACK ALERT ***************************************
         *
         * This is to prevent the coordinator from scrolling beyond the content of the recycler.
         * Without this the coordinator scrolls all the way up and if the recycler doesn't have enough
         * items to fill the screen, it will leave an empty space at the bottom.
         *
         * The CollapsingToolbarLayout is tied to the nearest Toolbar. If this toolbar reaches the top
         * of the CoordinatorLayout, it stops scrolling. And all scroll events go directly to the recycler.
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

        Log.v(TAG, "Fetched " + albums.size() + " allAlbums from the library");
    }

    @Override
    public void onClick(@NonNull Album item, @NonNull View view)
    {
        AlbumDetailFragment newFragment = new AlbumDetailFragment();
        newFragment.setAlbumId(item.getAlbumId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setExitTransition(new Fade());
            newFragment.setEnterTransition(new Slide(Gravity.BOTTOM));
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.main_frame, newFragment)
                .addToBackStack(null)
                .commit();
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
                    Pair<List<Track>,Integer> playlist = PlaylistResolver.resolvePlaylistFromArtist(getMediaLibrary(), artistId);
                    int randomStartingPosition = (int)(Math.random()*playlist.first.size());
                    getPlayerService().getPlayer().setPlaylist(playlist.first, randomStartingPosition, false);
                    getPlayerService().getPlayer().setShuffle(true);
                    getPlayerService().getPlayer().play();
                }
                break;
        }
    }

    public void setArtistId(long artistId)
    {
        this.artistId = artistId;
    }
}
