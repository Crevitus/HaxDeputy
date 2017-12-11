package com.crevitus.haxdeputy;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SMSMessageService extends Service {

    Messenger _messenger = new Messenger(new SMSHandler());
    @Override
    public IBinder onBind(Intent intent) {
        return _messenger.getBinder();
    }

    class SMSHandler extends Handler {
        final String SMS_SUCCESS_MESSAGE = "SMS Sent";
        final String SMS_FAILURE_MESSAGE = "SMS failed, please try again!";
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(msg.getData().getString("number"), null,
                        msg.getData().getString("message"), null, null);
                Toast.makeText(getApplicationContext(), SMS_SUCCESS_MESSAGE,
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), SMS_FAILURE_MESSAGE,
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
