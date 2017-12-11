package com.crevitus.haxdeputy;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class RingerStateService extends Service {

    RingStateDataReceiver _dataReceiver;
    int _previousIntent = 3;
    int _handOffCount;
    StringBuilder _receivedData;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        _handOffCount = 0;
        _receivedData = new StringBuilder();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        _dataReceiver = new RingStateDataReceiver();
        registerReceiver(_dataReceiver, filter);
        Log.e("notice", "receiving data");
        return 1;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(_dataReceiver);
    }

    class RingStateDataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences sharedPref = context.getSharedPreferences
                    (context.getString(R.string.preference_file_key),
                            Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            if(AudioManager.RINGER_MODE_CHANGED_ACTION.equals(intent.getAction())
                    && sharedPref.getInt
                    (context.getString(R.string.pref_trigger_count), 0) == 3)
            {
                AudioManager am = (AudioManager)getSystemService
                        (Context.AUDIO_SERVICE);
                if (_previousIntent == 2 && am.getRingerMode()
                        != AudioManager.RINGER_MODE_NORMAL) {
                    _receivedData.append(am.getRingerMode());
                }
                if((am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE
                        && _previousIntent == AudioManager.RINGER_MODE_SILENT)
                        || (am.getRingerMode() == AudioManager.RINGER_MODE_SILENT
                        && _previousIntent == AudioManager.RINGER_MODE_VIBRATE))
                {
                    _handOffCount++;
                }
                _previousIntent = am.getRingerMode();
            }
            if(_handOffCount == 3)
            {
                _handOffCount = 0;
                editor.putInt(context.getString(R.string.pref_trigger_count), 0);
                editor.putInt(context.getString(R.string.pref_running), 0);
                editor.commit();
                unregisterReceiver(this);
                Log.e("HaxDeputy received", bitStringToText(_receivedData)
                        + " this: " + _receivedData);
                Toast.makeText(context, "HaxDeputy received: "
                        + bitStringToText(_receivedData),
                        Toast.LENGTH_LONG).show();
            }
        }
        private String bitStringToText(StringBuilder input) {
            String output = "";
            for(int i = 0; i <= input.length() - 8; i+=8)
            {
                int bit = Integer.parseInt(input.substring(i, i+8), 2);
                output += (char) bit;
            }
            return output;
        }
    }
}