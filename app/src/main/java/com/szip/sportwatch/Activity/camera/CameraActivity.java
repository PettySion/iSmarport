package com.szip.sportwatch.Activity.camera;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.szip.sportwatch.Activity.BaseActivity;
import com.szip.sportwatch.BLE.BleClient;
import com.szip.sportwatch.BLE.EXCDController;
import com.szip.sportwatch.Interface.OnCameraListener;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.StatusBarCompat;

import java.io.IOException;

public class CameraActivity extends BaseActivity{

    private FrameLayout preview;

    private ImageView switchIv;

    private boolean cameraAble = true;

    private ICameraPresenter cameraPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);
        StatusBarCompat.translucentStatusBar(CameraActivity.this,true);
        initView();
        cameraPresenter = new CameraPresenterImp(getApplicationContext());
        cameraPresenter.initCamera(preview);


        switchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    changeCamera();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.getInstance().isMtk())
            EXCDController.getInstance().setOnCameraListener(onCameraListener);
        else
            BleClient.getInstance().setOnCameraListener(onCameraListener);
        cameraPresenter.registerSensor();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (MyApplication.getInstance().isMtk())
            EXCDController.getInstance().setOnCameraListener(null);
        else
            BleClient.getInstance().setOnCameraListener(null);
        cameraPresenter.unRegisterSensor();

    }

    @Override
    protected void onDestroy() {
        // ??????Camera??????????????????
        cameraPresenter.removeCamera();
        super.onDestroy();
    }

    private void initView() {
        preview = findViewById(R.id.camera_preview);
        switchIv = findViewById(R.id.switchIv);
    }


    private void changeCamera() throws IOException{
        cameraPresenter.changeCamera();
    }
    private OnCameraListener onCameraListener = new OnCameraListener() {
        @Override
        public void onCamera(int flag) {
            if (flag == 0)
                finish();
            else {
                if (cameraAble){
                    cameraAble = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cameraAble = true;
                        }
                    },1000);
                    cameraPresenter.takePicture();
                }
            }
        }
    };
}
