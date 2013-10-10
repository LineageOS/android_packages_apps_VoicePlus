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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class PackageChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return;
        }

        ComponentName listenerservice = new ComponentName(context, VoiceListenerService.class);
        ComponentName service = new ComponentName(context, VoicePlusService.class);
        ComponentName receiver = new ComponentName(context, OutgoingSmsReceiver.class);
        ComponentName activity = new ComponentName(context, VoicePlusSetup.class);

        PackageInfo pkg;
        try {
            pkg = pm.getPackageInfo(Helper.GOOGLE_VOICE_PACKAGE, 0);
        } catch (Exception e) {
            pkg = null;
        }

        if (pkg != null) {
            SharedPreferences settings =
                    context.getSharedPreferences("settings", Context.MODE_PRIVATE);

            if (!settings.getBoolean("pestered", false)) {
                Notification.Builder builder = new Notification.Builder(context);
                Notification n = builder
                .setSmallIcon(R.drawable.stat_sys_gvoice)
                .setContentText(context.getString(R.string.enable_voice_plus))
                .setTicker(context.getString(R.string.enable_voice_plus))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentIntent(PendingIntent.getActivity(context, 0,
                new Intent(context, VoicePlusSetup.class), 0)).build();

                ((NotificationManager)context.getSystemService(
                        Context.NOTIFICATION_SERVICE)).notify(1000, n);

                settings.edit().putBoolean("pestered", true).commit();
            }

            pm.setComponentEnabledSetting(activity,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
            pm.setComponentEnabledSetting(service,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
            pm.setComponentEnabledSetting(listenerservice,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
            context.startService(new Intent(context, VoicePlusService.class));
        } else {
            pm.setComponentEnabledSetting(activity,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
            pm.setComponentEnabledSetting(service,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
            pm.setComponentEnabledSetting(listenerservice,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
        }
    }
}
