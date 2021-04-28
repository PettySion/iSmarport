package com.szip.sportwatch.Activity.diy;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.szip.sportwatch.Activity.BaseActivity;
import com.szip.sportwatch.BLE.BleClient;
import com.szip.sportwatch.BLE.EXCDController;
import com.szip.sportwatch.Model.EvenBusModel.UpdateView;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.FileUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.Util.ProgressHudModel;
import com.szip.sportwatch.Util.StatusBarCompat;
import com.szip.sportwatch.View.CircularImageView;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;


public class DIYActivity extends BaseActivity implements IDiyView{

    private RecyclerView clockRv;
    private Uri resultUri;
    private int progress = 0;

    private CircularImageView backgroundIv;
    private ImageView clockIv,diyIv;

    private boolean isSendPic = false;
    private IDiyPresenter iDiyPresenter;
    private int clock = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (MyApplication.getInstance().isMtk()){
            setContentView(R.layout.activity_diy);
            iDiyPresenter = new DiyPresenterImpl(getApplicationContext(),this);
        } else{
            setContentView(R.layout.activity_diy06);
            iDiyPresenter = new DiyPresenterImpl06(getApplicationContext(),this);
        }
        StatusBarCompat.translucentStatusBar(this,true);
        setAndroidNativeLightStatusBar(this,true);
        EventBus.getDefault().register(this);
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isSendPic){
            progress = 0;
            ProgressHudModel.newInstance().diss();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        FileUtil.getInstance().deleteFile(MyApplication.getInstance().getPrivatePath()+"crop.jpg");
    }

    private void initEvent() {
        findViewById(R.id.diyIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.rightIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 保存成功
                if (resultUri==null){
                    showToast(getString(R.string.upDownBackground));
                    return;
                }
                if (clock==-1){
                    showToast(getString(R.string.chooseClock));
                    return;
                }
                if (!ProgressHudModel.newInstance().isShow()){
                    ProgressHudModel.newInstance().show(DIYActivity.this,getString(R.string.loading),
                            getString(R.string.connect_error),10000);
                    if (MyApplication.getInstance().isMtk())
                        EXCDController.getInstance().initDialInfo();
                    else
                        BleClient.getInstance().writeForSendPicture(0,clock,0,0,new byte[0]);
                }
            }
        });
    }

    private void initView() {
        setTitleText(getString(R.string.diyDail));
        clockRv = findViewById(R.id.clockRv);
        clockIv = findViewById(R.id.clockIv);
        diyIv = findViewById(R.id.diyIv);
        iDiyPresenter.getViewConfig(clockRv);
    }

    String[] proj = { MediaStore.Images.Media.DATA };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == UCrop.RESULT_ERROR){
            showToast(getString(R.string.crop_pic_failed));
            return;
        }
        switch (requestCode){
            case 1:{
                if (data==null||data.getData()==null)
                    return;
                if(MathUitl.isJpgFile(data.getData(),this)){
                    iDiyPresenter.cropPhoto(data.getData());
                }else {
                    showToast(getString(R.string.chooseJpg));
                }

            }
            break;
            case  UCrop.REQUEST_CROP:{
                if (data!=null){
                    resultUri = UCrop.getOutput(data);
                    try {
                        backgroundIv.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (findViewById(R.id.bottomRl).getVisibility()==View.GONE){
                        findViewById(R.id.bottomRl).setVisibility(View.VISIBLE);
                    }
                }
            }
            break;
        }
    }

    /**
     * 更新数据显示
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdataView(UpdateView updateView){
        if(updateView.getState().equals("0")){//进度+1
            iDiyPresenter.sendDial(null,-1);
            progress++;
            ProgressHudModel.newInstance().setProgress(progress);
        }else if (updateView.getState().equals("1")){//完成
            isSendPic = false;
            progress = 0;
            ProgressHudModel.newInstance().diss();
            showToast(getString(R.string.diyDailOK));
        }else if (updateView.getState().equals("2")){//失败
            if (isSendPic){
                isSendPic = false;
                progress = 0;
                ProgressHudModel.newInstance().diss();
                showToast(getString(R.string.diyDailError));
            }
        }else {
            isSendPic = true;
            ProgressHudModel.newInstance().diss();
            iDiyPresenter.sendDial(resultUri,clock);
        }
    }

    @Override
    public void setView(boolean isCircle) {
        if (isCircle){
            diyIv.setImageResource(R.mipmap.diy_c);
            backgroundIv = findViewById(R.id.backgroundIv_c);
        }else {
            backgroundIv = findViewById(R.id.backgroundIv_r);
        }
    }

    @Override
    public void setDialView(int dial, int clock) {
        clockIv.setImageResource(dial);
        this.clock = clock;
    }

    @Override
    public void getCropPhoto(UCrop uCrop) {
        uCrop.start(this);
    }

    @Override
    public void setDialProgress(int num) {
        ProgressHudModel.newInstance().showWithPie(DIYActivity.this,getString(R.string.diyDailing),num,
                getString(R.string.diyDailError),100*1000);
    }
}
