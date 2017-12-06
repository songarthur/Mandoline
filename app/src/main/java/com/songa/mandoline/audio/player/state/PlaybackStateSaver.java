package com.songa.mandoline.audio.player.state;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.songa.mandoline.audio.entity.Track;
import com.songa.mandoline.audio.library.MediaLibrary;
import com.songa.mandoline.audio.player.PlayerInterface;
import com.songa.mandoline.audio.player.PlaylistQueue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used to persist and restore a player's state using {@link SharedPreferences}.
 */
public class PlaybackStateSaver
{
    private static final String TAG = PlaybackStateSaver.class.getSimpleName();

    private static final String PREFERENCE_FILE_PLAYBACK = "preferences.playback";

    private static final String PREFERENCE_PLAYLIST = "playlist.playlist_regular";
    private static final String PREFERENCE_PLAYLIST_SHUFFLED = "playlist.playlist_shuffled";
    private static final String PREFERENCE_PLAYLIST_POSITION = "playlist.position";
    private static final String PREFERENCE_PLAYLIST_USES_SHUFFLED = "playlist.is_shuffled";
    private static final String PREFERENCE_PLAYLIST_IS_LOOPING = "playlist.is_looping";

    private static final String PREFERENCE_PLAYBACK_POSITION = "preferences.playback.playback_position";

    @NonNull private final Context context;

    public PlaybackStateSaver(@NonNull Context context)
    {
        this.context = context;
    }

    /**
     * Clears all player state files.
     */
    public void clear()
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_FILE_PLAYBACK, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Saves the state of a playlist queue.
     *
     * @param playlist
     * @param shuffledPlaylist
     * @param playlistPosition
     * @param useShuffle
     * @param isLooping
     */
    public void savePlaylistSettings(@NonNull List<Track> playlist,
                                     @NonNull List<Track> shuffledPlaylist,
                                     int playlistPosition,
                                     boolean useShuffle,
                                     boolean isLooping)
    {
        List<Long> indexes = new ArrayList<>(playlist.size());
        for (Track t : playlist) {
            if (t!=null && t.getTrackId()>=0) { indexes.add(t.getTrackId()); }
        }

        List<Long> indexesShuffled = new ArrayList<>(shuffledPlaylist.size());
        for (Track t : shuffledPlaylist) {
            if (t!=null && t.getTrackId()>=0) { indexesShuffled.add(t.getTrackId()); }
        }

        Gson gson = new Gson();
        String json = gson.toJson(indexes);
        String jsonShuffled = gson.toJson(indexesShuffled);

        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_FILE_PLAYBACK, Context.MODE_PRIVATE).edit();
        editor.putString(PREFERENCE_PLAYLIST, json);
        editor.putString(PREFERENCE_PLAYLIST_SHUFFLED, jsonShuffled);
        editor.putInt(PREFERENCE_PLAYLIST_POSITION, playlistPosition);
        editor.putBoolean(PREFERENCE_PLAYLIST_USES_SHUFFLED, useShuffle);
        editor.putBoolean(PREFERENCE_PLAYLIST_IS_LOOPING, isLooping);
        editor.apply();
    }

    /**
     * Saves a playlist queue looping state.
     *
     * @param isLooping
     */
    public void savePlaylistIsLooping(boolean isLooping)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_FILE_PLAYBACK, Context.MODE_PRIVATE).edit();
        editor.putBoolean(PREFERENCE_PLAYLIST_IS_LOOPING, isLooping);
        editor.apply();
    }

    /**
     * Saves the playlist's current position.
     *
     * @param playlistPosition
     */
    public void savePlaylistPosition(int playlistPosition)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_FILE_PLAYBACK, Context.MODE_PRIVATE).edit();
        editor.putInt(PREFERENCE_PLAYLIST_POSITION, playlistPosition);
        editor.apply();
    }

    /**
     * Saves the playlist as being shuffled.
     *
     * @param shuffledPlaylist
     * @param playlistPosition
     */
    public void savePlaylistShuffledState(@NonNull List<Track> shuffledPlaylist, int playlistPosition)
    {
        List<Long> indexesShuffled = new ArrayList<>(shuffledPlaylist.size());
        for (Track t : shuffledPlaylist) {
            if (t!=null && t.getTrackId()>=0) { indexesShuffled.add(t.getTrackId()); }
        }

        Gson gson = new Gson();
        String jsonShuffled = gson.toJson(indexesShuffled);

        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_FILE_PLAYBACK, Context.MODE_PRIVATE).edit();
        editor.putString(PREFERENCE_PLAYLIST_SHUFFLED, jsonShuffled);
        editor.putInt(PREFERENCE_PLAYLIST_POSITION, playlistPosition);
        editor.putBoolean(PREFERENCE_PLAYLIST_USES_SHUFFLED, true);
        editor.apply();
    }

    /**
     * Saves the playlist as being not shuffled.
     *
     * @param playlistPosition
     */
    public void savePlaylistNotShuffledState(int playlistPosition)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_FILE_PLAYBACK, Context.MODE_PRIVATE).edit();
        editor.putInt(PREFERENCE_PLAYLIST_POSITION, playlistPosition);
        editor.putBoolean(PREFERENCE_PLAYLIST_USES_SHUFFLED, false);
        editor.apply();
    }

    /**
     * Saves the current playback position.
     *
     * @param positionInMs
     */
    public void savePlaybackPosition(int positionInMs)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_FILE_PLAYBACK, Context.MODE_PRIVATE).edit();
        editor.putInt(PREFERENCE_PLAYBACK_POSITION, positionInMs);
        editor.apply();
    }

    /**
     * Restores a playlist queue.
     *
     * @param playlist
     * @param library
     */
    public void restorePlaylistSettings(@NonNull PlaylistQueue playlist, @NonNull MediaLibrary library)
    {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_FILE_PLAYBACK, Context.MODE_PRIVATE);

        String json = sp.getString(PREFERENCE_PLAYLIST, null);
        String jsonShuffled = sp.getString(PREFERENCE_PLAYLIST_SHUFFLED, null);
        int position = sp.getInt(PREFERENCE_PLAYLIST_POSITION, 0);
        boolean useShuffle = sp.getBoolean(PREFERENCE_PLAYLIST_USES_SHUFFLED, false);
        boolean isLooping = sp.getBoolean(PREFERENCE_PLAYLIST_IS_LOOPING, false);

        if (json==null || jsonShuffled==null) { return; }

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Long>>(){}.getType();

        List<Long> indexes = gson.fromJson(json, type);
        List<Long> indexesShuffled = gson.fromJson(jsonShuffled, type);

        if (indexes==null || indexesShuffled==null) { return; }

        List<Track> p = library.tracksFromIdList(indexes);
        List<Track> pShuffled = library.tracksFromIdList(indexesShuffled);

        if (p.size()!=pShuffled.size()) { return; }

        playlist.restoreState(p, pShuffled, position, useShuffle, isLooping);
    }

    /**
     * Restore the playback position of a player
     *
     * @param player
     */
    public void restorePlaybackPosition(@NonNull PlayerInterface player)
    {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_FILE_PLAYBACK, Context.MODE_PRIVATE);
        int playbackPosition = sp.getInt(PREFERENCE_PLAYBACK_POSITION, -1);

        if (playbackPosition>=0) {
            player.seekTo(playbackPosition);
        }
    }

}
