package com.szip.sportwatch.Fragment.ReportFragment.sport;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class OnfootFragment extends BaseFragment {

    private TextView timeTv,dataTv,distanceTv,unitTv,kcalTv,sportTimeTv, averageTv1,averageTv2,averageTv3,averageTv4,averageTv5;
    private SportReportView tableView1,tableView2,tableView3,tableView4;
    private SportData sportData;
    private SportSpeedView sportSpeed;

    private String[] heartArray = new String[0];
    private String[] speedArray = new String[0];
    private String[] strideArray = new String[0];
    private String[] speedPerHourArray = new String[0];
    private String[] altitudeArray = new String[0];
    public OnfootFragment(SportData sportData) {
        this.sportData = sportData;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_on_foot;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        initView();
        initData();
    }

    private void initView() {
        timeTv = getView().findViewById(R.id.timeTv);
        dataTv = getView().findViewById(R.id.dataTv);
        distanceTv = getView().findViewById(R.id.distanceTv);
        unitTv = getView().findViewById(R.id.unitTv);
        kcalTv = getView().findViewById(R.id.kcalTv);
        sportTimeTv = getView().findViewById(R.id.sportTimeTv);
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
    }

    private void initData() {
        heartArray = sportData.getHeartArray().split(",");
        strideArray = sportData.getStrideArray().split(",");
        speedArray = sportData.getSpeedArray().split(",");
        speedPerHourArray = sportData.getSpeedPerHourArray().split(",");
        altitudeArray = sportData.getAltitudeArray().split(",");
        timeTv.setText(DateUtil.getStringDateFromSecond(sportData.time,"MM/dd HH:mm:ss"));
        dataTv.setText(sportData.step+"");
        Log.i("DATA******","distance = "+sportData.distance);
        Log.i("DATA******","speed = "+sportData.speed);
        if (MyApplication.getInstance().getUserInfo().getUnit()==0){
            distanceTv.setText(String.format(Locale.ENGLISH,"%.2f",((sportData.distance+5)/10)/100f));
            averageTv3.setText(String.format(Locale.ENGLISH,"%.1f",sportData.speedPerHour/10f));
            unitTv.setText("km");
        } else{
            distanceTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.km2Miles(sportData.distance)));
            averageTv3.setText(String.format(Locale.ENGLISH,"%.1f",MathUitl.kmPerHour2MilesPerHour(sportData.speedPerHour)/10f));
            ((TextView)getView().findViewById(R.id.speedUnitTv)).setText("mile/h");
            speedPerHourArray = MathUitl.kmPerHour2MilesPerHour(speedPerHourArray);
            unitTv.setText("mile");
        }
        kcalTv.setText(String.format(Locale.ENGLISH,"%.1f",((sportData.calorie+55)/100)/10f));
        sportTimeTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                sportData.sportTime%3600/60,sportData.sportTime%3600%60));
        averageTv1.setText(sportData.heart+"");
        averageTv2.setText(sportData.stride+"");
        averageTv4.setText(sportData.height+"");
        averageTv5.setText(String.format(Locale.ENGLISH,"%02d'%02d''",sportData.speed/60,sportData.speed%60));
        tableView1.addData(heartArray);
        tableView2.addData(strideArray);
        tableView3.addData(speedPerHourArray);
        tableView4.addData(altitudeArray);
        sportSpeed.addData(speedArray);

        if(sportData.heart==0){
            getView().findViewById(R.id.heartLl).setVisibility(View.GONE);
        }
        if(sportData.height==0){
            getView().findViewById(R.id.altitudeLl).setVisibility(View.GONE);
        }
        if(sportData.stride==0){
            getView().findViewById(R.id.strideLl).setVisibility(View.GONE);
        }
        if(sportData.speed==0){
            getView().findViewById(R.id.speedLl).setVisibility(View.GONE);
        }
        if(sportData.speedPerHour==0){
            getView().findViewById(R.id.speedPerHourLl).setVisibility(View.GONE);
        }
    }
}
