package com.songa.mandoline.audio.player;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.songa.mandoline.audio.entity.Track;
import com.songa.mandoline.audio.player.state.PlaybackStateSaver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlaylistQueue
{
    private static final String TAG = PlaylistQueue.class.getSimpleName();

    private final @NonNull List<Track> playlist = new CopyOnWriteArrayList<>();
    private final @NonNull List<Track> shuffledPlaylist = new CopyOnWriteArrayList<>();

    private @NonNull List<Track> currentPlaylist;
    private int position;
    private boolean loopPlaylist;

    private @NonNull PlaybackStateSaver stateSaver;

    public PlaylistQueue(@NonNull PlaybackStateSaver stateSaver)
    {
        this.currentPlaylist = playlist;
        this.position = 0;
        this.loopPlaylist = false;
        this.stateSaver = stateSaver;
    }

    public void setPlaylist(@NonNull List<Track> playlist)
    {
        this.playlist.clear();
        this.playlist.addAll(playlist);
        this.position = 0;

        this.shuffledPlaylist.clear();
        this.shuffledPlaylist.addAll(playlist);

        this.currentPlaylist = this.playlist;

        saveState();
    }

    public void setLoopPlaylist(boolean loopPlaylist)
    {
        this.loopPlaylist = loopPlaylist;
        stateSaver.savePlaylistIsLooping(loopPlaylist);
    }

    public void setShuffled(boolean shuffle)
    {
        Track currentTrack = getCurrentTrack();
        long currentTrackId = currentTrack!=null ? currentTrack.getTrackId() : -1L;

        Log.v(TAG, "New shuffle mode : " + shuffle);

        if (!shuffle) {

            if (currentPlaylist==playlist) {
                Log.v(TAG, "Playlist is already set to 'not shuffled'");
                return;
            }

            int positionInRegularPlaylist = -1;
            for (int i=0; i<playlist.size(); i++) {
                Track t = playlist.get(i);
                if (t!=null && t.getTrackId()==currentTrackId) {
                    positionInRegularPlaylist = i;
                    break;
                }
            }

            if (positionInRegularPlaylist<0) {
                Log.w(TAG, "Could not find current track in regular playlist. " +
                        "Shuffled and regular playlist are inconsistent. " +
                        "Will start the playlist from the beginning.");
                positionInRegularPlaylist = 0;
            }

            currentPlaylist = playlist;
            position = positionInRegularPlaylist;

            stateSaver.savePlaylistNotShuffledState(position);

        } else {

            /*
             * Intended behavior : reshuffle no matter what the current shuffle mode of the playlist is.
             * If the playlist is already shuffled, it is reshuffled and the current track is put
             * back at the beginning of the playlist.
             */

            Collections.shuffle(shuffledPlaylist);

            int positionInShuffledPlaylist = -1;
            for (int i=0; i<shuffledPlaylist.size(); i++) {
                Track t = shuffledPlaylist.get(i);
                if (t!=null && t.getTrackId()==currentTrackId) {
                    positionInShuffledPlaylist = i;
                    break;
                }
            }

            if (positionInShuffledPlaylist<0) {
                Log.v(TAG, "Could not find the current track in the shuffled playlist. " +
                        "Will start the playlist from the first track found.");
            } else {
                shuffledPlaylist.remove(positionInShuffledPlaylist);
                shuffledPlaylist.add(0, currentTrack);
            }

            currentPlaylist = shuffledPlaylist;
            position = 0;

            stateSaver.savePlaylistShuffledState(shuffledPlaylist, position);
        }
    }

    public void setPlaylistPosition(int newPosition)
    {
        if (newPosition<0 || newPosition>=currentPlaylist.size()) {
            Log.w(TAG, "Tried to set the playlist position out of bounds. " +
                            "Current size is " + currentPlaylist.size() + ", received " + newPosition);
            return;
        }

        position = newPosition;
        stateSaver.savePlaylistPosition(position);
        Log.v(TAG, "Set playlist position to : " + position);
    }

    public @Nullable Track skipToNextTrack()
    {
        if (position>=currentPlaylist.size()-1) {
            if (loopPlaylist) {
                position = 0;
            } else {
                return null;
            }
        } else {
            position++;
        }

        Log.v(TAG, "Skipped to next position : " + position);
        stateSaver.savePlaylistPosition(position);
        return currentPlaylist.get(position);
    }

    public @Nullable Track skipToPreviousTrack()
    {
        if (position<=0) {
            if (loopPlaylist) {
                position = currentPlaylist.size()-1;
            } else {
                return null;
            }
        } else {
            position--;
        }

        Log.v(TAG, "Skipped to previous position : " + position);
        stateSaver.savePlaylistPosition(position);
        return currentPlaylist.get(position);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////


    @NonNull
    public List<Track> getPlaylist()
    {
        return new ArrayList<>(currentPlaylist);
    }

    public int getPlaylistPosition()
    {
        return position;
    }

    public @Nullable Track getCurrentTrack()
    {
        if (position<0 || position>=currentPlaylist.size()) { return null; }
        return currentPlaylist.get(position);
    }

    public boolean isLoopingPlaylist()
    {
        return loopPlaylist;
    }

    public boolean isUsingShuffledPlaylist()
    {
        return currentPlaylist == shuffledPlaylist;
    }

    ///////////////////////////////////////////////////////////////////////////
    // STATE PERSISTENCE
    ///////////////////////////////////////////////////////////////////////////

    void saveState()
    {
        stateSaver.savePlaylistSettings(
                playlist,
                shuffledPlaylist,
                position,
                isUsingShuffledPlaylist(),
                isLoopingPlaylist());
    }

    public void restoreState(@NonNull List<Track> playlist,
                             @NonNull List<Track> shuffledPlaylist,
                             int playlistPosition,
                             boolean useShuffle,
                             boolean isLooping)
    {
        this.playlist.clear();
        this.playlist.addAll(playlist);

        this.shuffledPlaylist.clear();
        this.shuffledPlaylist.addAll(shuffledPlaylist);

        this.currentPlaylist = useShuffle ? this.shuffledPlaylist : this.playlist;

        this.position = playlistPosition;
        this.loopPlaylist = isLooping;

    }
}
