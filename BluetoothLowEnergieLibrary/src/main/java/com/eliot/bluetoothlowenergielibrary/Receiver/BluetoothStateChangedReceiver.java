package com.eliot.bluetoothlowenergielibrary.Receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothStateChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Boolean isBluetoothEnbaled = intent.getBooleanExtra(BluetoothAdapter.ACTION_STATE_CHANGED, false);
        if (isBluetoothEnbaled) {
            System.out.println("//IsEnable ");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(enableBtIntent);
        } else {
            System.out.println(("//IsEnable false"));
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(enableBtIntent);
            }
        }
    }



