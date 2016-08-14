package com.example.admin.servicetimer.service;

public class Constants {


    public interface ACTION {
        public static final String STARTFOREGROUND_ACTION = "com.fahad.foregroundservice.action.startforeground";
        public static final String STOPFOREGROUND_ACTION = "com.fahad.foregroundservice.action.stopforeground";
        public static final String BROADCAST_ACTION = "com.fahad.foregroundservice.action.broadcast";
    }
    public interface TIMER {
        public static final String CURRENT_TIME = "com.fahad.foregroundservice.timer.current_time";
        public static final String DURATION = "com.fahad.foregroundservice.timer.duration";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 1;
    }
}