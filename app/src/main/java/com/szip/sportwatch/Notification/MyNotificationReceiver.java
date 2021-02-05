package com.szip.sportwatch.Notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.mediatek.ctrl.music.RemoteMusicController;
import com.mediatek.ctrl.notification.NotificationController;
import com.mediatek.ctrl.notification.NotificationData;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.Util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/12/27.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MyNotificationReceiver extends NotificationListenerService{

    private NotificationDataManager notificationDataManager = null;
    private ServiceHandler mServiceHandler;

    private Looper mServiceLooper;
    public MyNotificationReceiver() {
        notificationDataManager = new NotificationDataManager(this);
    }

    @Override
    public void onCreate() {
        mServiceLooper = Looper.getMainLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        super.onCreate();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i("notify******","Notification Posted, " + "ID: " + sbn.getId() + ", Package: "
                + sbn.getPackageName());
        configureMusicControl(sbn.getPackageName());
        LogUtil.getInstance().logd("notify******", "sdk version is " + android.os.Build.VERSION.SDK_INT);
        if(android.os.Build.VERSION.SDK_INT  < 18){
            Log.i("notify******","Android platform version is lower than 18.");
            return;
        }
        Notification notification = (Notification) sbn.getNotification();

        if (notification == null) {
            Log.e("notify******","Notification is null, return");
            return;
        }
        Log.i("notify******","packagename = " + sbn.getPackageName() + "tag = " +sbn.getTag()+"Id = " + sbn.getId());
        NotificationData notificationData = notificationDataManager.getNotificationData(
                notification, sbn.getPackageName(),sbn.getTag(),sbn.getId());

        notificationDataManager.sendNotificationData(notificationData);
    }

    /**
     * 判断手机有几个播放器
     * */
    private List<ResolveInfo> configureMusicControl(String packageName) {
        PackageManager localPackageManager = MyApplication.getInstance().getPackageManager();
        List<ResolveInfo> localList = localPackageManager.queryBroadcastReceivers(new Intent("android.intent.action.MEDIA_BUTTON"), 96);
        for (int i = 0; i < localList.size(); i++) {
            ResolveInfo localResolveInfo = localList.get(i);
            String pack = localResolveInfo.activityInfo.packageName;
            if (pack.equals(packageName)){
                LogUtil.getInstance().logd("notifySZIP******","set musice = "+pack);
                RemoteMusicController.getInstance(MyApplication.getInstance()).setMusicApp(pack);
            }

        }
        return localList;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("notify******","Notification Removed, " + "ID: " + sbn.getId() + ", Package: "
                + sbn.getPackageName());

        if(android.os.Build.VERSION.SDK_INT  < 18){
            Log.i("notify******","Android platform version is lower than 18.");
            return;
        }

        Notification notification = (Notification) sbn.getNotification();
        if (notification == null) {
            Log.e("notify******","Notification is null, return");
            return;
        }

        // the notification from onNotificationPosted and onNotificationRemoved will always
        // be different even they are from the same source.
        // e.g. the notification's texttitle and content from onNotificationRemoved is null
        NotificationData notificationData = notificationDataManager.getNotificationData(
                notification, sbn.getPackageName(),sbn.getTag(),sbn.getId());
        ArrayList<NotificationData> syncList = NotificationSyncList.getInstance().getSyncList();
        for(NotificationData data: syncList){
            if (data.equals(notificationData)) {
                // if the phone and watch is disconnected, the "connection status notification" will
                // be canceled(send and be canceled from MainService.java updateConnectionStatus()),
                // then watch will delete all notifications and so phone no need to send deletion
                // operation to watch again.
                // only the user cancel the notification from phone port then need send deletion
                // notification to watch to sync deletion opeartion.
                /*boolean isPackageNameSame = false;
                boolean isTextSame = false;
                boolean issContentSame = false;
                isPackageNameSame = data.getPackageName().equals(getPackageName());
                String textArray[] =data.getTextList();
                if(textArray!=null && textArray.length == 2 ){
                    isTextSame =textArray[0].equals(getString(R.string.notification_title));
                    issContentSame =textArray[1].equals(getString(R.string.notification_content));
                }
                if(!isPackageNameSame || !isTextSame || !issContentSame){*/
                NotificationController.getInstance(getBaseContext()).sendDelNotfications(
                        notificationData.getMsgId());
                Log.e("notify******","Notification Removed,sendDelNotfications");
                /*} else{
                   Log.e("notify******,"Notification Removed,do not sendDelNotfications");
                }*/
                syncList.remove(notificationData);
                NotificationSyncList.getInstance().saveSyncList();
                break;
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("notify******", "onUnbind()");
        return false;
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            //handle action: android.deletion.notification
            Intent intent = (Intent)msg.obj;
            int id = -1;
            if (intent != null) {
                id = intent.getIntExtra("msgid", -1);
                Log.e("notify******", "handleMessage,id = " + id);
            }
            ArrayList<NotificationData> syncList = NotificationSyncList.getInstance().getSyncList();
            for(NotificationData notificationData : syncList){
                if(notificationData.getMsgId() == id){
                    Log.e("notify******", "handleMessage,cancel notificaiton : " + notificationData);
                    NotificationSyncList.getInstance().removeNotificationData(String.valueOf(id));
                    NotificationSyncList.getInstance().saveSyncList();
                    // if the notification with the flag Notification.FLAG_ONGOING_EVENT ,
                    // e.g: "connection status notification"
                    // watch will also sync the deletion opeartion to phone, but the execution result
                    // of the below function will be unsuccessful. This is a known issue.
                    cancelNotification(notificationData.getPackageName(), notificationData.getTag(),
                            notificationData.getMsgId());
                    break;
                }
            }
            super.handleMessage(msg);
        }
    }

}
