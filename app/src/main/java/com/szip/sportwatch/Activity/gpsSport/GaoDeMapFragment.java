package com.szip.sportwatch.Activity.gpsSport;

import android.app.Dialog;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;
import com.szip.sportwatch.R;

public class GaoDeMapFragment extends DialogFragment implements LocationSource {
    private OnLocationChangedListener mListener;
    private TextView distanceTv,speedTv,calorieTv;
    private View mRootView;
    private MapView mapView;
    private AMap aMap;
    private Location location;

    private int speed;
    private float distance,calorie;


    public GaoDeMapFragment(int speed, float distance, float calorie, Location location) {
        this.speed = speed;
        this.distance = distance;
        this.calorie = calorie;
        this.location = location;
    }

    public void setData(int speed, float distance, float calorie){
        speedTv.setText(String.format("%d'%d''",speed/60,speed%60));
        distanceTv.setText(String.format("%.2f",distance/1000));
        calorieTv.setText(String.format("%.1f",calorie));
    }

    public void setLocation(Location location){
        if (mListener!=null)
            mListener.onLocationChanged(location);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_map, container, false);
        }
        initView();
        return mRootView;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if(window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            window.setWindowAnimations(R.style.MapAnim);
        }
        return dialog;
    }

    private void initView() {
        speedTv = mRootView.findViewById(R.id.speedTv);
        distanceTv = mRootView.findViewById(R.id.distanceTv);
        calorieTv = mRootView.findViewById(R.id.calorieTv);


        speedTv.setText(String.format("%d'%d''",speed/60,speed%60));
        distanceTv.setText(String.format("%.2f",distance/1000));
        calorieTv.setText(String.format("%.1f",calorie));
        mapView = mRootView.findViewById(R.id.gaodeMap);
        mRootView.findViewById(R.id.googleMap).setVisibility(View.GONE);
        mRootView.findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mapView.onCreate(getArguments());
                mapView.onResume();
                aMap = mapView.getMap();
                setUpMap();
            }
        },10);
    }

    @Override
    public void onPause() {
        if (mapView!=null){
            Log.d("LOCATION******","onPause");
            mapView.onDestroy();
        }
        super.onPause();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
    }

    /**
     * ????????????amap?????????
     */
    private void setUpMap() {
        // ??????????????????????????????
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.mipmap.sport_icon_direction));// ????????????????????????
        myLocationStyle.strokeColor(Color.TRANSPARENT);// ???????????????????????????
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);// ???????????????????????????
        myLocationStyle.showMyLocation(true);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// ??????????????????
        aMap.getUiSettings().setZoomControlsEnabled(false);// ??????????????????
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// ????????????????????????????????????
        aMap.moveCamera(CameraUpdateFactory.zoomTo(19f));
        aMap.setMyLocationEnabled(true);// ?????????true??????????????????????????????????????????false??????????????????????????????????????????????????????false
        if (mListener!=null)
            mListener.onLocationChanged(location);
    }
}
