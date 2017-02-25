package edu.uw.alihaugh.awty;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    public EditText message, phone, nag_rate;
    public Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        b = (Button)findViewById(R.id.control_button);
        message = (EditText) findViewById(R.id.message);
        phone = (EditText) findViewById(R.id.phone_number);
        nag_rate = (EditText) findViewById(R.id.minutes);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!message.getText().toString().trim().equals("") &&
                        !phone.getText().toString().trim().equals("") &&
                        !nag_rate.getText().toString().trim().equals("") &&
                        b.getText().equals("Start")) {
                    startSending(b, message, phone, nag_rate);
                } else if (b.getText().equals("Stop")) {
                    stopSending(b, message, phone, nag_rate);
                }
            }
        });
    }

    public void startSending(Button b, EditText message, EditText phone, EditText nag_rate) {

        Log.i("debugging", "startSending triggered");

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    public void stopSending(Button b, EditText message, EditText phone, EditText nag_rate) {
        Log.i("debugging", "stopSending started");
        // enable all fields
        message.setEnabled(true);
        phone.setEnabled(true);
        nag_rate.setEnabled(true);

        // change text in buttons
        b.setText("Start");

        // stop alarm
        Intent intent = new Intent(EditActivity.this, UpdateReceiver.class);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(EditActivity.this, 0, intent, 0);
        stopService(intent);
        alarm.cancel(pi);
        Log.i("debugging", "stopSending finished starting");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // disable changing all fields
                    message.setEnabled(false);
                    phone.setEnabled(false);
                    nag_rate.setEnabled(false);

                    // change text in button
                    b.setText("Stop");

                    // Start alarm
                    Intent intent = new Intent(EditActivity.this, UpdateReceiver.class);
                    SharedPreferences sp = getSharedPreferences("appPref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor pe = sp.edit();
                    pe.putString("phone", phone.getText().toString());
                    pe.putString("message", message.getText().toString());
                    pe.commit();
                    AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    PendingIntent pi = PendingIntent.getBroadcast(EditActivity.this, 0, intent, 0);
                    alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), Integer.parseInt(nag_rate.getText().toString()), pi);
                    Log.i("debugging", "startSending finished starting");
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }
}
