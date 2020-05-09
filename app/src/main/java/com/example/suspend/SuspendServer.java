package com.example.suspend;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class SuspendServer extends Service {
    public SuspendServer() {
    }
    private WindowManager windowManager;
    ImageView control;
    LinearLayout tools;
    WindowManager.LayoutParams ControlParams;
    WindowManager.LayoutParams ToolsParams;
    boolean canMove = false;
    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
    WindowManager.LayoutParams params = new WindowManager.LayoutParams();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private String TAG = "aaaa";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                EventBus.getDefault().post("exit");
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMessage(String s) {
        if (s.equals("close")) {
            notificationManager.cancel(1);
            handler.sendEmptyMessageDelayed(1, 1000);
            onDestroy();
        }
        if (s.equals("show")) {
            if (control != null) {
                control.setVisibility(View.VISIBLE);
            }
        }
        if (s.equals("hide")) {
            if (control != null) {
                control.setVisibility(View.GONE);
            }
        }
    }

    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

     //   notificationManager.notify(1, getNotification());
        getNotificationCompat();

    }

    private  void getNotificationCompat(){

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder( this )
                        .setContentText(" ")
                .setContentTitle("备忘")
                        // 点击消失
                        .setAutoCancel( true )
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        // 通知产生的时间，会在通知信息里显示
                        // 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                        .setDefaults( Notification.DEFAULT_VIBRATE | Notification.DEFAULT_ALL | Notification.DEFAULT_SOUND );
        //2：如果是8以上的系统，则新建一个消息通道，传一个channelId
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "sample";//消息通道的id，以后可以通过该id找到该消息通道
            String channelName = "固定通知";//消息通道的name
            // 通知的优先级，作用就是优先级的不同。可以导致消息出现的形式不一样。
            // HIGH是会震动并且出现在屏幕的上方。设置优先级为low或者min时。来通知时都不会震动，且不会直接出现在屏幕上方
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            notifyBuilder.setChannelId(channelId);
        }
        PendingIntent resultPendingIntent =

                PendingIntent.getActivity( this, 0, new Intent(this,MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT );
        notifyBuilder.setContentIntent( resultPendingIntent );
        Notification notification=notifyBuilder.build();
        notification.flags= Notification.PRIORITY_HIGH;
        notificationManager.notify( 66, notification );
        notificationManager.notify(88,notification);
        notificationManager.notify(99,notification);
        //后添加的在SBN数组最前面
        for (int i=0;i<notificationManager.getActiveNotifications().length;i++){
            StatusBarNotification aa=notificationManager.getActiveNotifications()[i];

            Log.i(TAG, ""+aa.getId()+"----"+aa.getNotification().extras.get("android.showWhen")+"----"+aa.getPostTime());//extras保存的关于notification的全部信息 具体的key可以点进去看源码
        }

    }


    private Notification getNotification() {
        //1:获取系统提供的通知管理服务
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);

        //2：如果是8以上的系统，则新建一个消息通道，传一个channelId
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "sample";//消息通道的id，以后可以通过该id找到该消息通道
            String channelName = "固定通知";//消息通道的name
            // 通知的优先级，作用就是优先级的不同。可以导致消息出现的形式不一样。
            // HIGH是会震动并且出现在屏幕的上方。设置优先级为low或者min时。来通知时都不会震动，且不会直接出现在屏幕上方
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }
        //view 布局导入layout布局
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_layout);
        Intent intent = new Intent(this, NotificationClickReceiver.class);
        intent.setAction("show");
        PendingIntent show = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent intent1 = new Intent(this, NotificationClickReceiver.class);
        intent1.setAction("hide");
        PendingIntent hide = PendingIntent.getBroadcast(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent intent2 = new Intent(this, NotificationClickReceiver.class);
        intent2.setAction("exit");
        PendingIntent exit = PendingIntent.getBroadcast(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        //把PendingIntent加入Remote布局中
//        views.setOnClickPendingIntent(R.id.show, show);
//        views.setOnClickPendingIntent(R.id.hide, hide);
//        views.setOnClickPendingIntent(R.id.exit, exit);

        builder.setSmallIcon(R.mipmap.ic_launcher).setAutoCancel(true)
                //      .setContentTitle("悬浮框显示中")
                //    .setContentText("若该通知消失请重启APP!")
//                .setContentText(TimeUtil.getNowMDHMSTime())
                //  .setWhen(System.currentTimeMillis())
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setCustomContentView(views);


        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT; // 设置常驻，不能滑动取消
        return notification;
    }
    boolean opened=false;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //showFloatingWindow();
        //    FloatingButton();
      //  FloatingControl();
        if (!opened){
            EventBus.getDefault().register(this);
            opened=true;
        }

        return START_STICKY;
    }

    private ImageView imageView;
    private Button button;

