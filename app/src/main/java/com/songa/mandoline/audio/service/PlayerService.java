package com.songa.mandoline.audio.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.songa.mandoline.R;
import com.songa.mandoline.audio.player.state.PlaybackStateEvent;
import com.songa.mandoline.audio.entity.Track;
import com.songa.mandoline.audio.player.state.PlaybackStateSaver;
import com.songa.mandoline.audio.library.MediaLibrary;
import com.songa.mandoline.audio.service.binding.PlayerServiceBinder;
import com.songa.mandoline.audio.player.PlayerInteractor;
import com.songa.mandoline.audio.player.state.PlayerStateNotifier;
import com.songa.mandoline.audio.player.PlayerInterface;
import com.songa.mandoline.audio.player.PlayerManager;
import com.songa.mandoline.audio.player.PlaylistQueue;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Service used to play music on the background.<br><br>
 *
 * Do not bind to this service directly, use instead {@link com.songa.mandoline.audio.service.binding.PlayerServiceBindingDelegate}.<br>
 *
 * Service binds take place in the main thread, but the service needs MediaLibrary to restore
 * its state. So instead of blocking the main thread in onCreate waiting for MediaLibrary we exit
 * immediately from onCreate and the initialization is done asynchronously. When the initialization is
 * finished we notify it through the initialization watcher.<br>
 *
 * Therefore be careful when binding manually to this service, you might receive a service that is
 * not initialized yet.
 */
public class PlayerService extends Service implements AudioManager.OnAudioFocusChangeListener
{
    private static final String TAG = PlayerService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 17061992;

    private MediaSessionCompat mediaSession;

    private HandlerThread thread;
    private PlayerInteractor playerInteractor;
    private PlayerStateNotifier playerStateNotifier;

    private Disposable playbackWatcher;

    private long playingTrackId = -1;
    private int playbackState = PlaybackStateCompat.STATE_PAUSED;

    private BehaviorSubject<Boolean> initializationWatcher = BehaviorSubject.createDefault(false);

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return new PlayerServiceBinder(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i(TAG, "onStartCommand()");
        MediaButtonReceiver.handleIntent(mediaSession, intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.i(TAG, "Service creation requested");
        super.onCreate();

        // Initial state of the session
        PlaybackStateCompat state = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY_PAUSE)
                .build();

        // Set up the MediaSession
        mediaSession = new MediaSessionCompat(this, PlayerService.class.getSimpleName());
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setPlaybackState(state);

        // Set up callbacks for Media Buttons
        mediaSession.setCallback(new MediaButtonListener(this));

        // Launch service initialization outside of the main thread
        MediaLibrary.getLibraryProvider(this)
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<MediaLibrary>() {
                    @Override
                    public void accept(MediaLibrary library) throws Exception {
                        restoreServiceState(library);
                    }
                });

        Log.i(TAG, "Service created");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (playbackWatcher!=null) { playbackWatcher.dispose(); }
        thread.quitSafely();

