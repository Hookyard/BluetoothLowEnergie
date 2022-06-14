package com.eliot.bluetoothlowenergielibrary;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.eliot.bluetoothlowenergielibrary.App.App;
import com.eliot.bluetoothlowenergielibrary.Interface.SerialListener;
import com.eliot.bluetoothlowenergielibrary.Serial.SerialService;
import com.eliot.bluetoothlowenergielibrary.Serial.SerialSocket;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Vector;

public class Cmd_connect implements ServiceConnection, SerialListener {
    public final static Cmd_connect instance = new Cmd_connect();
    private SerialSocket serialSocket = null;
    private BluetoothDevice bluetoothDevice;
    private String deviceName = "";
    private SerialService serialService;
    private ArrayList<byte[]> dataList;
    private Thread automaticReconnectionThread;
    private Handler handler = new Handler();
    private Vector<String> filter = new Vector<>();
    private ConnectedState connectedState = ConnectedState.NOT_CONNECTED;
    private String test = "";
    private Context context, contextActivity;

    private enum ConnectedState {
        CONNECTED, NOT_CONNECTED
    }

    public Cmd_connect() {
        dataList = new ArrayList<>();
    }

    public SerialSocket getSocket() {
        return serialSocket;
    }

    public void setSocket(Context context, BluetoothDevice bluetoothDevice) {
        serialSocket = new SerialSocket(context, bluetoothDevice);
    }

    public void startService(Intent intent) {
        System.out.println("//Context + " + context);
        try {
            if (serialService == null) {
                context.bindService(intent, this, Context.BIND_AUTO_CREATE);
            } else {
                System.out.println("//serialService serialService is not null and already exist");
            }
        } catch (Exception e) {
            System.out.println("//ServiceErreur startService");
        }
    }

    public void connect() throws IOException {
        serialService.connect(serialSocket);
        serialService.write("test".getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        System.out.println("//TestOnServiceConnected");
        serialService = ((SerialService.SerialBinder) iBinder).getService();
        serialService.attach(this);
        System.out.println("//TestOnServiceConnected True " + serialService);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        serialService.disconnect();
        connectedState = ConnectedState.NOT_CONNECTED;
    }

    @Override
    public void onSerialConnect() {
        connectedState = ConnectedState.CONNECTED;

        System.out.println(("//ConnectedState " + connectedState));
    }

    @Override
    public void onSerialConnectError(Exception e) {
        connectedState = ConnectedState.NOT_CONNECTED;
        serialService.disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        dataList.add(data);
        System.out.println("//Data " + data.toString());
        test = new String(data, StandardCharsets.UTF_8);
        System.out.println("//Data " + test);
        for(int i = 0; i < dataList.size(); i++){
            dataList.get(i);
        }
    }

    @Override
    public void onSerialIoError(Exception e) {
        serialService.disconnect();
    }

    /*<------------------------------------Getter and Setter--------------------------------------->*/
    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public ConnectedState getConnectedState() {
        return connectedState;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public SerialService getSerialService() {
        return serialService;
    }

    public Cmd_connect getInstance() {
        return instance;
    }

    public void setContextActivity(Context contextActivity) {
        this.contextActivity = contextActivity;
    }
}
