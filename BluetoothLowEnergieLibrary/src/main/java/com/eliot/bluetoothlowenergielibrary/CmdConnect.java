package com.eliot.bluetoothlowenergielibrary;

import static android.service.controls.ControlsProviderService.TAG;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.eliot.bluetoothlowenergielibrary.Interface.BluetoothDeviceListListener;
import com.eliot.bluetoothlowenergielibrary.Interface.SerialListener;
import com.eliot.bluetoothlowenergielibrary.Receiver.BluetoothStateChangedReceiver;
import com.eliot.bluetoothlowenergielibrary.Receiver.CheckConnectionReceiver;
import com.eliot.bluetoothlowenergielibrary.Serial.SerialService;
import com.eliot.bluetoothlowenergielibrary.Serial.SerialSocket;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

public class CmdConnect implements ServiceConnection, SerialListener {
    private static CmdConnect instance = null;
    private SerialSocket serialSocket = null;
    private BluetoothDevice bluetoothDevice;
    private String deviceName = "";
    private SerialService serialService;
    private ArrayList<byte[]> dataList;
    private Handler handler = new Handler();
    private Vector<String> filter = new Vector<>();
    private ConnectedState connectedState = ConnectedState.NOT_CONNECTED;
    private String dataStr = "";
    private Context context, applicationContext;
    private BluetoothStateChangedReceiver bluetoothStateChangedReceiver = new BluetoothStateChangedReceiver();
    private CheckConnectionReceiver checkConnectionReceiver = new CheckConnectionReceiver();
    private byte[] testBytes;
    private boolean bluetoothEnable;
    private int cntAttempt = 0;
    private int testReboot = 0;
    private int maxCharacterNumber = 1000;
    private long minThreadSleep, maxThreadSleep;
    private ArrayList<BluetoothDevice> bluetoothDeviceArrayList;
    private CmdAnalyse cmdAnalyse;

    private ArrayList<byte[]> byteDataReceivedList;
    private ArrayList<String> stringDataReceivedList;

    private Thread threadRebootAttempt;
    private Boolean threadRebootAttemptOn = false;

    private BluetoothDeviceListListener callBack;

    public enum ConnectedState {
        CONNECTED, NOT_CONNECTED
    }

    /*<------------------------------------Getter and Setter--------------------------------------->*/

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public ArrayList<BluetoothDevice> getBluetoothDeviceArrayList() {
        return bluetoothDeviceArrayList;
    }

    public ArrayList<String> getStringDataReceivedList() {
        return stringDataReceivedList;
    }

    public void setBluetoothEnable(boolean bluetoothEnable) {
        this.bluetoothEnable = bluetoothEnable;
    }

