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
import com.songa.mandoline.util.TrackDurationUtil;

/**
 * Viewholder used to display a track. Simple version.
 */
public class TrackViewholder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private final UiItemTrackBinding binding;
    private @Nullable ViewholderListener<TrackViewholder> listener = null;

    private @Nullable Track track = null;

    private TrackViewholder(@NonNull final UiItemTrackBinding binding)
    {
        super(binding.getRoot());
        this.binding = binding;
    }

    /**
     * Returns a viewholder already attached to its inflated view.
     *
     * @param parent
     * @return
     */
    public static @NonNull TrackViewholder inflate(@NonNull ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        UiItemTrackBinding binding = UiItemTrackBinding.inflate(inflater, parent, false);
        TrackViewholder vh = new TrackViewholder(binding);

        binding.getRoot().setOnClickListener(vh);

        return vh;
    }

    /**
     * Utility function. Binds the given viewholder with the given track if and only if it is an
     * instance of {@link TrackViewholder}.
     *
     * @param holder
     * @param track
     */
    public static void bind(@NonNull RecyclerView.ViewHolder holder, @Nullable Track track)
    {
        if (holder instanceof TrackViewholder) {
            TrackViewholder vh = (TrackViewholder) holder;
            vh.setup(track);
        }
    }

    /**
     * Binds the given track to this viewholder.
     *
     * @param track
     */
    public void setup(@Nullable Track track)
    {
        this.track = track;

        if (track==null) {
            binding.trackNumber.setText("");
            binding.trackTitle.setText("");
            binding.trackDuration.setText("");

        } else {

            String trackNo = track.getTrackNumber()>0 ? Long.toString(track.getTrackNumber()%100) : "";
            String duration = TrackDurationUtil.getDurationString(track);

            binding.trackNumber.setText(trackNo);
            binding.trackTitle.setText(track.getTrackTitle());
            binding.trackDuration.setText(duration);
        }
    }

    /**
     * Sets a click listener for this viewholder.
     *
     * @param listener
     * @return
     */
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

    /**
     * Returns the track bound to this viewholder.
     *
     * @return
     */
    @Nullable
    public Track getTrack() {
        return track;
    }
}
