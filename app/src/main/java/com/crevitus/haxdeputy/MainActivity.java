package com.crevitus.haxdeputy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button _sendBtn;
    EditText _txtphoneNo;
    EditText _txtMessage;
    TextView _txtStatus;
    final String SMS_SUCCESS_MESSAGE = "SMS Sent";
    final String SMS_FAILURE_MESSAGE = "SMS failed, please try again!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _txtStatus= (TextView) findViewById(R.id.txtPermStatus);
        _sendBtn = (Button) findViewById(R.id.btnSendSMS);
        Button btnPerm = (Button) findViewById(R.id.btnRequestPermission);
        _txtphoneNo = (EditText) findViewById(R.id.editText);
        _txtMessage = (EditText) findViewById(R.id.editText2);

        _sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                    sendSMSMessage();
                    _txtMessage.setText("");
            }
        });

        btnPerm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    _txtStatus.setText(getString(R.string.txt_permission_status_not_granted));
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.SEND_SMS},
                            Constants.PERMISSIONS_REQUEST_SMS);
                }
                else {
                    _txtStatus.setText(getString(R.string.txt_permission_status_granted));
                }
            }
        });
    }
    public void sendSMSMessage() {
        String phoneNo = _txtphoneNo.getText().toString();
        String message = _txtMessage.getText().toString();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), SMS_SUCCESS_MESSAGE, Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), SMS_FAILURE_MESSAGE, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_SMS: {
                if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    _txtStatus.setText(getString(R.string.txt_permission_status_granted));
                }
            }
        }
    }
}