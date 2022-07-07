package com.eliot.bluetoothlowenergielibrary;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CmdWrite {
    private String dataStr;
    private String add;
    private byte[] data;

    public CmdWrite(String dataStr) {
        this.dataStr = dataStr;
        this.add = "\r\n";
    }

    public CmdWrite(byte[] data) {
        this.data = data;
    }

    public void socketWrite() {
        if (data != null) {
            String strTemp = data.toString();
            strTemp += add;
            byte[] dataFinal = strTemp.getBytes(StandardCharsets.UTF_8);
            try {
                CmdConnect.getInstance().getSerialService().write(dataFinal);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (dataStr != null) {
            dataStr += add;
            byte[] dataFinal = dataStr.getBytes(StandardCharsets.UTF_8);
            try {
                CmdConnect.getInstance().getSerialService().write(dataFinal);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
