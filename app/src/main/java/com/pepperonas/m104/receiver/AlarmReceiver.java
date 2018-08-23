package com.pepperonas.m104.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pepperonas.m104.notification.NotificationNetwork;
import com.pepperonas.m104.utils.Log;

/**
 * @author Martin Pfeffer
 * <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        Log.w(TAG, "onReceive: Renewing Network-Notification...");
        NotificationNetwork.renew();
    }

}
