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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class OutgoingSmsReceiver extends BroadcastReceiver {
    private static final String LOGTAG = "OutgoingSmsReceiver";

    private boolean canDeliverToAddress(Context context, Intent intent) {
        String address = intent.getStringExtra("destAddr");

        if (address == null)
            return false;
        if (address.startsWith("+") && !address.startsWith("+1"))
            return false;

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String country = tm.getNetworkCountryIso();
        if (country == null)
            country = tm.getSimCountryIso();
        if (country == null)
            return address.startsWith("+1"); /* Should never be reached. */

        if (!country.toUpperCase().equals("US") && !address.startsWith("+1"))
            return false;

        return true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context.getSharedPreferences("settings",
                Context.MODE_PRIVATE).getString("account", null) == null) {
            return;
        }

        if (!canDeliverToAddress(context, intent)) {
            String destination = intent.getStringExtra("destAddr");
            if (destination == null)
            destination = "(null)";
            Log.d(LOGTAG, "Sending <" + destination + "> via cellular instead of Google Voice.");
            return;
        }

        abortBroadcast();
        setResultCode(Activity.RESULT_CANCELED);

        intent.setClass(context, VoicePlusService.class);
        context.startService(intent);
    }
}