        Log.i(TAG, "Service destroyed");
    }

    @Override
    public void onAudioFocusChange(int focusChange)
    {
        // TODO : Proper audio focus management
        // lower volume on transient loss etc...
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                playerInteractor.play();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                playerInteractor.pause();
                break;
        }
    }

    /**
     * Called when the player is playing.
     * @param event
     */
    private void onPlayEvent(@NonNull PlaybackStateEvent event)
    {
        Track track = playerInteractor.getCurrentTrack();
        if (track==null) { return; }

        if (playbackState!=PlaybackStateCompat.STATE_PLAYING) {

            playbackState = PlaybackStateCompat.STATE_PLAYING;

            // signal other music apps they should pause
            AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            if (audioManager!=null) {
                audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            }
        }

        mediaSession.setActive(true);
        mediaSession.setPlaybackState(buildPlaybackState(PlaybackStateCompat.STATE_PLAYING, event.getNewPlaybackPosition()));

        if (playingTrackId!=track.getTrackId()) {
            // Display the notification and place the service in the foreground
            startForeground(NOTIFICATION_ID, buildNotification(track, null, true));
            playingTrackId = track.getTrackId();
        }
    }

    /**
     * Called when the player pauses.
     *
     * @param event
     */
    private void onPauseEvent(@NonNull PlaybackStateEvent event)
    {
        playbackState = PlaybackStateCompat.STATE_PAUSED;
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (audioManager!=null) {
            audioManager.abandonAudioFocus(this);
        }

        mediaSession.setActive(false);
        mediaSession.setPlaybackState(buildPlaybackState(PlaybackStateCompat.STATE_PAUSED, event.getNewPlaybackPosition()));

        stopForeground(false);
        playingTrackId = -1;

        Track track = playerInteractor.getCurrentTrack();
        if (track != null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, buildNotification(track, null, false));
        }
    }

    /**
     * Builds the playback state.
     *
     * @param state
     * @param position
     * @return
     */
    private PlaybackStateCompat buildPlaybackState(int state, int position)
    {
        return new PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE
                    |PlaybackStateCompat.ACTION_PLAY
                    |PlaybackStateCompat.ACTION_PAUSE
                    |PlaybackStateCompat.ACTION_STOP
                    |PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    |PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
            .setState(state, position, 1.0f)
            .build();
    }

    /**
     * Builds a "now playing" notification for a given track.
     * @param track
     * @param cover
     * @param isPlaying
     * @return
     */
    private Notification buildNotification(@NonNull Track track, @Nullable Bitmap cover, boolean isPlaying)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(PlayerService.this);

        builder
                // Add the metadata for the currently playing track
                .setContentTitle(track.getTrackTitle())
                .setContentText(track.getAlbumName() + " - " + track.getArtistName())
                //.setSubText(track.getArtistName())

                // Enable launching the player by clicking the notification
                //.setContentIntent(controller.getSessionActivity())

                // Stop the service when the notification is swiped away
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP))

                // Set the channel id
                .setChannelId(TAG)

                // Make the transport controls visible on the lock screen
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                // Add an app icon and set its accent color
                // Be careful about the color
                .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))

                // Skip Previous button
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_skip_previous_black_24dp,
                        getString(R.string.app_name),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))

                // Play/Pause button
                .addAction(new NotificationCompat.Action(
                        isPlaying ? R.drawable.ic_pause_black_24dp : R.drawable.ic_play_arrow_black_24dp,
                        getString(R.string.app_name),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)))

                // Skip Next button
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_skip_next_black_24dp,
                        getString(R.string.app_name),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)))
                // Take advantage of MediaStyle features
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0,1,2)

                        // Add a cancel button
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP)));

        if (cover!=null) {
            builder.setLargeIcon(cover);
        }

        return builder.build();
    }

    /**
     * Restore the service's state.
     * Restore the player's playlist, state, playback position, current track etc...
     *
     * @param library
     */
    private void restoreServiceState(@NonNull MediaLibrary library)
    {
        Log.d(TAG, "Restoring service state");

        thread = new HandlerThread(TAG);
        thread.start();

        PlaybackStateSaver playbackStateSaver = new PlaybackStateSaver(this);

        PlaylistQueue playlistQueue = new PlaylistQueue(playbackStateSaver);
        playbackStateSaver.restorePlaylistSettings(playlistQueue, library);

        playerStateNotifier = new PlayerStateNotifier();
        playbackWatcher = playerStateNotifier.getPlaybackWatcher()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PlaybackStateEvent>() {
                    @Override
                    public void accept(PlaybackStateEvent playbackStateEvent) throws Exception {
                        switch (playbackStateEvent.getNewState()) {
                            case PlaybackStateCompat.STATE_PLAYING:
                                onPlayEvent(playbackStateEvent);
                                break;
                            case PlaybackStateCompat.STATE_PAUSED:
                                onPauseEvent(playbackStateEvent);
                                break;
                        }
                    }
                });

        PlayerManager player = new PlayerManager(this, playlistQueue, playerStateNotifier, playbackStateSaver);
        playerInteractor = new PlayerInteractor(new Handler(thread.getLooper()), player);
        playbackStateSaver.restorePlaybackPosition(playerInteractor);

        Log.d(TAG, "Service state restored");
        initializationWatcher.onNext(true);
    }

    /**
     * Returns an observable notifying whether or not the service has been initialized.
     * Backed by a {@link BehaviorSubject}, will always return at least once.
     *
     * @return
     */
    public Observable<Boolean> getInitializationWatcher()
    {
        return initializationWatcher;
    }

    /**
     * Returns the player tied to this service.
     *
     * @return
     */
    public PlayerInterface getPlayer() {
        return playerInteractor;
    }
}
