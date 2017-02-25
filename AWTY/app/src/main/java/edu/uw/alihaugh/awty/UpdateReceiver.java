package edu.uw.alihaugh.awty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by alihaugh on 2/18/17.
 */

public class UpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("debugging", "Got into receiver");
        SharedPreferences sp = context.getSharedPreferences("appPref", Context.MODE_PRIVATE);
        String message = sp.getString("message", "Not found");
        String phone = sp.getString("phone", "Not found");
        String notification = phone + ": " + message;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, message, null, null);
        Toast t = Toast.makeText(context, notification, Toast.LENGTH_SHORT);
        t.show();
    }
}
