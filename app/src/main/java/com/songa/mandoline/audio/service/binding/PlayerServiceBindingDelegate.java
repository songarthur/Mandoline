package com.songa.mandoline.audio.service.binding;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.songa.mandoline.audio.service.PlayerService;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Utility class used to bind a context to the {@link PlayerService}.
 * Notifies the listener the service is available only after the service finishes its initialization.
 */
public class PlayerServiceBindingDelegate
{
    private static final String TAG = PlayerServiceBindingDelegate.class.getSimpleName();

    private final @NonNull Context context;
    private final @NonNull ServiceConnection serviceConnectionListener;
    private final @NonNull PlayerServiceBindingListener requester;

    private @Nullable PlayerService service = null;

    public PlayerServiceBindingDelegate(@NonNull Context context, @NonNull PlayerServiceBindingListener requester)
    {
        this.context = context;
        this.requester = requester;
        this.serviceConnectionListener = new ServiceConnectionListener();
    }

    /**
     * Bind to the service.
     */
    public final void bind()
    {
        Log.d(TAG, "Starting and binding to PlayerService");

        Intent intent = new Intent(context, PlayerService.class);
        context.startService(intent);
        context.bindService(intent, serviceConnectionListener, 0);
    }

    /**
     * Unbind from the service.
     */
    public final void unbind()
    {
        Log.d(TAG, "Unbind from PlayerService");

        if (service!=null) {
            context.unbindService(serviceConnectionListener);
            service = null;
        }
    }

    /**
     * Returns the service if bound.
     * The service is invalid before onPlayerServiceAvailable has been called
     *
     * @return
     */
    public @Nullable PlayerService getService()
    {
        return service;
    }

    private class ServiceConnectionListener implements ServiceConnection
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder)
        {
            Log.d(TAG, "Received service binder");

            if (binder!=null && binder instanceof PlayerServiceBinder) {
                Log.d(TAG, "Service validated, waiting for service initialization");
                service = ((PlayerServiceBinder)binder).getService();
                service.getInitializationWatcher()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ServiceInitializationListener());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}
    }

    private class ServiceInitializationListener implements Observer<Boolean>
    {
        private Disposable subscription = null;

        @Override
        public void onSubscribe(Disposable d)
        {
            this.subscription = d;
        }

        @Override
        public void onNext(Boolean initialized)
        {
            if (initialized) {
                Log.d(TAG, "Service initialized");
                subscription.dispose();
                requester.onPlayerServiceAvailable();
            }
        }

        @Override
        public void onError(Throwable e) {}

        @Override
        public void onComplete() {}
    }
}
