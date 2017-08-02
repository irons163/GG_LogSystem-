package com.senao.debug_log;

import android.os.Environment;

public class DebugConfig {
	static String TAG = "Logger";
	static final String ROOT = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/";
	static String FILE_NAME = "logger.txt";

	static String logFile = ROOT + FILE_NAME;
	static int logUploadSize = 10; 
	static int updateIntervalTime = 5000; 
	
	static boolean debugModeEnable = false;
	static boolean showDebugLogToLogCat = true;
	static boolean writeLogToSdcard = false;
	static boolean showLineNumer = false;	
	static boolean showAlertMessageBoxWhenSendLogByNetFail = false;	
	static boolean showCrashMessageBoxWhenAppCrash = true;	
	static boolean sendLogByNet = false;
	
	static String googleFormID = "";
}
