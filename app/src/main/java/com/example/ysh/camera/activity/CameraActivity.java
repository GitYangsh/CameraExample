package com.example.ysh.camera.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.ysh.camera.R;
import com.example.ysh.camera.view.CameraShutterView;

import java.util.List;

/**
 * Created by ysh on 2017/3/16.
 */

public class CameraActivity extends BaseActivity implements CameraShutterView.onShutterListener{

    private static final String[] PERMISSIONS_ALL = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };
    private static final String[] PERMISSIONS_TAKE_PICTURE = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final String[] PERMISSIONS_TAKE_VIDEO = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    private CameraShutterView mShutterView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initView();

        requestPermissions(PERMISSIONS_ALL, new PermissionListener() {
            @Override
            public void onGranted() {
                Toast.makeText(CameraActivity.this, "permission granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(List<String> deniedList) {
                StringBuilder sb = new StringBuilder("permission denied:\n");
                for (String s : deniedList) {
                    sb.append(s + "\n");
                }
                Toast.makeText(CameraActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        mShutterView = (CameraShutterView) findViewById(R.id.camera_shutter);
        mShutterView.setShutterType(CameraShutterView.TYPE_TAKE_VIDEO);
        mShutterView.setListener(this);
    }

    @Override
    public void onTakePicture() {

    }

    @Override
    public void onStartRecording() {

    }

    @Override
    public void onFinishRecording() {

    }
}
