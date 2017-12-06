package com.songa.mandoline.core.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.songa.mandoline.MandolineApp;
import com.songa.mandoline.audio.library.MediaLibrary;
import com.songa.mandoline.audio.service.PlayerService;
import com.songa.mandoline.audio.service.binding.PlayerServiceBindingListener;
import com.songa.mandoline.audio.service.binding.PlayerServiceBindingDelegate;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Base fragment class. <br>
 *
 * Automatically binds/unbinds to the music playing service if the permissions allow it. <br>
 *
 * If the {@link MediaLibrary} is not available, will request for it and call onMediaLibraryInitialized
 * when available, at the earliest after onViewCreated. <br>
 *
 * If the {@link MediaLibrary} is already available, will not call onMediaLibraryInitialized.<br>
 */
public abstract class BaseFragment extends Fragment implements PlayerServiceBindingListener
{
    private @Nullable PlayerServiceBindingDelegate playerBindingDelegate = null;
    private @Nullable MediaLibrary mediaLibrary = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // permission check
        // the service needs the permissions to initialize since it uses MediaLibrary too
        if (MandolineApp.checkPermissions(getContext())) {

            playerBindingDelegate = new PlayerServiceBindingDelegate(getContext(), this);
            playerBindingDelegate.bind();

            // fetch the instance synchronously if available
            mediaLibrary = MediaLibrary.tryToGetInstance();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // if the instance was not available we fetch it asynchronously
        // this call is made later on onViewCreated to make sure the callback onMediaLibraryInitialized()
        // is called after the views are available
        if (mediaLibrary==null && MandolineApp.checkPermissions(getContext())) {

            MediaLibrary.getLibraryProvider(getContext())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<MediaLibrary>() {
                        @Override
                        public void accept(MediaLibrary mediaLibrary) throws Exception {
                            BaseFragment.this.mediaLibrary = mediaLibrary;
                            onMediaLibraryInitialized();
                        }
                    });
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (playerBindingDelegate!=null) {
            playerBindingDelegate.unbind();
        }
    }

    /**
     * Called when the player service become available.
     */
    @Override
    public void onPlayerServiceAvailable() {}

    /**
     * Called only when the Media Library becomes available after this fragment reaches onCreate.
     */
    public void onMediaLibraryInitialized() {}

    /**
     * @return Returns the player service if available.
     */
    public final @Nullable PlayerService getPlayerService()
    {
        return playerBindingDelegate !=null ? playerBindingDelegate.getService() : null;
    }

    /**
     * @return Return the media library if available.
     */
    public final @Nullable MediaLibrary getMediaLibrary()
    {
        return mediaLibrary;
    }
}