//    private void FloatingButton() {
//        final LinearLayout linearLayout = new LinearLayout(this);
//        button = new Button(this);
//        button.setText("长按震动");
//        final FloatingOnTouchListener floating = new FloatingOnTouchListener();
//        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        lp.format = PixelFormat.TRANSLUCENT;
//        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.x = 0;
//        lp.y = 0;
//        lp.gravity=Gravity.CENTER;
//        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//        linearLayout.setPadding(30, 10, 30, 10);
//        linearLayout.setOnTouchListener(floating);
//        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                return false;
//            }
//        });
//        linearLayout.setBackgroundColor(Color.parseColor("#ffffff"));
//        final TextView textView = new TextView(this);
//        ImageView imageView = new ImageView(this);
//        imageView.setImageResource(R.mipmap.game);
//
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doStartApplicationWithPackageName("com.tencent.tmgp.sgame");
//            }
//        });
//        ImageView imageView1 = new ImageView(this);
//        imageView1.setImageResource(R.mipmap.qq);
//
//        imageView1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doStartApplicationWithPackageName("com.tencent.mobileqq");
//            }
//        });
//        ImageView imageView2 = new ImageView(this);
//        imageView2.setImageResource(R.mipmap.wx);
//
//        imageView2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doStartApplicationWithPackageName("com.tencent.mm");
//            }
//        });
//        linearLayout.addView(imageView);
//        linearLayout.addView(imageView1);
//        linearLayout.addView(imageView2);
//        control = new ImageView(this);
//        control.setImageResource(R.mipmap.float_off);
//        control.setOnTouchListener(floating);
//        windowManager.addView(control, lp);
//
//    }
//




    private void FloatingControl() {
        control = new ImageView(this);
        control.setImageResource(R.mipmap.float_off);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        ControlParams = new WindowManager.LayoutParams();
        ControlParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        ControlParams.format = PixelFormat.TRANSLUCENT;
        ControlParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        ControlParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        ControlParams.gravity = Gravity.CENTER;
        ControlParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        control.setOnTouchListener(new FloatingOnTouchListener());
        ToolsParams=new WindowManager.LayoutParams();
        ToolsParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        ToolsParams.format = PixelFormat.TRANSLUCENT;
        ToolsParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        ToolsParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        ToolsParams.gravity = Gravity.BOTTOM;
        ToolsParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        tools=new LinearLayout(this);
        tools.setPadding(10,20,20,10);
        ImageView qq=new ImageView(this);
        qq.setImageResource(R.mipmap.qq);
        ImageView wx=new ImageView(this);
        wx.setImageResource(R.mipmap.wx);
        ImageView game =new ImageView(this);
        game.setImageResource(R.mipmap.game);
        qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doStartApplicationWithPackageName("com.tencent.mobileqq");
            }
        });
        wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doStartApplicationWithPackageName("com.tencent.mm");
            }
        });
        game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doStartApplicationWithPackageName("com.tencent.tmgp.sgame");
            }
        });
        ImageView gb=new ImageView(this);
        gb.setImageResource(R.mipmap.guobiao);
        gb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( imageView.getVisibility()==View.GONE){
                    imageView.setVisibility(View.VISIBLE);
                }else {
                    imageView.setVisibility(View.GONE);
                }

            }
        });

        tools.addView(qq);
        tools.addView(wx);
        tools.addView(game);
        tools.addView(gb);
        tools.setVisibility(View.GONE);
        showFloatingWindow();
        windowManager.addView(control, ControlParams);
        windowManager.addView(tools,ToolsParams);

    };
    private void showFloatingWindow() {

            // 新建悬浮窗控件
            imageView = new ImageView(this);
            imageView.setImageResource(R.mipmap.guobiao);
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.format = PixelFormat.TRANSLUCENT;
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.x = 176;
            layoutParams.y = -128;
            // 将悬浮窗控件添加到WindowManager
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            imageView.setVisibility(View.GONE);
            windowManager.addView(imageView, layoutParams);

    }


    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ControlParams = (WindowManager.LayoutParams) control.getLayoutParams();
            Log.i(TAG, "变化前: " + ControlParams.x + "y" + ControlParams.y);
            Point point = new Point();
            windowManager.getDefaultDisplay().getRealSize(point);
            if (msg.arg1 > point.x / 2) { //在右侧区域
                ControlParams.x = ControlParams.x + (point.x - msg.arg1);
                control.setImageResource(R.mipmap.float_right);
            } else {//在左侧区域
                ControlParams.x = ControlParams.x - msg.arg1;
                control.setImageResource(R.mipmap.float_left);
            }
            Log.i(TAG, "变化后: " + ControlParams.x + "y" + ControlParams.y);
            canMove = false;
            windowManager.updateViewLayout(control, ControlParams);
        }
    };


    //移动悬浮框
    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;
        String TAG = "aaaa";
        WindowManager.LayoutParams param;
        private int sx, sy, fx, fy;
        private boolean isClick = false;

        @Override
        public boolean onTouch(final View view, MotionEvent event) {
            param = (WindowManager.LayoutParams) view.getLayoutParams();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //获取手指按下的位置
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    sx = (int) event.getRawX();
                    sy = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //获取移动中的坐标
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    //计算移动的距离变化
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    //在原来坐标的基础上加上变化的坐标
                    param.x = param.x + movedX;
                    param.y = param.y + movedY;
                    // 更新悬浮窗控件布局
                    windowManager.updateViewLayout(view, param);
                    break;
                case MotionEvent.ACTION_UP:
                    fx = (int) event.getRawX();
                    fy = (int) event.getRawY();
                    //通过触摸前后悬浮窗坐标的变化大小 判断是点击还是触摸
                    if (Math.abs(fx - sx) < 5 && Math.abs(fy - sy) < 5) {
                        if (tools.getVisibility()==View.GONE){
                            tools.setVisibility(View.VISIBLE);
                        }else {
                            tools.setVisibility(View.GONE);
                        }
                        isClick = true;
                    }
                    break;
                default:
                    break;
            }
            return !isClick;
        }
    }

    public void startVibrate(long[] second, boolean repeat) {
        int isRepeat = repeat ? 1 : -1;
        Vibrator vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(second, isRepeat);
    }

    public void stopVibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.cancel();
    }

    //通过包名启动应用
    private void doStartApplicationWithPackageName(String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            //获取app信息
            packageinfo = getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "获取APP信息失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：package name.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            startActivity(intent);
        }
    }
}
