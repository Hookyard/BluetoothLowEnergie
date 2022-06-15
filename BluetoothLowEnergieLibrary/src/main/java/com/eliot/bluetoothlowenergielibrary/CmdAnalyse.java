package com.eliot.bluetoothlowenergielibrary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CmdAnalyse {
    private String dataStr = "";
    private Pattern patternData;

    public CmdAnalyse() {
        patternData = Pattern.compile("(\\n>$)");
    }

    public void start(String str) {
        dataStr += str;

        Matcher matcher = patternData.matcher(dataStr);
        if (matcher.find()) {
            dataStr.replaceAll("(?m)^[ \t]*\r?\n", "");
            dataStr = "";
        }
    }

    public String getData() {
        return dataStr;
    }
}
