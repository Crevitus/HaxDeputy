package com.crevitus.haxdeputy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;

public class RingerStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        int triggerCount = sharedPref.getInt(context.getString
                (R.string.pref_trigger_count),0);

        if (AudioManager.RINGER_MODE_CHANGED_ACTION.equals(intent.getAction())
                && triggerCount != 3)
        {
            editor.putLong(context.getString(R.string.pref_received_time),
                    System.currentTimeMillis());
            if ((System.currentTimeMillis() - sharedPref.getLong(context.getString
                    (R.string.pref_received_time), 1000)) < 700)
            {
                editor.putLong(context.getString(R.string.pref_received_time),
                        System.currentTimeMillis());
                editor.putInt(context.getString(R.string.pref_trigger_count),
                        (triggerCount + 1));
                Log.e("trigger count: ", ""+(triggerCount + 1));
            }
            editor.commit();
        }
        if (sharedPref.getInt(context.getString(R.string.pref_trigger_count), 0)
                == 3 && sharedPref.getInt
                (context.getString(R.string.pref_running), 0) != 1)
        {
            context.startService(new Intent(context, RingerStateService.class));
            editor.putInt(context.getString(R.string.pref_running), 1);
            editor.commit();
        }
    }
}
