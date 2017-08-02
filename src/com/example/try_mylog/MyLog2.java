package com.example.try_mylog;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.os.Environment;
import android.provider.Settings.System;
import android.util.Log;

public class MyLog2 {


	private static MyLog2 instance = new MyLog2();
	public static LogDispatcher logDispatcher;
	
	private MyLog2() {
		logDispatcher = new LogDispatcher();
		logDispatcher.start();
	}

	public static MyLog2 getInstance() {
		if (instance == null) {
			instance = new MyLog2();
		}
		return instance;
	}

	private String getFunctionName() {
		StackTraceElement[] sts = Thread.currentThread().getStackTrace();

		if (sts == null) {
			return null;
		}

		for (StackTraceElement st : sts) {
			if (st.isNativeMethod()) {
				continue;
			}

			if (st.getClassName().equals(Thread.class.getName())) {
				continue;
			}

			if (st.getClassName().equals(this.getClass().getName())) {
				continue;
			}

			return "[" + Thread.currentThread().getName() + "("
					+ Thread.currentThread().getId() + "): " + st.getFileName()
					+ ":" + st.getLineNumber() + "]";
		}

		return null;
	}

	private String createMessage(String msg) {
		String functionName = getFunctionName();
		String message = (functionName == null ? msg
				: (functionName + " - " + msg));
		return message;
	}

	public void info(String msg) {
		String message = createMessage(msg);
		if (DebugConfig.debug) {
			Log.i(DebugConfig.TAG, message);
		}
		if (DebugConfig.write2Sdcard) {
			instance.writeLog(message);
		}
	}

	public static void i(String msg) {
		instance.info(msg);
	}

	public static void i(Exception e) {
		instance.info(e != null ? e.toString() : "null");
	}

	public void verbose(String msg) {
		String message = createMessage(msg);
		if (DebugConfig.debug) {
			Log.v(DebugConfig.TAG, message);
		}
		if (DebugConfig.write2Sdcard) {
			instance.writeLog(message);
		}
	}

	public void v(String msg) {
		if (DebugConfig.debug) {
			instance.verbose(msg);
		}
		if (DebugConfig.write2Sdcard) {
			instance.writeLog(msg);
		}
	}

	public void v(Exception e) {
		if (DebugConfig.debug) {
			instance.verbose(e != null ? e.toString() : "null");
		}
		if (DebugConfig.write2Sdcard) {
			instance.writeLog(e.toString());
		}
	}

	public void debug(String msg) {
		if (DebugConfig.debug) {
			String message = createMessage(msg);
			Log.d(DebugConfig.TAG, message);
		}
		if (DebugConfig.write2Sdcard) {
			instance.writeLog(msg);
		}
	}

	public void error(String msg) {
		String message = createMessage(msg);
		if (DebugConfig.debug) {
			Log.e(DebugConfig.TAG, message);
		}
		if (DebugConfig.write2Sdcard) {
			instance.writeLog(message);
		}
	}

	public void error(Exception e) {
		StringBuffer sb = new StringBuffer();
		String name = getFunctionName();
		StackTraceElement[] sts = e.getStackTrace();

		if (name != null) {
			sb.append(name + " - " + e + "\r\n");
		} else {
			sb.append(e + "\r\n");
		}
		if (sts != null && sts.length > 0) {
			for (StackTraceElement st : sts) {
				if (st != null) {
					sb.append("[ " + st.getFileName() + ":"
							+ st.getLineNumber() + " ]\r\n");
				}
			}
		}
		if (DebugConfig.debug) {
			Log.e(DebugConfig.TAG, sb.toString());
		}
		if (DebugConfig.write2Sdcard) {
			instance.writeLog(sb.toString());
		}
	}

	public void warn(String msg) {
		String message = createMessage(msg);
		if (DebugConfig.debug) {
			Log.w(DebugConfig.TAG, message);
		}
		if (DebugConfig.write2Sdcard) {
			instance.writeLog(message);
		}
	}

	public static void d(String msg) {
		instance.debug(msg);

	}

	public static void d(Exception e) {
		instance.debug(e != null ? e.toString() : "null");
	}

	public static void e(String msg) {
		instance.error(msg);
	}

	public static void e(Exception e) {
		instance.error(e);
	}

	public static void w(String msg) {
		instance.warn(msg);
	}

	public static void w(Exception e) {
		instance.warn(e != null ? e.toString() : "null");
	}

	public static void resetLogFile() {
		File file = new File(DebugConfig.logFile);
		file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeLog(String content) {
//		FileUtil.writeLogToFile(content);
		logDispatcher.add(content);
	}
	
	public static void quit(){
		logDispatcher.quit();
	}
	
	class LogDispatcher extends Thread {

	    private static final String TAG = "LogDispatcher";

	    private final BlockingQueue<String> mMarkerQueue;
	    private volatile boolean mQuit = false;
//	    private final LogWriter mLogWriter;

	    private int count = 0;
	    
	    private StringBuilder logInfos = new StringBuilder();
	    
	    private long lastUpdateTime;
	    
	    LogDispatcher() {
	        mMarkerQueue = new ArrayBlockingQueue<String>(50);
	    }

	    void add(String logInfo) {
	        try {
	            mMarkerQueue.put(logInfo);
	        } catch (InterruptedException e) {
	            Log.e(TAG, e.getMessage(), e);
	        }
	    }

	    public void quit() {
	        mQuit = true;
	        interrupt();
	    }

	    @Override
	    public void run() {
	        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
	        String string;
	        while (true) {
	            try {
	            	string = mMarkerQueue.take();
	            } catch (InterruptedException e) {
	                if (mQuit) {
	                    return;
	                }
	                continue;
	            }

//	            if (mLogWriter != null) {
//	                mLogWriter.writeLog(marker);
//	            }
	            
	            if(string != null){
	            	FileUtil.writeLogToFile(string);
	            	count++;
	            }
	            if(DebugConfig.debug){
	            	if(logInfos.length()!=0)
	            		logInfos.append("\n");
	            	logInfos.append(string);
	            }
	            if(count >= DebugConfig.logUploadSize || java.lang.System.currentTimeMillis() > lastUpdateTime){
	            	lastUpdateTime = java.lang.System.currentTimeMillis() + DebugConfig.update;
	            	String urlPath = MyLog.makeUrlStringByCrashLogInfo(MyLog.mac, null, null, null, null, null, null, logInfos.toString());
	            	if(MyLog.sendLog(MyLog.makeHttpClient(), MyLog.makeHttpPost(urlPath)))
	            		logInfos.setLength(0);
	            	count = 0;
	            }
	        }
	    }

	}
}



