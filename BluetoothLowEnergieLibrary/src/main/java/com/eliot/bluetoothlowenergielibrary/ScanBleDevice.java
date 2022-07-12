package com.eliot.bluetoothlowenergielibrary;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Handler;

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

    private static final long SCAN_PERIOD = 10000;

    public ScanBleDevice() {
        patternFilter = Pattern.compile("uc201");
        bluetoothDeviceList = new HashSet<BluetoothDevice>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    public Boolean getScanning() {
        return this.scanning;
    }

    public void setScanning(Boolean scanning) {
        this.scanning = scanning;
    }

    public void scanLeDevice() {
        if (!scanning) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);
            scanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }

    public void stopLeScan() {
        if (scanning == true) {
            scanning = false;
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
        }
    };

    public HashSet<BluetoothDevice> getBluetoothDeviceList() {
        return bluetoothDeviceList;
    }
    };
