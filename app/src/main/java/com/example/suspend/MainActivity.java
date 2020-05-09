package com.example.suspend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {
    private Button getPermission, start, show, stop;
    NotificationManager notificationManager;
    TextView show_text;
    String info = "";
    Button test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermission = findViewById(R.id.getPermission);
        start = findViewById(R.id.startSuspend);
        show = findViewById(R.id.show);
        stop = findViewById(R.id.stop);
        show_text = findViewById(R.id.show_text);
        test = findViewById(R.id.test);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatusBarNotification[] sbn = notificationManager.getActiveNotifications();

                for (int i = 0; i < sbn.length; i++) {
                    info += "id：" + sbn[i].getId() + "内容：" + sbn[i].getNotification().extras.get("android.text") + "\n";
                }
                show_text.setText(info);

            }
        });

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationManager.cancelAll();
                info = "";
                show_text.setText("");
                stopService(new Intent(MainActivity.this, SuspendServer.class));
            }
        });

        EventBus.getDefault().register(this);
        getPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())), 1);
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, SuspendServer.class));
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMessage(String s) {
        if (s.equals("exit")) {
            stopService(new Intent(this, SuspendServer.class));
            System.exit(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private String TAG = "aaaa";



    public static void gotoQQ(final Context context, String qq) {
        try {
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qq;
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
} catch (Exception e) {
        Toast.makeText(context, "手机QQ未安装或该版本不支持", Toast.LENGTH_SHORT).show();
        }
        }

}
