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
import com.songa.mandoline.util.TrackDurationUtil;

/**
 * Alternative viewholder to display a track. Features a more detailed view (duration and track no).
 */
public class TrackAltViewholder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private final UiItemTrackAltBinding binding;
    private @Nullable ViewholderListener<TrackAltViewholder> listener = null;

    private @Nullable Track track = null;

    private TrackAltViewholder(@NonNull final UiItemTrackAltBinding binding)
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
    public static @NonNull TrackAltViewholder inflate(@NonNull ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        UiItemTrackAltBinding binding = UiItemTrackAltBinding.inflate(inflater, parent, false);
        TrackAltViewholder vh = new TrackAltViewholder(binding);

        binding.getRoot().setOnClickListener(vh);

        return vh;
    }

    /**
     * Utility function. Binds the given viewholder with the given track if and only if it is an
     * instance of {@link TrackAltViewholder}.
     *
     * @param holder
     * @param track
     */
    public static void bind(@NonNull RecyclerView.ViewHolder holder, @Nullable Track track)
    {
        if (holder instanceof TrackAltViewholder) {
            TrackAltViewholder vh = (TrackAltViewholder) holder;
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
            binding.trackTitle.setText("");
            binding.trackSubtitle.setText("");
            binding.trackDuration.setText("");

        } else {

            String duration = TrackDurationUtil.getDurationString(track);

            String subtitle = track.getArtistName()!=null ? track.getArtistName() : "";
            if (track.getAlbumName()!=null) {
                subtitle += (subtitle.isEmpty()? "" : " | ") + track.getAlbumName();
            }

            binding.trackTitle.setText(track.getTrackTitle());
            binding.trackSubtitle.setText(subtitle);
            binding.trackDuration.setText(duration);
        }
    }

    /**
     * Sets a click listener for this viewholder.
     *
     * @param listener
     * @return
     */
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

    /**
     * Returns the track bound to tohis viewholder.
     * @return
     */
    @Nullable
    public Track getTrack() {
        return track;
    }
}
