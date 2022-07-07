package com.eliot.bluetoothlowenergielibrary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CmdAnalyse {
    private String dataStr = "";
    private Pattern patternData;

    public CmdAnalyse() {
        patternData = Pattern.compile("(\\n>$)");
    }

    public void start(String dataReceived) {
        dataStr += dataReceived;

        Matcher matcher = patternData.matcher(dataStr);
        if (matcher.find()) {
            dataStr.replaceAll("(?m)^[ \t]*\r?\n", "");
            CmdConnect.getInstance().getStringDataReceivedList().add(dataStr);
            dataStr = "";
            for (int i = 0; i < CmdConnect.getInstance().getStringDataReceivedList().size(); i++) {
                /*int numberOfCharaclist =  */
            }
        }
    }

    public String getData() {
        return dataStr;
    }
}
