package com.szip.sportwatch.Fragment.ReportFragment.sport;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.szip.sportwatch.Fragment.BaseFragment;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.View.SportReportView;
import com.szip.sportwatch.View.SportSpeedView;

import java.util.Locale;

public class RunFragment extends BaseFragment {

    private TextView dataTv,timeTv,distanceTv,unitTv,kcalTv,sportTimeTv,averageTv1, averageTv2,averageTv3,averageTv4,averageTv5;
    private SportReportView tableView1, tableView2,tableView3, tableView4;
    private SportData sportData;
    private SportSpeedView sportSpeed;

    private String[] heartArray = new String[0];
    private String[] strideArray = new String[0];
    private String[] speedArray = new String[0];
    private String[] speedPerHourArray = new String[0];
    private String[] altitudeArray = new String[0];

    public RunFragment(SportData sportData) {
        this.sportData = sportData;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_run;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        initView();
        initData();
    }

    private void initView() {
        timeTv = getView().findViewById(R.id.timeTv);
        sportTimeTv = getView().findViewById(R.id.sportTimeTv);
        distanceTv = getView().findViewById(R.id.distanceTv);
        unitTv = getView().findViewById(R.id.unitTv);
        kcalTv = getView().findViewById(R.id.kcalTv);
        averageTv1 = getView().findViewById(R.id.averageTv1);
        averageTv2 = getView().findViewById(R.id.averageTv2);
        averageTv3 = getView().findViewById(R.id.averageTv3);
        averageTv4 = getView().findViewById(R.id.averageTv4);
        averageTv5 = getView().findViewById(R.id.averageTv5);
        tableView1 = getView().findViewById(R.id.tableView1);
        tableView2 = getView().findViewById(R.id.tableView2);
        tableView3 = getView().findViewById(R.id.tableView3);
        tableView4 = getView().findViewById(R.id.tableView4);
        sportSpeed = getView().findViewById(R.id.sportSpeed);
        dataTv = getView().findViewById(R.id.dataTv);

        if(sportData.type==6){
            ((TextView)getView().findViewById(R.id.sportIdTv)).setText(R.string.training);
            getView().findViewById(R.id.bgRl).setBackground(getView().getResources().getDrawable(R.drawable.sport_bg_purple));
            ((ImageView)getView().findViewById(R.id.bgIv)).setImageResource(R.mipmap.sport_bg_trainrun);

        }
    }

    private void initData() {
        heartArray = sportData.getHeartArray().split(",");
        strideArray = sportData.getStrideArray().split(",");
        speedArray = sportData.getSpeedArray().split(",");
        speedPerHourArray = sportData.getSpeedPerHourArray().split(",");
        altitudeArray = sportData.getAltitudeArray().split(",");
        dataTv.setText(sportData.step+"");
        timeTv.setText(DateUtil.getStringDateFromSecond(sportData.time,"MM/dd HH:mm:ss"));
        sportTimeTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                sportData.sportTime%3600/60,sportData.sportTime%3600%60));
        if (MyApplication.getInstance().getUserInfo().getUnit()==0){
            distanceTv.setText(String.format(Locale.ENGLISH,"%.2f",sportData.distance/1000f));
            unitTv.setText("km");
        } else{
            distanceTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.metric2Miles(sportData.distance*10)));
            unitTv.setText("mile");
        }
        kcalTv.setText(String.format(Locale.ENGLISH,"%.1f", sportData.calorie/1000f));
        averageTv1.setText(sportData.heart+"");
        averageTv2.setText(sportData.stride+"");
        averageTv3.setText(String.format(Locale.ENGLISH,"%.1f",sportData.speedPerHour/10f));
        averageTv4.setText(sportData.height+"");
        averageTv5.setText(String.format(Locale.ENGLISH,"%02d'%02d''",sportData.speed/60,sportData.speed%60));
        tableView1.addData(heartArray);
        tableView2.addData(strideArray);
        tableView3.addData(speedPerHourArray);
        tableView4.addData(altitudeArray);
        sportSpeed.addData(speedArray);

        if (MyApplication.getInstance().isMtk()){
            getView().findViewById(R.id.altitudeLl).setVisibility(View.GONE);
            getView().findViewById(R.id.speedPerHourLl).setVisibility(View.GONE);
        }else {

        }

    }
}
