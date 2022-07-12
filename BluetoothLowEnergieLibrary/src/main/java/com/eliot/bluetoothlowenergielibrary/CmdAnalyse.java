package com.eliot.bluetoothlowenergielibrary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CmdAnalyse {
    private String dataStr = "";
    private final Pattern patternData;
    private final int maxSize;
    private int dataLength;

    public CmdAnalyse() {
        patternData = Pattern.compile("(\\n>$)");
        maxSize = 1000;
    }

    public void start(String dataReceived) {
        dataStr += dataReceived;

        Matcher matcher = patternData.matcher(dataStr);
        if (matcher.find()) {
            dataStr.replaceAll("(?m)\r\n^[ \t]*\r?\n", "");
            CmdConnect.getInstance().getStringDataReceivedList().add(dataStr);
            dataStr = "";
            for (int i = 0; i < CmdConnect.getInstance().getStringDataReceivedList().size(); i++) {
                dataLength += CmdConnect.getInstance().getStringDataReceivedList().get(i).length();
                if (dataLength > maxSize) {
                    dataLength -= CmdConnect.getInstance().getStringDataReceivedList().get(0).length();
                    if (dataLength > 0) {
                        CmdConnect.getInstance().getStringDataReceivedList().remove(0);
                    }
                }
            }
        }
    }
}
