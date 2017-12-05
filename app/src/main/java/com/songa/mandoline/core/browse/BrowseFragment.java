package com.songa.mandoline.core.browse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songa.mandoline.R;
import com.songa.mandoline.core.base.BaseFragment;
import com.songa.mandoline.databinding.FragmentBrowseBinding;

public class BrowseFragment extends BaseFragment
{
    private FragmentBrowseBinding binding;
    private BrowseAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        setExitTransition(new Fade());
        binding = FragmentBrowseBinding.inflate(inflater, container, false);

        pagerAdapter = new BrowseAdapter(this, getChildFragmentManager());
        binding.pager.setAdapter(pagerAdapter);
        binding.pager.setOffscreenPageLimit(4);

        binding.tabs.setupWithViewPager(binding.pager, true);
        setupTabs();

        return binding.getRoot();
    }

    private void setupTabs()
    {
        if (binding.tabs.getTabCount()!=3) {
            Log.e(BrowseFragment.class.getSimpleName(), "Unexpected number of tabs (expected 3) : " + binding.tabs.getTabCount());
            return;
        }

        binding.tabs.getTabAt(0).setIcon(R.drawable.ico_artist_selector);
        //binding.tabs.getTabAt(0).setText(R.string.tab_browse_artists);

        binding.tabs.getTabAt(1).setIcon(R.drawable.ico_album_selector);
        //binding.tabs.getTabAt(1).setText(R.string.tab_browse_albums);

        binding.tabs.getTabAt(2).setIcon(R.drawable.ico_tracks_selector);
        //binding.tabs.getTabAt(2).setText(R.string.tab_browse_tracks);
    }
}
