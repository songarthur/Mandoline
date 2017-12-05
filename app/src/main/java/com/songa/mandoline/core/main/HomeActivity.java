package com.songa.mandoline.core.main;

import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.songa.mandoline.R;
import com.songa.mandoline.artwork.Artwork;
import com.songa.mandoline.core.AppRouter;
import com.songa.mandoline.core.base.BaseActivity;
import com.songa.mandoline.core.browse.BrowseFragment;
import com.songa.mandoline.databinding.ActivityHomeBinding;
import com.songa.mandoline.audio.entity.Track;

public class HomeActivity
        extends     BaseActivity
        implements  HomeView,
                    View.OnClickListener
{
    private ActivityHomeBinding binding;
    private HomePresenter presenter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        binding.player.actionPlayPause.setActivated(false);
        binding.player.actionPlayPause.setOnClickListener(this);
        binding.player.player.setOnClickListener(this);

        Fragment browseFragment = getSupportFragmentManager().findFragmentByTag(BrowseFragment.class.getSimpleName());

        if (browseFragment==null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_frame, new BrowseFragment(), BrowseFragment.class.getSimpleName())
                    .commitNow();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (presenter!=null) { presenter.start();}
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (presenter!=null) { presenter.stop();}
    }

    @Override
    public void onPlayerServiceAvailable()
    {
        if (getPlayerService()==null) { return; }
        presenter = new HomePresenter(this, getPlayerService().getPlayer());
        presenter.start();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.player:
                AppRouter.goToPlayerPage(this);
                break;
            case R.id.action_play_pause:
                if (presenter!=null) {
                    presenter.actionPlayPause(binding.player.actionPlayPause.isActivated());
                }
                break;
        }
    }

    @Override
    public void setCurrentTrack(@Nullable Track track)
    {
        if (track==null)
        {
            binding.player.playerTitle.setText("");
            binding.player.playerTitle.setText("");
            binding.player.playerCoverArt.setImageResource(R.drawable.placeholder_album);
            return;
        }

        binding.player.playerTitle.setText(track.getTrackTitle());
        binding.player.playerSubtitle.setText(track.getArtistName() + " | " + track.getAlbumName());

        Artwork.load(track.getArtistName(), track.getAlbumName())
                .placeholder(R.drawable.placeholder_album)
                .error(R.drawable.placeholder_album)
                .localArtworkFallback(track.getCoverArtUri())
                .into(binding.player.playerCoverArt);
    }

    @Override
    public void setPlaying(boolean isPlaying)
    {
        binding.player.actionPlayPause.setActivated(isPlaying);
    }
}
