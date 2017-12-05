package com.songa.mandoline.ui.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songa.mandoline.databinding.UiItemTrackAltBinding;
import com.songa.mandoline.audio.entity.Track;
import com.songa.mandoline.ui.listener.ViewholderListener;

public class TrackAltViewholder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private final UiItemTrackAltBinding binding;
    private @Nullable ViewholderListener<TrackAltViewholder> listener = null;

    private @Nullable Track track = null;

    public TrackAltViewholder(@NonNull final UiItemTrackAltBinding binding)
    {
        super(binding.getRoot());
        this.binding = binding;
    }

    public static @NonNull TrackAltViewholder inflate(@NonNull ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        UiItemTrackAltBinding binding = UiItemTrackAltBinding.inflate(inflater, parent, false);
        TrackAltViewholder vh = new TrackAltViewholder(binding);

        binding.getRoot().setOnClickListener(vh);

        return vh;
    }

    public static void bind(@NonNull RecyclerView.ViewHolder holder, @Nullable Track track)
    {
        if (holder instanceof TrackAltViewholder) {
            TrackAltViewholder vh = (TrackAltViewholder) holder;
            vh.setup(track);
        }
    }

    public void setup(@Nullable Track track)
    {
        this.track = track;

        if (track==null) {
            binding.trackTitle.setText("");
            binding.trackSubtitle.setText("");
            binding.trackDuration.setText("");

        } else {

            String duration = track.getTrackDuration()>0 ?
                    String.format("%d:%02d", track.getTrackDuration()/60000, (track.getTrackDuration()/1000)%60)
                    : "";

            String subtitle = track.getArtistName()!=null ? track.getArtistName() : "";
            if (track.getAlbumName()!=null) {
                subtitle += (subtitle.isEmpty()? "" : " | ") + track.getAlbumName();
            }

            binding.trackTitle.setText(track.getTrackTitle());
            binding.trackSubtitle.setText(subtitle);
            binding.trackDuration.setText(duration);
        }
    }

    public TrackAltViewholder clickListener(@Nullable ViewholderListener<TrackAltViewholder> listener)
    {
        this.listener = listener;
        return this;
    }

    @Override
    public void onClick(View v)
    {
        if (listener!=null) {
            listener.onClick(this, v);
        }
    }

    @Nullable
    public Track getTrack() {
        return track;
    }
}
