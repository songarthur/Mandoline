package com.songa.mandoline.core;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.songa.mandoline.core.main.HomeActivity;
import com.songa.mandoline.core.permission.PermissionActivity;
import com.songa.mandoline.core.player.PlayerActivity;

/**
 * Class used to navigate from one activity to another.
 */
public class AppRouter
{
    private AppRouter() {}

    /**
     * Go to the permission page. Clears the backstack.
     *
     * @param context
     */
    public static void goToPermissionPage(@NonNull Context context)
    {
        Intent intent = new Intent(context, PermissionActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        context.startActivity(intent);
    }

    /**
     * Go to the home page. Clears the backstack.
     *
     * @param context
     */
    public static void goToHomePage(@NonNull Context context)
    {
        Intent intent = new Intent(context, HomeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        context.startActivity(intent);
    }

    /**
     * Go to the player.
     *
     * @param context
     */
    public static void goToPlayerPage(@NonNull Context context)
    {
        Intent intent = new Intent(context, PlayerActivity.class);
        context.startActivity(intent);
    }
}
