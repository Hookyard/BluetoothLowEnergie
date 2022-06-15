package com.eliot.bluetoothlowenergielibrary;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import com.eliot.bluetoothlowenergielibrary.Interface.SerialListener;
import com.eliot.bluetoothlowenergielibrary.Receiver.BluetoothStateChangedReceiver;
import com.eliot.bluetoothlowenergielibrary.Receiver.CheckConnectionReceiver;
import com.eliot.bluetoothlowenergielibrary.Serial.SerialService;
import com.eliot.bluetoothlowenergielibrary.Serial.SerialSocket;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Vector;

public class CmdConnect implements ServiceConnection, SerialListener {
    public final static CmdConnect instance = new CmdConnect();
    private SerialSocket serialSocket = null;
    private BluetoothDevice bluetoothDevice;
    private String deviceName = "";
    private SerialService serialService;
    private ArrayList<byte[]> dataList;
    private Handler handler = new Handler();
    private Vector<String> filter = new Vector<>();
    private ConnectedState connectedState = ConnectedState.NOT_CONNECTED;
    private String test = "";
    private Context context, contextActivity;
    private BluetoothStateChangedReceiver bluetoothStateChangedReceiver = new BluetoothStateChangedReceiver();
    private CheckConnectionReceiver checkConnectionReceiver = new CheckConnectionReceiver();

    private enum ConnectedState {
        CONNECTED, NOT_CONNECTED
    }

    public CmdConnect() {
        dataList = new ArrayList<>();
        enableBlueooth();
    }

    public SerialSocket getSocket() {
        return serialSocket;
    }

    public void setSerialSocket(SerialSocket socket) {
        this.serialSocket = socket;
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

    //Permet de démarrer le bluetooth au démarrage de l'application
    public void enableBlueooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(enableBtIntent);
        }
    }

    //Envoie d'un message pour demander a l'utilisateur d'utiliser le bluetooth si celui-ci le désactive
    public void startBluetoothIfChangesAppear(Context context) {
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(bluetoothStateChangedReceiver, filter);
    }

    public void stopBluetoothIfChangesAppear(Context context) {
        context.unregisterReceiver(bluetoothStateChangedReceiver);
    }

    public void automaticRebootOfConnection() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 5000);
                try {
                    if(serialSocket != null) {
                        serialService.connect(serialSocket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void filterCheckConnection(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        context.registerReceiver(checkConnectionReceiver, filter);
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
        System.out.println("//ErrorConnect " + connectedState);
        while (connectedState != ConnectedState.CONNECTED)
        CmdConnect.instance.automaticRebootOfConnection();
    }

    //Reception des données envoyés par l'uc
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
        System.out.println("//ErrorConnect " + connectedState);
        while (connectedState != ConnectedState.CONNECTED)
            CmdConnect.instance.automaticRebootOfConnection();
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

    public CmdConnect getInstance() {
        return instance;
    }

    public void setContextActivity(Context contextActivity) {
        this.contextActivity = contextActivity;
    }
}
