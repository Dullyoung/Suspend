package com.example.suspend;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationClickReceiver extends BroadcastReceiver {
    String TAG = "aaaa";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = intent.getAction();
        if (action.equals("show")) {
            EventBus.getDefault().post("show");
        }
        if (action.equals("hide")) {
            EventBus.getDefault().post("hide");
        }
        if (action.equals("exit")) {
            EventBus.getDefault().post("close");

        }
    }
}
