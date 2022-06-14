package com.eliot.bluetoothlowenergielibrary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cmd_analyse {
    private String dataStr = "";
    private Pattern patternData;

    public Cmd_analyse() {
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
