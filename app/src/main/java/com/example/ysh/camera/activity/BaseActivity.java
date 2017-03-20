package com.example.ysh.camera.activity;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ysh on 2017/3/18.
 */

public class BaseActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 1;
    protected PermissionListener mPermissionListener;

    protected void requestPermissions(@NonNull String[] permissions, @NonNull PermissionListener permissionListener) {
        mPermissionListener = permissionListener;

        List<String> requestList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(permission);
            }
        }

        if (requestList.isEmpty()) {
            mPermissionListener.onGranted();
        } else {
            ActivityCompat.requestPermissions(this, requestList.toArray(new String[requestList.size()]), PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                List<String> deniedList = new ArrayList<>();
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        deniedList.add(permissions[i]);
                    }
                }

                if (deniedList.isEmpty()) {
                    mPermissionListener.onGranted();
                } else {
                    mPermissionListener.onDenied(deniedList);
                }

                break;
        }
    }

    protected interface PermissionListener {
        void onGranted();

        void onDenied(List<String> deniedList);
    }
}
