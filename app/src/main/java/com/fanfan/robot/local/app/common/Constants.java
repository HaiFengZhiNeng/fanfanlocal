package com.fanfan.robot.local.app.common;

import com.fanfan.robot.local.app.NovelApp;
import com.fanfan.robot.local.utils.system.FileUtil;

import java.io.File;

/**
 * Created by android on 2017/12/18.
 */

public class Constants {


    public static int displayWidth;
    public static int displayHeight;

    private static final String M_SDROOT_CACHE_PATH = FileUtil.getCacheDir(NovelApp.getInstance().getApplicationContext()) + File.separator;

    private static final String M_SDROOT_FILE_PATH = FileUtil.getExternalFileDir(NovelApp.getInstance().getApplicationContext()) + File.separator;

    public static final String PROJECT_PATH = M_SDROOT_FILE_PATH + "local" + File.separator;
    public static final String PRINT_LOG_PATH = M_SDROOT_CACHE_PATH + "print";
    public static final String PRINT_TIMLOG_PATH = M_SDROOT_CACHE_PATH + "log" + File.separator;
    public static final String CRASH_PATH = M_SDROOT_CACHE_PATH + "crash";
    public static final String RECORDER_PATH = PROJECT_PATH + "Camera";
    public static final String DOWNLOAD_PATH = PROJECT_PATH + "download";

    public static final String GRM_PATH = PROJECT_PATH + "msc";

    public static final String PICTURETAKEN = "pictureTaken";

    public static String RES_DIR_NAME = "robotResources";

    public static final String IAT_LOCAL_BUILD = "iat_local_build";

//    public static final String IAT_LOCAL_UPDATELEXICON = "iat_local_updatelexicon";

    public static final String PASSWORD = "password";

    public static final String QUERYLANAGE = "query_lanage";
    public static final String IAT_LINE_LANGUAGE = "iat_line_language";

    public static final String IAT_LOCAL_LANGUAGE = "iat_local_language";
    public static final String IAT_LINE_LANGUAGE_TALKER = "iat_line_language_talker";
    public static final String IAT_LOCAL_LANGUAGE_TALKER = "iat_local_language_talker";
    public static final String IAT_LINE_LANGUAGE_HEAR = "iat_line_language_hear";

    public static final String IAT_LOCAL_LANGUAGE_HEAR = "iat_local_language_hear";

    public static final String IS_INITIALIZATION = "is_initialization";

    public static final String LINE_SPEED = "line_speed";
    public static final String LINE_VOLUME = "line_volume";

    public static final String LANGUAGE_TYPE = "language_type";

    public static long lockingTime = 60 * 1000 * 100;

}
