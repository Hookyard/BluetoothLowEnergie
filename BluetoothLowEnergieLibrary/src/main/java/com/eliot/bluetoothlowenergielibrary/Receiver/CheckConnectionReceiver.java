package com.eliot.bluetoothlowenergielibrary.Receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.eliot.bluetoothlowenergielibrary.CmdConnect;

public class CheckConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            //Device found
            Toast.makeText(context, "device found", Toast.LENGTH_SHORT).show();
        }
        else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            //Device is now connected
            Toast.makeText(context, "device connected", Toast.LENGTH_SHORT).show();
        }
        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            //Done searching
            Toast.makeText(context, "finie de chercher", Toast.LENGTH_SHORT).show();
        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
            //Device is about to disconnect
            Toast.makeText(context, "en train de deconnecter", Toast.LENGTH_SHORT).show();
        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            //Device has disconnected
            Toast.makeText(context, "appareil déconnecté", Toast.LENGTH_SHORT).show();
        }
    }
};
