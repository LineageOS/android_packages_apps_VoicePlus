/*
 * Copyright (C) 2013 CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cyanogenmod.voiceplus;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.PowerManager;

public class Helper {
    public static final String GOOGLE_VOICE_PACKAGE = "com.google.android.apps.googlevoice";

    static PowerManager.WakeLock wakeLock;
    static WifiManager.WifiLock wifiLock;
    public static void acquireTemporaryWakelocks(Context context, long timeout) {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP| PowerManager.ON_AFTER_RELEASE, "PushSMS");
        }
        wakeLock.acquire(timeout);

        if (wifiLock == null) {
            WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            wifiLock = wm.createWifiLock("PushSMS");
        }

        wifiLock.acquire();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                wifiLock.release();
            }
        }, timeout);
    }
}
