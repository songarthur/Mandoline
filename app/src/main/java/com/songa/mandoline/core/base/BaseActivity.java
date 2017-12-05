package com.songa.mandoline.core.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.songa.mandoline.MandolineApp;
import com.songa.mandoline.audio.service.PlayerService;
import com.songa.mandoline.audio.service.binding.PlayerServiceBindingListener;
import com.songa.mandoline.audio.service.binding.PlayerServiceBindingDelegate;
import com.songa.mandoline.core.AppRouter;

public abstract class BaseActivity extends AppCompatActivity implements PlayerServiceBindingListener
{
    private @Nullable PlayerServiceBindingDelegate playerBindingDelegate = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // permission check
        // the service needs the permissions to initialize since it uses MediaLibrary too
        if (MandolineApp.checkPermissions(this)) {
            playerBindingDelegate = new PlayerServiceBindingDelegate(this, this);
            playerBindingDelegate.bind();

        } else {
            AppRouter.goToPermissionPage(this);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (playerBindingDelegate!=null) {
            playerBindingDelegate.unbind();
        }
    }

    @Override
    public void onPlayerServiceAvailable() {}

    public final @Nullable PlayerService getPlayerService()
    {
        return playerBindingDelegate!=null ? playerBindingDelegate.getService() : null;
    }
}
