package com.songa.mandoline.audio.service.binding;

/**
 * Listener called when a service is bound and initialized.
 */
public interface PlayerServiceBindingListener
{
    /**
     * Called by {@link PlayerServiceBindingDelegate} when the service is bound and initialized.
     */
    void onPlayerServiceAvailable();
}
