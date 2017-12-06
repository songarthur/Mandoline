package com.songa.mandoline.core.player;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.songa.mandoline.R;
import com.songa.mandoline.artwork.Artwork;
import com.songa.mandoline.core.base.BaseActivity;
import com.songa.mandoline.databinding.ActivityPlayerBinding;
import com.songa.mandoline.audio.entity.Track;
import com.songa.mandoline.audio.player.PlaybackMode;
import com.songa.mandoline.ui.drawable.SmoothColorDrawable;
import com.songa.mandoline.util.TrackDurationUtil;

import java.util.List;

public class PlayerActivity
        extends     BaseActivity
        implements  PlayerView,
                    View.OnClickListener, SeekBar.OnSeekBarChangeListener, ViewPager.OnPageChangeListener
{
    private static final String TAG = PlayerActivity.class.getSimpleName();

    private static final int SEEKBAR_STEPS = 300;

    private PlayerPresenter presenter;

    private ActivityPlayerBinding binding;

    private PlaylistPagerAdapter playlistPagerAdapter;

    private boolean isTouchingSeekbar = false;

    private SmoothColorDrawable bgDrawable;

    ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE EVENTS
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        presenter = new PlayerPresenter(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player);

        binding.actionClose.setOnClickListener(this);

        playlistPagerAdapter = new PlaylistPagerAdapter();
        binding.pager.setAdapter(playlistPagerAdapter);
        binding.pager.setOffscreenPageLimit(1);

        binding.seekbar.setMax(SEEKBAR_STEPS);

        int bgColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        bgDrawable = new SmoothColorDrawable(bgColor, bgColor, 500);
        binding.layout.setBackground(bgDrawable);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onPlayerServiceAvailable()
    {
        presenter.onPlayerServiceAvailable(getPlayerService());
    }

    ///////////////////////////////////////////////////////////////////////////
    // PLAYLIST COVER PAGER LISTENER
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onPageSelected(int position) {}

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageScrollStateChanged(int state)
    {
       if (state == ViewPager.SCROLL_STATE_IDLE) {
           presenter.actionPageSelected(binding.pager.getCurrentItem());
       }
    }

    ///////////////////////////////////////////////////////////////////////////
    // SEEKBAR LISTENER
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
        isTouchingSeekbar = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        isTouchingSeekbar = false;
        presenter.actionSeek();
    }

    ///////////////////////////////////////////////////////////////////////////
    // CLICK LISTENER
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onClick(View v)
    {
        if (v.getId()==R.id.action_close) {
            finish();
            return;
        }

        if (getPlayerService()==null) { return; }

        switch (v.getId()) {
            case R.id.action_play_pause:
                presenter.actionPlayPause();
                break;

            case R.id.action_previous:
                presenter.actionPreviousTrack();
                break;

            case R.id.action_next:
                presenter.actionNextTrack();
                break;

            case R.id.action_repeat_mode:
                presenter.actionRepeatMode();
                break;

            case R.id.action_shuffle:
                presenter.actionShuffle();
                break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // TOUCH LISTENERS
    ///////////////////////////////////////////////////////////////////////////

    public void enableTouchListeners()
    {
        Log.v(TAG, "Enabling touch events");

        binding.seekbar.setOnSeekBarChangeListener(this);
        binding.seekbar.setEnabled(true);

        binding.pager.removeOnPageChangeListener(this);
        binding.pager.addOnPageChangeListener(this);
        binding.pager.setOnTouchListener(null);

        binding.actionPlayPause.setOnClickListener(this);
        binding.actionNext.setOnClickListener(this);
        binding.actionPrevious.setOnClickListener(this);
        binding.actionRepeatMode.setOnClickListener(this);
        binding.actionShuffle.setOnClickListener(this);
    }

    public void disableTouchListeners()
    {
        Log.v(TAG, "Disabling touch events");

        binding.seekbar.setOnSeekBarChangeListener(null);
        binding.seekbar.setEnabled(false);

        binding.pager.removeOnPageChangeListener(this);
        binding.pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        binding.actionPlayPause.setOnClickListener(null);
        binding.actionNext.setOnClickListener(null);
        binding.actionPrevious.setOnClickListener(null);
        binding.actionRepeatMode.setOnClickListener(null);
        binding.actionShuffle.setOnClickListener(null);
    }

    ///////////////////////////////////////////////////////////////////////////
    // CONTROLS
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void setIsPlaying(boolean isPlaying)
    {
        binding.actionPlayPause.setActivated(isPlaying);
    }

    @Override
    public boolean isPlaying()
    {
        return binding.actionPlayPause.isActivated();
    }

    @Override
    public void setIsShuffling(boolean isShuffling)
    {
        binding.actionShuffle.setActivated(isShuffling);
    }

    @Override
    public boolean isShuffling()
    {
        return binding.actionShuffle.isActivated();
    }

    @Override
    public void setRepeatMode(@NonNull PlaybackMode.RepeatMode repeatMode)
    {
        switch (repeatMode) {
            case NONE:
                binding.actionRepeatMode.setActivated(false);
                binding.actionRepeatMode.setSelected(false);
                break;
            case PLAYLIST:
                binding.actionRepeatMode.setActivated(true);
                binding.actionRepeatMode.setSelected(false);
                break;
            case TRACK:
                binding.actionRepeatMode.setActivated(true);
                binding.actionRepeatMode.setSelected(true);
                break;
        }
    }

    @NonNull
    @Override
    public PlaybackMode.RepeatMode getRepeatMode()
    {
        ImageButton btn = binding.actionRepeatMode;

        if (!btn.isActivated()) { return PlaybackMode.RepeatMode.NONE; }
        return btn.isSelected() ? PlaybackMode.RepeatMode.TRACK : PlaybackMode.RepeatMode.PLAYLIST;
    }

    ///////////////////////////////////////////////////////////////////////////
    // CURRENT TRACK & PLAYLIST
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void setPlaybackPosition(int playbackPosition)
    {
        if (isTouchingSeekbar) { return; }

        Track track = playlistPagerAdapter.getTrack(binding.pager.getCurrentItem());

        TrackDurationUtil.setSeekbar(binding.seekbar, track, playbackPosition);

        String positionString = TrackDurationUtil.getDurationString(playbackPosition);
        binding.trackPlayback.setText(positionString);
    }

    @Override
    public int getPlaybackPosition()
    {
        Track track = playlistPagerAdapter.getTrack(binding.pager.getCurrentItem());
        return TrackDurationUtil.getPlaybackPositionFromSeekbar(binding.seekbar, track);
    }

    @Override
    public void setPlaylist(@NonNull List<Track> playlist)
    {
        playlistPagerAdapter.setPlaylist(playlist);
        binding.playlistCount.setText(Integer.toString(playlist.size()));
    }

    @Override
    public void setCurrentTrack(int playlistPosition)
    {
        binding.pager.setCurrentItem(playlistPosition, true);

        Track track = playlistPagerAdapter.getTrack(playlistPosition);

        if (track==null) {
            binding.trackTitle.setText("");
            binding.trackAlbum.setText("");
            binding.trackArtist.setText("");
            bgDrawable.setDefaultColor();
            return;
        }

        binding.trackTitle.setText(track.getTrackTitle());
        binding.trackAlbum.setText(track.getAlbumName());
        binding.trackArtist.setText(track.getArtistName());

        String durationString = TrackDurationUtil.getDurationString(track);
        binding.trackDuration.setText(durationString);

        binding.playlistPosition.setText(Integer.toString(playlistPosition+1));

        Palette palette = Artwork.getPalette(track.getArtistName(), track.getAlbumName());
        int color = palette!=null ? palette.getDarkMutedColor(-1) : -1;
        if (color != -1) { bgDrawable.setNewColor(color); }
        else { bgDrawable.setDefaultColor(); }

        //updateBackgroundColor(track);
    }

    ///////////////////////////////////////////////////////////////////////////
    // BACKGROUND
    ///////////////////////////////////////////////////////////////////////////



}