    public boolean getBluetoothEnable() {
        return bluetoothEnable;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setConnectedState(ConnectedState connectedState) {
        this.connectedState = connectedState;
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

    public static CmdConnect getInstance() {
        if (instance == null) {
            instance = new CmdConnect();
        }
        return instance;
    }

    public void setApplicationContext(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public CmdAnalyse getCmdAnalyse() {
        return cmdAnalyse;
    }

    public SerialSocket getSocket() {
        return serialSocket;
    }

    public void setSerialSocket(SerialSocket socket) {
        this.serialSocket = socket;
    }

    public CmdConnect() {
        this.dataList = new ArrayList<>();
        this.bluetoothDeviceArrayList = new ArrayList<>();
        this.byteDataReceivedList = new ArrayList<>();
        this.stringDataReceivedList = new ArrayList<>();
        this.cmdAnalyse = new CmdAnalyse();
        this.callBack = null;
        this.minThreadSleep = 1000;
        this.maxThreadSleep = 5000;
    }

    public void startService(Intent intent, Context applicationContext) {
        System.out.println("//Context + " + context);
        this.context = applicationContext;
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

    public void connect() {
        try {
            if (serialSocket.getName() != null && bluetoothDevice.getName() != null) {
                if (!Objects.equals(serialSocket.getName(), bluetoothDevice.getName())) {
                    try {
                        serialSocket = new SerialSocket(context, bluetoothDevice);
                        serialService.connect(serialSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        throw new Exception();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("//Already Connected " + e);
                    }
                }
            } } catch (Exception e){
                try {
                    serialSocket = new SerialSocket(context, bluetoothDevice);
                    serialService.connect(serialSocket);
                } catch (IOException ioE) {
                    e.printStackTrace();
                }
            }
        }

    //Permet de démarrer le bluetooth au démarrage de l'application
    public void enableBluetooth(Context applicationContext) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "enableBluetooth: bluetooth is not supported in this device");
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            applicationContext.startActivity(enableBtIntent);
        }
    }

    //enregistre le receiver
    public void registerBluetoothStateChangedReceiver(Context context) {
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(bluetoothStateChangedReceiver, filter);
    }

    public void stopBluetoothStateChangedReceiver(Context context) {
        context.unregisterReceiver(bluetoothStateChangedReceiver);
    }

    public void rebootAttempt() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                threadRebootAttemptOn = true;
                cntAttempt = 0;
                while (!threadRebootAttempt.isInterrupted() && cntAttempt <= 3 && connectedState != ConnectedState.CONNECTED) {
                    try {
                        Thread.sleep((long) (minThreadSleep + (Math.random() * (maxThreadSleep - minThreadSleep))));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("//Thread.interrupt() ");
                    }
                    cntAttempt ++;
                    System.out.println("//cntAttempt= " + cntAttempt + "//connectedState= " + connectedState);
                        if (serialSocket != null) {
                            System.out.println("//testSocket " + serialSocket);
                            connect();
                        } else {
                            try {
                                serialService.connect(new SerialSocket(applicationContext, bluetoothDevice));
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println("//ErreurSocket");
                            }
                        }
                }
                if (cntAttempt >= 3 && connectedState == ConnectedState.NOT_CONNECTED) {
                    System.out.println("//cntAttemptError " + cntAttempt);
                    threadRebootAttempt.currentThread().interrupt();
                    cntAttempt = 0;
                }
                if (connectedState == ConnectedState.CONNECTED) {
                    threadRebootAttempt.interrupt();
                }
            };
        };

        threadRebootAttempt = new Thread(runnable);
        threadRebootAttempt.start();
    }

    public void intentCheckConnectionState(Context context) {
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
        if (threadRebootAttemptOn) {
            threadRebootAttempt.interrupt();
            cntAttempt = 0;
            threadRebootAttemptOn = false;
        }
        System.out.println(("//ConnectedState " + connectedState));
    }

    @Override
    public void onSerialConnectError(Exception e) {
        serialService.disconnect();
        connectedState = ConnectedState.NOT_CONNECTED;
        Toast.makeText(context, "device is disconnected", Toast.LENGTH_SHORT).show();
        System.out.println("//ErrorConnect " + connectedState);
        /*while (connectedState != ConnectedState.CONNECTED) {
            System.out.println("//TestBoucleWhile");
            rebootAttempt();
        }*/
    }

    //Reception des données envoyés par l'uc sous forme de tableau de byte
    @Override
    public void onSerialRead(byte[] data) {

        dataList.add(data);
        dataStr = new String(data, StandardCharsets.UTF_8);
        System.out.println("//Data " + dataStr);
        cmdAnalyse.start(dataStr);
        /*for(int i = 0; i < dataList.size(); i++){
            dataList.get(i);
        }*/
    }

    public void sendCommand(byte[] data) {
        try {
            serialService.write(data);
            System.out.println("//SerialServiceSendCommand " + new String(data, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSerialIoError(Exception e) {
        serialService.disconnect();
        connectedState = ConnectedState.NOT_CONNECTED;
        System.out.println("//ErrorConnect " + connectedState);
        rebootAttempt();
    }

    public void setBluetoothListListener(BluetoothDeviceListListener callBack) {

    }
}
