package com.songa.mandoline.core.browse.albums;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songa.mandoline.R;
import com.songa.mandoline.core.base.BaseFragment;
import com.songa.mandoline.core.detail.album.AlbumDetailFragment;
import com.songa.mandoline.databinding.FragmentListAlbumBinding;
import com.songa.mandoline.audio.entity.Album;
import com.songa.mandoline.audio.library.MediaLibrary;
import com.songa.mandoline.ui.listener.ItemEventListener;
import com.songa.mandoline.ui.recycler.GridSpacingDecoration;

import java.util.List;

public class AlbumListFragment extends BaseFragment implements ItemEventListener<Album>
{
    private static final String TAG = AlbumListFragment.class.getSimpleName();

    private FragmentListAlbumBinding binding;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private AlbumListAdapter recyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentListAlbumBinding.inflate(inflater, container, false);

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
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable("layoutManager", recyclerLayoutManager.onSaveInstanceState());
    }

    @Override
    public void onMediaLibraryInitialized()
    {
        startSyncingWithAlbumDB(getMediaLibrary());
    }

    @Override
    public void onClick(@NonNull Album item, @NonNull View view)
    {
        AlbumDetailFragment newFragment = new AlbumDetailFragment();
        newFragment.setAlbumId(item.getAlbumId());

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

    private void startSyncingWithAlbumDB(@Nullable MediaLibrary mediaLibrary)
    {
        if (mediaLibrary==null) { return; }

        recyclerAdapter = new AlbumListAdapter();
        recyclerAdapter.setListener(this);
        binding.recycler.setAdapter(recyclerAdapter);

        List<Album> albums = mediaLibrary.allAlbums();
        recyclerAdapter.addAll(albums, true);
        Log.v(TAG, "Fetched " + albums.size() + " allAlbums from the library");
    }
}
