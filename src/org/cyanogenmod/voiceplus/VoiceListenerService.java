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

import android.content.Intent;
import android.content.SharedPreferences;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class VoiceListenerService extends NotificationListenerService {
    SharedPreferences settings;
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (!Helper.GOOGLE_VOICE_PACKAGE.equals(sbn.getPackageName())) {
            return;
        }
        if (settings == null) {
            settings = getSharedPreferences("settings", MODE_PRIVATE);
        }
        if (null == settings.getString("account", null)) {
            return;
        }

        cancelNotification(Helper.GOOGLE_VOICE_PACKAGE, sbn.getTag(), sbn.getId());
        startService(new Intent(this, VoicePlusService.class)
                .setAction(VoicePlusService.ACTION_INCOMING_VOICE));
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }
}
