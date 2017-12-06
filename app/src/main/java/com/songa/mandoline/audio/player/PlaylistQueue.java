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

/**
 * A playlist queue.
 * Stores all tracks in the playlist, keeps tracks of the current track, responsible for shuffling,
 * loops the playlist if necessary.
 */
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

    /**
     * Sets the current playlist being played.
     *
     * @param playlist the playlist being played ordered
     */
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

    /**
     * Sets whether or not this playlist should loop.
     *
     * @param loopPlaylist
     */
    public void setLoopPlaylist(boolean loopPlaylist)
    {
        this.loopPlaylist = loopPlaylist;
        stateSaver.savePlaylistIsLooping(loopPlaylist);
    }

    /**
     * Shuffles/unshuffles the playlist. <br>
     * If set to false, will restore the playlist to its original state, when setPlaylist was last called.<br>
     * If set to true, will shuffle the playlist or reshuffle it if it was already in a shuffled state,
     * and will place the current track at the top of the playlist.
     *
     * Either way the current track does not change, the playlist position will change to conform to this.
     *
     * @param shuffle
     */
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

    /**
     * Moves the playlist to an arbitrary position.
     *
     * @param newPosition
     */
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

    /**
     * Skips to the next track.
     *
     * @return the next track, or null if there is no track next
     */
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

    /**
     * Skips to the previous track.
     *
     * @return the previous track, or null if there is none
     */
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

    /**
     * Return a copy of the current playlist.
     *
     * @return
     */
    @NonNull
    public List<Track> getPlaylist()
    {
        return new ArrayList<>(currentPlaylist);
    }

    /**
     * Returns the position in the playlist of the current track.
     *
     * @return
     */
    public int getPlaylistPosition()
    {
        return position;
    }

    /**
     * Returns the current track.
     *
     * @return
     */
    public @Nullable Track getCurrentTrack()
    {
        if (position<0 || position>=currentPlaylist.size()) { return null; }
        return currentPlaylist.get(position);
    }

    /**
     * Returns true if the playlist is looping.
     * @return
     */
    public boolean isLoopingPlaylist()
    {
        return loopPlaylist;
    }

    /**
     * Returns true if the playlist is shuffling.
     * @return
     */
    public boolean isUsingShuffledPlaylist()
    {
        return currentPlaylist == shuffledPlaylist;
    }

    ///////////////////////////////////////////////////////////////////////////
    // STATE PERSISTENCE
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Saves the state of the queue.
     */
    void saveState()
    {
        stateSaver.savePlaylistSettings(
                playlist,
                shuffledPlaylist,
                position,
                isUsingShuffledPlaylist(),
                isLoopingPlaylist());
    }

    /**
     * Restores the playlist to the given state.
     *
     * @param playlist
     * @param shuffledPlaylist
     * @param playlistPosition
     * @param useShuffle
     * @param isLooping
     */
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
