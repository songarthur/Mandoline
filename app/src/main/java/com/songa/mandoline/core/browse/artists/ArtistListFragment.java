package com.songa.mandoline.core.browse.artists;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songa.mandoline.R;
import com.songa.mandoline.core.base.BaseFragment;
import com.songa.mandoline.core.detail.artist.ArtistDetailFragment;
import com.songa.mandoline.databinding.FragmentListArtistBinding;
import com.songa.mandoline.audio.entity.Artist;
import com.songa.mandoline.ui.listener.ItemEventListener;
import com.songa.mandoline.ui.recycler.GridSpacingDecoration;

import java.util.List;

public class ArtistListFragment extends BaseFragment implements ItemEventListener<Artist>
{
    private static final String TAG = ArtistListFragment.class.getSimpleName();

    private FragmentListArtistBinding binding;
    private GridLayoutManager recyclerLayoutManager;
    private ArtistListAdapter recyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentListArtistBinding.inflate(inflater, container, false);

        recyclerLayoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false);
        binding.recycler.setLayoutManager(recyclerLayoutManager);
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
    public void onMediaLibraryInitialized()
    {
        startSyncingWithArtistDB();
    }

    private void startSyncingWithArtistDB()
    {
        if (getMediaLibrary()==null) { return; }

        recyclerAdapter = new ArtistListAdapter();
        recyclerAdapter.setListener(this);
        binding.recycler.setAdapter(recyclerAdapter);

        List<Artist> artists = getMediaLibrary().allArtists();
        recyclerAdapter.addAll(artists, true);
        Log.v(TAG, "Fetched " + artists.size() + " allArtists from the library");
    }

    @Override
    public void onClick(@NonNull Artist item, @NonNull View view)
    {
        ArtistDetailFragment newFragment = new ArtistDetailFragment();
        newFragment.setArtistId(item.getArtistId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            newFragment.setEnterTransition(new Slide(Gravity.BOTTOM));
        }

        // TODO : cleaner and safer fragment management
        getParentFragment().getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_frame, newFragment)
                .addToBackStack(null)
                .commit();
    }
}
