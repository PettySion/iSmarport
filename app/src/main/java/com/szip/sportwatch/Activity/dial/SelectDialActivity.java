package com.szip.sportwatch.Activity.dial;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.szip.sportwatch.Activity.BaseActivity;
import com.szip.sportwatch.Activity.diy.DIYActivity;
import com.szip.sportwatch.BLE.BleClient;
import com.szip.sportwatch.BLE.EXCDController;
import com.szip.sportwatch.Model.EvenBusModel.UpdateDialView;
import com.szip.sportwatch.Model.EvenBusModel.UpdateView;
import com.szip.sportwatch.Model.SendDialModel;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Service.MainService;
import com.szip.sportwatch.Util.ProgressHudModel;
import com.szip.sportwatch.Util.StatusBarCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SelectDialActivity extends BaseActivity implements ISelectDialView{


    private ISelectDialPresenter iSelectDialPresenter;
    private String pictureUrl;
    private ImageView dialIv,changeIv;
    private int progress = 0;
    private boolean isSendPic = false;

    private String faceType = "";
    private boolean isCircle = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_select_dial);
        if (MyApplication.getInstance().isMtk()) {
            iSelectDialPresenter = new SelectDialPresenterImpl(getApplicationContext(),this);
        } else{
            if (MyApplication.getInstance().getFaceType().equals("454*454"))
                iSelectDialPresenter = new SelectDialPresenterWithFileImpl(getApplicationContext(),this);
            else
                iSelectDialPresenter = new SelectDialPresenterImpl06(getApplicationContext(),this);
        }
        isCircle = MyApplication.getInstance().isCircle();
        faceType = MyApplication.getInstance().getFaceType();
        Log.i("data******","faceType = "+faceType);
        StatusBarCompat.translucentStatusBar(this,true);
        setAndroidNativeLightStatusBar(this,true);
        EventBus.getDefault().register(this);
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
        iSelectDialPresenter.setViewDestory();
        EventBus.getDefault().unregister(this);
    }


    private void initEvent() {
        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.rightIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ProgressHudModel.newInstance().isShow()&&pictureUrl!=null){
                    ProgressHudModel.newInstance().show(SelectDialActivity.this,getString(R.string.loading),
                            getString(R.string.connect_error),30000);
                    boolean hasFile = MainService.getInstance().downloadFirmsoft(pictureUrl);
                    if(hasFile)
                        iSelectDialPresenter.startToSendDial();
                }
            }
        });
    }

    private void initView() {
        setTitleText(getString(R.string.face));
        RecyclerView dialRv = findViewById(R.id.dialRv);
        changeIv = findViewById(R.id.changeIv);
        iSelectDialPresenter.getViewConfig(dialRv);
    }

    /**
     * 更新数据显示
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdataView(UpdateDialView updateView){
        if(updateView.getType()==0){//进度+1
            progress++;
            iSelectDialPresenter.sendDial(null,-1);
            ProgressHudModel.newInstance().setProgress(progress);
        }else if (updateView.getType()==1){//完成
            isSendPic = false;
            progress = 0;
            ProgressHudModel.newInstance().diss();
            showToast(getString(R.string.diyDailOK));
        }else if (updateView.getType()==2){//失败
            if (isSendPic){
                isSendPic = false;
                progress = 0;
                ProgressHudModel.newInstance().diss();
                showToast(getString(R.string.diyDailError));
            }
        }else if (updateView.getType()==3){//准备开始
            Log.i("data******","准备发送数据");
            isSendPic = true;
            progress = 0;
            ProgressHudModel.newInstance().diss();
            String fileNames[] = pictureUrl.split("/");
            iSelectDialPresenter.sendDial(fileNames[fileNames.length-1],updateView.getData());
        }else if (updateView.getType() == 4){//断点续传
            iSelectDialPresenter.resumeSendDial(updateView.getData());
        }else {
            isSendPic = false;
            progress = 0;
            ProgressHudModel.newInstance().diss();
            showToast(getString(R.string.diyDailOK));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendPicture(SendDialModel sendDialModel){
        if (sendDialModel.isLoadSuccess()){
            iSelectDialPresenter.startToSendDial();
        }else {
            showToast(getString(R.string.httpError));
            ProgressHudModel.newInstance().diss();
        }
    }

    @Override
    public void initList(boolean initSuccess) {
        if (!initSuccess)
            showToast(getString(R.string.httpError));
        initView();
        initEvent();
    }

    @Override
    public void setView(String id, String pictureId) {
        if (isCircle){
            changeIv.setImageResource(R.mipmap.change_watch_c);
            dialIv = findViewById(R.id.dialIv_c);
        }else {
            if (faceType.indexOf("320*385")>=0){
                changeIv.setImageResource(R.mipmap.change_watch_06);
                dialIv = findViewById(R.id.dialIv_r06);
            }else {
                dialIv = findViewById(R.id.dialIv_r);
            }
        }
        this.pictureUrl = pictureId;
        Glide.with(this).load(id).into(dialIv);
    }

    @Override
    public void setDialView(String dialId, String pictureId) {
        if (dialId==null){
            startActivity(new Intent(SelectDialActivity.this, DIYActivity.class));
            finish();
        }else {
            this.pictureUrl = pictureId;
            Glide.with(this).load(dialId).into(dialIv);
        }
    }

    @Override
    public void setDialProgress(int max) {
        ProgressHudModel.newInstance().showWithPie(this,getString(R.string.diyDailing),max,
                getString(R.string.diyDailError),5*60*1000);
    }
}
