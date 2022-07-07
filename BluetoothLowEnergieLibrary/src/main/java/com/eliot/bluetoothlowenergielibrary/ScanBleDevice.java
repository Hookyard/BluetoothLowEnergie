package com.eliot.bluetoothlowenergielibrary;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.eliot.bluetoothlowenergielibrary.Interface.BluetoothDeviceListListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScanBleDevice {
    private HashSet<BluetoothDevice> bluetoothDeviceList;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler = new Handler();
    private Pattern patternFilter;
    private Matcher matcherFilter;
    private Boolean booleanFilter;
    private boolean scanning;

    private BluetoothDeviceListListener callBack;

    private static final long SCAN_PERIOD = 10000;

    public ScanBleDevice(Context context) {


        patternFilter = Pattern.compile("uc201");

        bluetoothDeviceList = new HashSet<BluetoothDevice>();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        callBack = null;
    }

    public void scanLeDevice() {
        if (!scanning) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    /*bluetoothAdapter.stopLeScan(leScanCallback);*/
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            /*bluetoothAdapter.startLeScan(leScanCallback);*/
            bluetoothLeScanner.startScan(leScanCallback);
        } else {
            scanning = false;
            /*bluetoothAdapter.stopLeScan(leScanCallback);*/
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }

    public void stopLeScan() {
        if (scanning == true) {
            scanning = false;
            /*bluetoothAdapter.stopLeScan(leScanCallback);*/
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (result.getDevice().getName() != null) {
                matcherFilter = patternFilter.matcher(result.getDevice().getName());
                booleanFilter = matcherFilter.find();
                if (booleanFilter) {
                    bluetoothDeviceList.add(result.getDevice());
                    System.out.println("//Reussi + " + result.getDevice().getName());
                }
            }
            /*bluetoothDeviceList.add(result.getDevice());*/
        }



    /*private BluetoothAdapter.LeScanCallback leScanCallback = (bluetoothDevice, i, bytes) -> {
        if (bluetoothDevice.getName() != null) {
            matcherFilter = patternFilter.matcher(bluetoothDevice.getName());
            booleanFilter = matcherFilter.find();
            if (booleanFilter) {
                bluetoothDeviceList.add(bluetoothDevice);
                System.out.println("//Reussi + " + bluetoothDevice.getName());
            }
        }*/
    };

    /*private void setBluetoothDeviceListlistener(BluetoothDeviceListListener callBack) {
        this.callBack = callBack;
        if (callBack != null) {

        }
    }*/
    public HashSet<BluetoothDevice> getBluetoothDeviceList() {
        return bluetoothDeviceList;
    }
    };
