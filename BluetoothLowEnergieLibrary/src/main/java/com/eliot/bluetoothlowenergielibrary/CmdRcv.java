package com.eliot.bluetoothlowenergielibrary;

import java.util.ArrayList;

public class CmdRcv {
    private ArrayList<String> dataList;
    private int maxSize = 1000;
    private String strTest = "";

    public CmdRcv(String data) {
        dataList.add(data);
    }

    public void parcourList() {
        for (int i = 0; i < dataList.size(); i++) {
            strTest += dataList.get(i);
            while (strTest.length() > maxSize) {
                String newStr = dataList.get(0);
                strTest.substring(newStr.length());
                dataList.remove(0);
            }
            strTest = "";
        }
    }

    public ArrayList<String> getDataList() {
        return dataList;
    }
}
