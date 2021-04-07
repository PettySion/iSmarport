package com.szip.sportwatch.Activity.Fragment.ReportFragment.sport;

import android.os.Bundle;
import android.widget.TextView;

import com.szip.sportwatch.Activity.Fragment.BaseFragment;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.View.SportReportView;

import java.util.Locale;

public class SurfingFragment extends BaseFragment {
    private TextView timeTv,sportTimeTv,kcalTv,distanceTv,unitTv, averageTv1,averageTv2;
    private SportReportView tableView1,tableView2;
    private SportData sportData;

    private String[] heartArray = new String[0];
    private String[] speedPerHourArray = new String[0];

    public SurfingFragment(SportData sportData) {
        this.sportData = sportData;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sufing;
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
        kcalTv = getView().findViewById(R.id.kcalTv);
        unitTv = getView().findViewById(R.id.unitTv);
        tableView1 = getView().findViewById(R.id.tableView1);
        tableView2 = getView().findViewById(R.id.tableView2);
        averageTv1 = getView().findViewById(R.id.averageTv1);
        averageTv2 = getView().findViewById(R.id.averageTv2);
    }

    private void initData() {
        heartArray = sportData.getHeartArray().split(",");
        speedPerHourArray = sportData.getSpeedPerHourArray().split(",");

        timeTv.setText(DateUtil.getStringDateFromSecond(sportData.time,"MM/dd HH:mm:ss"));
        sportTimeTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                sportData.sportTime%3600/60,sportData.sportTime%3600%60));
        kcalTv.setText(String.format(Locale.ENGLISH,"%.1f", sportData.calorie/1000f));
        if (MyApplication.getInstance().getUserInfo().getUnit()==0){
            distanceTv.setText(String.format(Locale.ENGLISH,"%.2f",sportData.distance/1000f));
            unitTv.setText("km");
        } else{
            distanceTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.metric2Miles(sportData.distance*10)));
            unitTv.setText("mile");
        }
        averageTv1.setText(sportData.heart+"");
        averageTv2.setText(String.format(Locale.ENGLISH,"%.1f",sportData.speedPerHour/10f));

        tableView1.addData(heartArray);
        tableView2.addData(speedPerHourArray);
    }
}
