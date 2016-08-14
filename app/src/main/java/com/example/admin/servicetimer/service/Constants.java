package com.example.admin.servicetimer.service;

public class Constants {


    public interface ACTION {
        public static String MAIN_ACTION = "com.fahadhd.foregroundservice.action.main";
        public static final String STARTFOREGROUND_ACTION = "com.fahadhd.foregroundservice.action.startforeground";
        public static final String STOPFOREGROUND_ACTION = "com.fahadhd.foregroundservice.action.stopforeground";
        public static final String BROADCAST_ACTION = "com.fahadhd.foregroundservice.action.broadcast";
    }
    public interface TIMER {
        public static final String CURRENT_TIME = "com.fahadhd.foregroundservice.timer.current_time";
        public static final String DURATION = "com.fahadhd.foregroundservice.timer.duration";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 1;
    }
}