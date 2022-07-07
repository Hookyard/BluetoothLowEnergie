package com.eliot.bluetoothlowenergielibrary.Receiver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Switch;

import com.eliot.bluetoothlowenergielibrary.CmdConnect;

public class BluetoothStateChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /*Boolean isBluetoothEnbaled = intent.getBooleanExtra(BluetoothAdapter.ACTION_STATE_CHANGED, false);
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
            }*/

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.getState();

        switch (bluetoothAdapter.getState()) {
            case BluetoothAdapter.STATE_OFF:
                CmdConnect.getInstance().setBluetoothEnable(false);
                CmdConnect.getInstance().setConnectedState(CmdConnect.ConnectedState.NOT_CONNECTED);
                System.out.println("//Broasdcast StateOff");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(enableBtIntent);
                if (CmdConnect.getInstance().getConnectedState() == CmdConnect.ConnectedState.CONNECTED)
                    CmdConnect.getInstance().setConnectedState(CmdConnect.ConnectedState.NOT_CONNECTED);
                break;
                case BluetoothAdapter.STATE_ON:
                    System.out.println("//Broasdcast StateOn");
                    CmdConnect.getInstance().setBluetoothEnable(true);
                    break;
            default:
                System.out.println("//Broadcast StateValue " + bluetoothAdapter.getState());
        }
        }
    }



