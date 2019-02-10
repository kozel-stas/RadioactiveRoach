package com.roach.config;

public class ConfigConstants {

    public static String SERVER_SOCKET_ADDRESS = "127.0.0.1";
    public static String SERVER_NAME = "Radioactive Roach v1.0";
    public static int SERVER_SOCKET_PORT = 8080;
    public static int SERVER_BUFFER_SIZE = 1024;
    public static long KEEP_ALIVE_TIME_OUT = 30_000;
    public static long INITIAL_TIME_OUT = 5_000;
    public static long REQUEST_TIMED_OUT = 30_000;
    public static int MAX_UNPROCESSED_MESSAGES_SIZE = 10_000;
    public static int THRESHOLD_FOR_UNPROCESSED_MESSAGES = 5;
    public static int MAX_NUMBER_OF_HTTP_PROCESSORS = Runtime.getRuntime().availableProcessors();
    public static long DISCONNECT_DELAY_IN_SECONDS = 1;

}
