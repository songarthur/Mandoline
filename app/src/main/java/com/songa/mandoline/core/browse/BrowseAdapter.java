package com.songa.mandoline.core.browse;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.songa.mandoline.core.browse.albums.AlbumListFragment;
import com.songa.mandoline.core.browse.artists.ArtistListFragment;
import com.songa.mandoline.core.browse.tracks.TrackListFragment;

public class BrowseAdapter extends FragmentPagerAdapter
{
    private final @NonNull BrowseFragment parentFragment;

    public BrowseAdapter(@NonNull BrowseFragment parentFragment, @NonNull FragmentManager fm)
    {
        super(fm);
        this.parentFragment = parentFragment;
    }

    @Override
    public Fragment getItem(int position)
    {
        Fragment f;
        switch (position) {
            case 0 : f = new ArtistListFragment(); break;
            case 1 : f = new AlbumListFragment(); break;
            case 2 : f = new TrackListFragment(); break;
            default: return null;
        }
        return f;
    }

    @Override
    public int getCount()
    {
        return 3;
    }
}
