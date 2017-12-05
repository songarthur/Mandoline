package com.songa.mandoline.core.browse.tracks;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songa.mandoline.R;
import com.songa.mandoline.core.base.BaseFragment;
import com.songa.mandoline.databinding.FragmentListTrackBinding;
import com.songa.mandoline.audio.entity.Track;
import com.songa.mandoline.audio.player.PlaybackMode;
import com.songa.mandoline.audio.library.PlaylistResolver;
import com.songa.mandoline.ui.listener.ItemEventListener;

import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class TrackListFragment extends BaseFragment implements ItemEventListener<Track>, View.OnClickListener
{
    private static final String TAG = TrackListFragment.class.getSimpleName();

    private FragmentListTrackBinding binding;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private TrackListAdapter recyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentListTrackBinding.inflate(inflater, container, false);

        binding.actionShuffle.setOnClickListener(this);

        recyclerLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recycler.setLayoutManager(recyclerLayoutManager);
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
    public void onMediaLibraryInitialized()
    {
        if (getMediaLibrary()==null) { return; }

        recyclerAdapter = new TrackListAdapter();
        recyclerAdapter.setListener(this);
        binding.recycler.setAdapter(recyclerAdapter);

        List<Track> allTracks = getMediaLibrary().allTracks();
        recyclerAdapter.addAll(allTracks, true);
        Log.v(TAG, "Fetched " + allTracks.size() + " tracks from library");
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.action_shuffle:
                if (getPlayerService()!=null && getMediaLibrary()!=null) {
                    List<Track> playlist = getMediaLibrary().allTracks();
                    int randomPosition = (int)(Math.random()*playlist.size());
                    getPlayerService().getPlayer().setPlaylist(playlist, randomPosition, false);
                    getPlayerService().getPlayer().setShuffle(true);
                    getPlayerService().getPlayer().play();
                }
                break;
        }
    }

    @Override
    public void onClick(@NonNull Track item, @NonNull View view)
    {
        if (getPlayerService()==null || getMediaLibrary()==null) { return; }

        Pair<List<Track>,Integer> playlist = PlaylistResolver.resolvePlaylistFromTrack(getMediaLibrary(), item.getTrackId(), PlaybackMode.PlaylistMode.ALL_TRACKS);
        getPlayerService().getPlayer().setPlaylist(playlist.first, playlist.second, true);
    }
}
