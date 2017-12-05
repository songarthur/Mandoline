package com.songa.mandoline.core;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.songa.mandoline.core.main.HomeActivity;
import com.songa.mandoline.core.permission.PermissionActivity;
import com.songa.mandoline.core.player.PlayerActivity;

public class AppRouter
{
    private AppRouter() {}

    public static void goToPermissionPage(@NonNull Context context)
    {
        Intent intent = new Intent(context, PermissionActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        context.startActivity(intent);
    }

    public static void goToHomePage(@NonNull Context context)
    {
        Intent intent = new Intent(context, HomeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        context.startActivity(intent);
    }

    public static void goToPlayerPage(@NonNull Context context)
    {
        Intent intent = new Intent(context, PlayerActivity.class);
        context.startActivity(intent);
    }
}
