package com.eliot.bluetoothlowenergielibrary.Serial;

class Constants {

    // values have to be globally unique
    public static final String APPLICATION_ID = "com.eliot.bluetoothlowenergielibrary";

    static final String INTENT_ACTION_DISCONNECT = APPLICATION_ID + ".Disconnect";
    static final String NOTIFICATION_CHANNEL = APPLICATION_ID + ".Channel";
    static final String INTENT_CLASS_MAIN_ACTIVITY = APPLICATION_ID + ".MainActivity";

    // values have to be unique within each app
    static final int NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001;

    private Constants() {}
}
