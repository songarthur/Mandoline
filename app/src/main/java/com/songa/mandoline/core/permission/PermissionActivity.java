package com.songa.mandoline.core.permission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.songa.mandoline.R;
import com.songa.mandoline.core.AppRouter;
import com.songa.mandoline.core.main.HomeActivity;
import com.songa.mandoline.databinding.ActivityPermissionBinding;

public class PermissionActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final int PERMISSION_REQUEST_CODE = 200;

    private ActivityPermissionBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_permission);
        binding.button.setOnClickListener(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        int grantedStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (grantedStatus==PackageManager.PERMISSION_GRANTED) { finish(); }
    }

    @Override
    public void onClick(View v)
    {
        askForPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    AppRouter.goToHomePage(this);
                }
            }
        }
    }

    private void askForPermission()
    {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }
}
