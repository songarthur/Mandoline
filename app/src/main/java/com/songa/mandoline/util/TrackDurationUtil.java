package com.songa.mandoline.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.SeekBar;

import com.songa.mandoline.audio.entity.Track;

import java.util.Locale;

public final class TrackDurationUtil
{
    private TrackDurationUtil() {}

    public static String getDurationString(@Nullable Track track)
    {
        if (track==null || track.getTrackDuration()<0) {
            return "--:--";
        } else {
            return getDurationString((int)track.getTrackDuration());
        }
    }

    public static String getDurationString(int durationInMs)
    {
        StringBuilder sb = new StringBuilder();

        int durationInSec = durationInMs/1000;
        int sec = durationInSec % 60;
        int min = durationInSec / 60;
        int hrs = durationInSec / 3600;

        if (hrs>0) { sb.append(Integer.toString(hrs)).append(":"); }
        sb.append(String.format(Locale.FRANCE, "%02d", min))
                .append(":")
                .append(String.format(Locale.FRANCE, "%02d", sec));

        return sb.toString();
    }

    public static void setSeekbar(@NonNull SeekBar seekbar, @Nullable Track track, int playbackPositionInMs)
    {
        if (track==null || track.getTrackDuration()<=0) {
            seekbar.setProgress(0);
            return;
        }

        float duration = track.getTrackDuration();
        float ratio = playbackPositionInMs/duration;
        int progress = Math.min((int)(ratio*seekbar.getMax()), seekbar.getMax());

        seekbar.setProgress(progress);
    }

    public static int getPlaybackPositionFromSeekbar(@NonNull SeekBar seekbar, @Nullable Track track)
    {
        if (track==null || track.getTrackDuration()<=0) { return 0; }

        float duration = track.getTrackDuration();
        float ratio = (float)seekbar.getProgress() / (float)seekbar.getMax();
        float position = duration * ratio;

        return (int)position;
    }
}
