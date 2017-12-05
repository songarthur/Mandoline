package com.songa.mandoline.ui.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songa.mandoline.databinding.UiItemTrackBinding;
import com.songa.mandoline.audio.entity.Track;
import com.songa.mandoline.ui.listener.ViewholderListener;

public class TrackViewholder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private final UiItemTrackBinding binding;
    private @Nullable ViewholderListener<TrackViewholder> listener = null;

    private @Nullable Track track = null;

    public TrackViewholder(@NonNull final UiItemTrackBinding binding)
    {
        super(binding.getRoot());
        this.binding = binding;
    }

    public static @NonNull TrackViewholder inflate(@NonNull ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        UiItemTrackBinding binding = UiItemTrackBinding.inflate(inflater, parent, false);
        TrackViewholder vh = new TrackViewholder(binding);

        binding.getRoot().setOnClickListener(vh);

        return vh;
    }

    public static void bind(@NonNull RecyclerView.ViewHolder holder, @Nullable Track track)
    {
        if (holder instanceof TrackViewholder) {
            TrackViewholder vh = (TrackViewholder) holder;
            vh.setup(track);
        }
    }

    public void setup(@Nullable Track track)
    {
        this.track = track;

        if (track==null) {

        } else {


            String trackNo = track.getTrackNumber()>0 ? Long.toString(track.getTrackNumber()%100) : "";
            String duration = track.getTrackDuration()>0 ?
                    String.format("%d:%02d", track.getTrackDuration()/60000, (track.getTrackDuration()/1000)%60)
                    : "";

            binding.trackNumber.setText(trackNo);
            binding.trackTitle.setText(track.getTrackTitle());
            binding.trackDuration.setText(duration);
        }
    }

    public TrackViewholder clickListener(@Nullable ViewholderListener<TrackViewholder> listener)
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
