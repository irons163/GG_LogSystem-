package com.example.try_mylog;

import android.os.Environment;

public class DebugConfig {
	public static String TAG = "Logger";
	public static final String ROOT = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/";
	public static String FILE_NAME = "logger.txt";

	public static String logFile = ROOT + FILE_NAME;
	public static boolean debug = BuildConfig.DEBUG;

	public static boolean write2Sdcard = true;
	
	public static int logUploadSize = 10; 
	
	public static int update = 5000; 
	
	public static boolean showLineNumer = false;
}
