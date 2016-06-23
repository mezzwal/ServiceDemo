package com.nubiz.servicedemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

/**
 * Created by admin on 6/13/2016.
 */
public class Receiver extends BroadcastReceiver {
    private static final int START_DELAY = 2000;

    public void onReceive(final Context paramContext, final Intent paramIntent) {
        if (paramIntent.getAction().contains("MANUALLY_STARTED")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    paramContext.startService(new Intent(paramContext, UpdateService.class).putExtra
                            ("type", paramIntent.getAction().substring(paramIntent.getAction().lastIndexOf(".") + 1)));
                }
            }, START_DELAY);
        } else
            paramContext.startService(new Intent(paramContext, UpdateService.class).putExtra
                    ("type", paramIntent.getAction().substring(paramIntent.getAction().lastIndexOf(".") + 1)));

    }
}
