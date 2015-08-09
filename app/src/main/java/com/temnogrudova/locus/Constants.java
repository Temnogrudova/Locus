package com.temnogrudova.locus;

/**
 * Created by 123 on 16.04.2015.
 */
public class Constants {
        public interface ACTION {
            public static String START_ACTION = "com.demo.gemsense.gestureservicetest.action.startforeground";
            public static String STOP_ACTION = "com.demo.gemsense.gestureservicetest.action.stopforeground";
            public static String SET_CURRENT_LOCATION ="com.demo.gemsense.gestureservicetest.action.setcurrentlocation";
            public static String GET_CURRENT_LOCATION ="com.demo.gemsense.gestureservicetest.action.getcurrentlocation";
            public static String SET_NOTOFICATION_SOUND ="com.demo.gemsense.gestureservicetest.action.setnoticationsound";

        }

        public interface NOTIFICATION_ID {
            public static int FOREGROUND_SERVICE = 101;
        }
}
