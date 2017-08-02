package com.senao.debug_log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

public class DebugLog implements UncaughtExceptionHandler {
	private static final String V = "V";
	private static final String D = "D";
	private static final String I = "I";
	private static final String W = "W";
	private static final String E = "E";
	private static final String A = "A";

	private Context mContext;
	private Thread.UncaughtExceptionHandler defaultExceptionHandler;
	private static String mac;
	private static Handler handler;
	private static List<String> googleFormFieldsList = new ArrayList<String>();

	private DebugLog() {
		logDispatcher = new LogDispatcher();
		logDispatcher.start();
	}

	public static DebugLog getInstance() {
		if (instance == null) {
			instance = new DebugLog();
		}
		return instance;
	}

	@Override
	public void uncaughtException(final Thread thread, final Throwable exception) {
		// TODO Auto-generated method stub

		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {

				Builder builder = new Builder(mContext);
				builder.setTitle("Cannot send debug log!");
				builder.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								showCrashMessageBox(thread, exception);
							}
						});
				builder.show();
			}
		};

		if(DebugConfig.writeLogToSdcard || DebugConfig.sendLogByNet){
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (defaultExceptionHandler != null) {
					StringWriter errors = new StringWriter();
					exception.printStackTrace(new PrintWriter(errors));

					// showLogInfo(exception, errors);

					String[] message = errors.toString().split("\n");

					String date = getCurrentTime();

					String tag = "CrashError";
					StringBuilder text = new StringBuilder();

					for (int i = 0; i < message.length; i++) {
						text.append(message[i]);
						if (i != message.length - 1) {
							text.append("\n");
						}
					}

					DebugLog.e(tag, text.toString());
					logDispatcher.isDisableAdd = true;
					logDispatcher.isImmediateUpload = true;

					while (!logDispatcher.mQuit) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					logDispatcher.quit();
					
					showCrashMessageBox(thread, exception);	
				}
			}
		}).start();
		}else{
			showCrashMessageBox(thread, exception);
		}
	}

	public void init(Context context, boolean debugModeEnable) {
		mContext = context;

		WifiManager wm = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		mac = wm.getConnectionInfo().getMacAddress();

		if(debugModeEnable){
			DebugConfig.debugModeEnable = true;
		}else{
			DebugConfig.debugModeEnable = false;
			DebugConfig.showDebugLogToLogCat = false;
			DebugConfig.writeLogToSdcard = false;
			DebugConfig.showLineNumer = false;	
			DebugConfig.showAlertMessageBoxWhenSendLogByNetFail = false;	
			DebugConfig.showCrashMessageBoxWhenAppCrash = true;	
			DebugConfig.sendLogByNet = false;
		}
		
		defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	public void init(Context context, String googleFormID, boolean debugModeEnable) {
		init(context, debugModeEnable);
		setGoogleFormID(googleFormID);
	}
	
	public void setShowDebugLogToLogCat(boolean showDebugLogToLogCat){
		if(DebugConfig.debugModeEnable)
			DebugConfig.showDebugLogToLogCat = showDebugLogToLogCat;
	}
	
	public void setGoogleFormID(String googleFormID){
		if(DebugConfig.debugModeEnable)
			DebugConfig.googleFormID = googleFormID;
	}

	public void setShowLineNumber(boolean showLineNumer) {
		if(DebugConfig.debugModeEnable)
			DebugConfig.showLineNumer = showLineNumer;
	}

	public void setShowCrashMessageBoxWhenAppCrash(boolean showCrashMessageBoxWhenAppCrash){
		if(DebugConfig.debugModeEnable)
			DebugConfig.showCrashMessageBoxWhenAppCrash = showCrashMessageBoxWhenAppCrash;
	}
	
	public void setWriteLogToSdcard(boolean writeLogToSdcard){
		if(DebugConfig.debugModeEnable)
			DebugConfig.writeLogToSdcard = writeLogToSdcard;
	}
	
	public void setSendLogByNet(boolean sendLogByNet){
		if(DebugConfig.debugModeEnable){
			DebugConfig.sendLogByNet = sendLogByNet;
			setGoogleFormFieldsByNet();
		}
	}

//	public void getMyLog() {
//		// TODO Auto-generated constructor stub
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				System.out.println("get");
//				String str = "";
//				String pageStr = "";
//				BufferedReader br;
//				try {
//					HttpPost httpPost = new HttpPost(
//							"https://docs.google.com/forms/d/12pVe4Ep3CR0l4RoUg8ah7d2Kb7Usg4MPHeN2sWV1nw0/viewform");
//					URL u = new URL(
//							"https://docs.google.com/forms/d/12pVe4Ep3CR0l4RoUg8ah7d2Kb7Usg4MPHeN2sWV1nw0/viewform");
//					InputStream obj;
//
//					// obj = u.openStream();
//
//					HttpURLConnection conn = (HttpURLConnection) u
//							.openConnection();
//					conn.setRequestMethod("GET");
//					obj = conn.getInputStream();
//					InputStreamReader isr = new InputStreamReader(
//							(InputStream) obj, "UTF-8");
//
//					br = new BufferedReader(isr);
//
//					while ((str = br.readLine()) != null) {
//						pageStr += str;
//					}
//					System.out.println(pageStr);
//					br.close();
//					isr.close();
//					obj.close();
//					conn.disconnect();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//				String result = "";
//				Pattern pattern = Pattern.compile("entry\\.[0-9]*");
//				Matcher matcher = pattern.matcher(pageStr);
//				while (matcher.find()) {
//					// System.out.println(matcher.group());
//					result = matcher.group();
//					// String whichClass="";
//					// String method="";
//					// whichClass = result.substring(0,result.indexOf(";"));
//					// method =
//					// result.substring(result.indexOf(">")+1,result.indexOf("("));
//					// result=whichClass+"."+method;
//					System.out.println(result);
//
//				}
//			}
//		}).start();
//	}

	private void setGoogleFormFieldsByNet() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String googleFormInfo = getGoogleFormInfoByNet();
				if (!googleFormInfo.equals("")) {
					googleFormFieldsList = setGoogleFormFieldsFromGoogleFormInfo(googleFormInfo);
				}
			}
		}).start();
	}

	private String getGoogleFormInfoByNet() {
		final StringBuilder pageStr = new StringBuilder();

//		System.out.println("get");
		String str = "";

		BufferedReader br;
		try {
			URL u = new URL(
					"https://docs.google.com/forms/d/" + DebugConfig.googleFormID + "/viewform");
			InputStream obj;

			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setRequestMethod("GET");
			obj = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader((InputStream) obj,
					"UTF-8");

			br = new BufferedReader(isr);

			while ((str = br.readLine()) != null) {
				pageStr.append(str);
			}
//			System.out.println(pageStr);
			br.close();
			isr.close();
			obj.close();
			conn.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return pageStr.toString();
	}

	private List<String> setGoogleFormFieldsFromGoogleFormInfo(
			String googleFormInfo) {
		List<String> googleFormFieldsList = new ArrayList<String>();
		String result = "";
		Pattern pattern = Pattern.compile("entry\\.[0-9]*");
		Matcher matcher = pattern.matcher(googleFormInfo);
		while (matcher.find()) {
			// System.out.println(matcher.group());
			result = matcher.group();
			// String whichClass="";
			// String method="";
			// whichClass = result.substring(0,result.indexOf(";"));
			// method =
			// result.substring(result.indexOf(">")+1,result.indexOf("("));
			// result=whichClass+"."+method;
			googleFormFieldsList.add(result);
//			System.out.println(result);

		}
		return googleFormFieldsList;
	}

//	private void showLogInfo(Throwable exception, StringWriter errors) {
//		Log.e("tag", "exception1 >>>>>>>" + exception.getCause().getMessage());
//		Log.e("tag", "exception2 >>>>>>>" + exception.getMessage());
//		Log.e("tag", "exception3 >>>>>>>"
//				+ exception.getStackTrace().toString());
//		Log.e("tag", "exception4 >>>>>>>" + exception.toString());
//
//		for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
//			Log.e("tag", "exception5 >>>>>>>" + stackTraceElement.toString()
//					+ stackTraceElement.getLineNumber());
//		}
//		Log.e("tag", "exception6 >>>>>>>" + errors.toString());
//	}

	private static HttpPost makeHttpPost(String urlPath) {
		HttpPost httpPost = null;
		if (urlPath != null)
			httpPost = new HttpPost(
					"https://docs.google.com/forms/d/" + DebugConfig.googleFormID + "/formResponse"
							+ urlPath);
		return httpPost;
	}

	private static DefaultHttpClient makeHttpClient() {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpParams params = new BasicHttpParams();
		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(params, timeoutSocket);

		client.setParams(params);
		return client;
	}

	private static boolean sendLog(DefaultHttpClient client, HttpPost httpPost) {
		HttpResponse response = null;

		try {
			if (httpPost != null)
				response = client.execute(httpPost);

		} catch (IOException e) {
			if(DebugConfig.showAlertMessageBoxWhenSendLogByNetFail)
				handler.sendEmptyMessage(0);
		}

		boolean isUploadInfoSuccess = false;

		if (response != null) {
			StatusLine statusLine = response.getStatusLine();
			if (statusLine != null) {
				String statusCode = Integer.toString(response.getStatusLine()
						.getStatusCode());

				if (!(!statusCode.equals("409") && !statusCode.equals("403") && (statusCode
						.startsWith("4") || statusCode.startsWith("5")))) {
					isUploadInfoSuccess = true;
				}
			}
		}

		return isUploadInfoSuccess;
	}

	private static String makeUrlStringByCrashLogInfo(String Mac, String level,
			String Time, String PID, String TID, String Application,
			String Tag, String Text) {
		String logUploadParameter = "?";

		if (Mac != null)
			logUploadParameter = logUploadParameter
					+ getUrlEncodeString(googleFormFieldsList.get(0)) + "="
					+ getUrlEncodeString(Mac);
		if (level != null)
			logUploadParameter = logUploadParameter + "&"
					+ getUrlEncodeString(googleFormFieldsList.get(1)) + "="
					+ getUrlEncodeString(level);
		if (Time != null)
			logUploadParameter = logUploadParameter + "&"
					+ getUrlEncodeString(googleFormFieldsList.get(2)) + "="
					+ getUrlEncodeString(Time);
		if (PID != null)
			logUploadParameter = logUploadParameter + "&"
					+ getUrlEncodeString(googleFormFieldsList.get(3)) + "="
					+ getUrlEncodeString(PID);
		if (TID != null)
			logUploadParameter = logUploadParameter + "&"
					+ getUrlEncodeString(googleFormFieldsList.get(4)) + "="
					+ getUrlEncodeString(TID);
		if (Application != null)
			logUploadParameter = logUploadParameter + "&"
					+ getUrlEncodeString(googleFormFieldsList.get(5)) + "="
					+ getUrlEncodeString(Application);
		if (Tag != null)
			logUploadParameter = logUploadParameter + "&"
					+ getUrlEncodeString(googleFormFieldsList.get(6)) + "="
					+ getUrlEncodeString(Tag);
		if (Text != null)
			logUploadParameter = logUploadParameter + "&"
					+ getUrlEncodeString(googleFormFieldsList.get(7)) + "="
					+ getUrlEncodeString(Text);

		logUploadParameter += "&submit=Submit";

//		String logUploadPath = "https://docs.google.com/forms/d/12pVe4Ep3CR0l4RoUg8ah7d2Kb7Usg4MPHeN2sWV1nw0/formResponse";

		return logUploadParameter;
	}

	private static String getUrlEncodeString(String oranginalString) {
		String encodedString = "";
		try {
			encodedString = URLEncoder.encode(oranginalString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			encodedString = "";
			e.printStackTrace();
		}
		return encodedString;
	}

	private static DebugLog instance = new DebugLog();
	private static LogDispatcher logDispatcher;

	private String getFunctionName() {
		if (DebugConfig.showLineNumer) {
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
						+ Thread.currentThread().getId() + "): "
						+ st.getFileName() + ":" + st.getLineNumber() + "]";
			}
		}
		return null;
	}

	private String createMessage(String msg) {
		String functionName = getFunctionName();
		String message = (functionName == null ? msg
				: (functionName + " - " + msg));
		return message;
	}

	private void info(String msg) {
		info(DebugConfig.TAG, msg);
	}

	private void info(String tag, String msg) {
		String message = createMessage(msg);
		if (DebugConfig.showDebugLogToLogCat) {
			Log.i(tag, message);
		}
		if (DebugConfig.writeLogToSdcard || DebugConfig.sendLogByNet) {
			LogBean log = new LogBean(I, getCurrentTime(), tag, message);
			instance.writeLog(log);
		}
	}

	public static void i(String msg) {
		instance.info(msg);
	}

	public static void i(String tag, String msg) {
		instance.info(tag, msg);
	}

	public static void i(Exception e) {
		instance.info(e != null ? e.toString() : "null");
	}

	private void verbose(String msg) {
		verbose(DebugConfig.TAG, msg);
	}

	private void verbose(String tag, String msg) {
		String message = createMessage(msg);
		if (DebugConfig.showDebugLogToLogCat) {
			Log.v(tag, message);
		}
		if (DebugConfig.writeLogToSdcard || DebugConfig.sendLogByNet) {
			LogBean log = new LogBean(V, getCurrentTime(), tag, message);
			instance.writeLog(log);
		}
	}

	public void v(String msg) {
		instance.v(msg);
	}

	public void v(String tag, String msg) {
		instance.v(tag, msg);
	}

	private void debug(String msg) {
		debug(DebugConfig.TAG, msg);
	}

	private void debug(String tag, String msg) {
		String message = createMessage(msg);
		if (DebugConfig.showDebugLogToLogCat) {
			Log.d(DebugConfig.TAG, message);
		}
		if (DebugConfig.writeLogToSdcard || DebugConfig.sendLogByNet) {
			LogBean log = new LogBean(D, getCurrentTime(), tag, message);
			instance.writeLog(log);
		}
	}

	private void error(String msg) {
		error(DebugConfig.TAG, msg);
	}

	private void error(String tag, String msg) {
		String message = createMessage(msg);
		if (DebugConfig.showDebugLogToLogCat) {
			Log.e(tag, message);
		}
		if (DebugConfig.writeLogToSdcard || DebugConfig.sendLogByNet) {
			LogBean log = new LogBean(E, getCurrentTime(), tag, message);
			instance.writeLog(log);
		}
	}

	private void warn(String msg) {
		warn(DebugConfig.TAG, msg);
	}

	private void warn(String tag, String msg) {
		String message = createMessage(msg);
		if (DebugConfig.showDebugLogToLogCat) {
			Log.w(DebugConfig.TAG, message);
		}
		if (DebugConfig.writeLogToSdcard || DebugConfig.sendLogByNet) {
			LogBean log = new LogBean(W, getCurrentTime(), tag, message);
			instance.writeLog(log);
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

	public static void e(String tag, String msg) {
		instance.error(tag, msg);
	}

	public static void w(String msg) {
		instance.warn(msg);
	}

	public static void w(Exception e) {
		instance.warn(e != null ? e.toString() : "null");
	}

	private static void resetLogFile() {
		File file = new File(DebugConfig.logFile);
		file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeLog(LogBean log) {
		// FileUtil.writeLogToFile(content);
		logDispatcher.add(log);
	}

	public static void quit() {
		logDispatcher.quit();
	}

	private String getCurrentTime() {
		long time = System.currentTimeMillis();
		Date d = new Date(time);
		DateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss.sss");
		String date = df.format(d);
		return date;
	}
	
	private void showCrashMessageBox(Thread thread, Throwable exception){
		if(DebugConfig.showCrashMessageBoxWhenAppCrash){
			defaultExceptionHandler.uncaughtException(
					thread, exception);
		}
	}
	
	class LogDispatcher extends Thread {

		private static final String TAG = "LogDispatcher";

		private final BlockingQueue<LogBean> mMarkerQueue;
		private volatile boolean mQuit = false;
		// private final LogWriter mLogWriter;

		private int count = 0;

		private StringBuilder logInfos = new StringBuilder();

		private long lastUpdateTime;

		private boolean isImmediateUpload = false;

		private boolean isUploadLogSuccess = false;

		private boolean isDisableAdd = false;

		LogDispatcher() {
			mMarkerQueue = new ArrayBlockingQueue<LogBean>(50);
		}

		void add(LogBean logInfo) {
			if (!isDisableAdd) {
				try {
					mMarkerQueue.put(logInfo);
				} catch (InterruptedException e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		}

		public void quit() {
			mQuit = true;
			interrupt();
		}

		@Override
		public void run() {
//			android.os.Process
//					.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
			LogBean string;
			lastUpdateTime = java.lang.System.currentTimeMillis()
					+ DebugConfig.update;
			while (true) {
				try {
					string = mMarkerQueue.take();
				} catch (InterruptedException e) {
					if (mQuit) {
						return;
					}
					continue;
				}

				// if (mLogWriter != null) {
				// mLogWriter.writeLog(marker);
				// }

				if (DebugConfig.writeLogToSdcard && string != null) {
					String s = string.getLevel() + " " + string.getTime() + " "
							+ string.getTag() + "\n" + string.getText() + "\n";
					FileUtil.writeLogToFile(s);
				}
				
				if(string != null)
					count++;

				if (logInfos.length() != 0)
						logInfos.append("\n");
					logInfos.append(string.getText());
				
				if (DebugConfig.sendLogByNet && ((isImmediateUpload && count > 0 && mMarkerQueue.size() == 0)
						|| count >= DebugConfig.logUploadSize
						|| java.lang.System.currentTimeMillis() > lastUpdateTime)) {
					lastUpdateTime = java.lang.System.currentTimeMillis()
							+ DebugConfig.update;
					if (googleFormFieldsList.size() != 0) {
						String urlPath = DebugLog.makeUrlStringByCrashLogInfo(
								DebugLog.mac, string.getLevel(),
								string.getTime(), null, null, null,
								string.getTag(), logInfos.toString());

						if (DebugLog.sendLog(DebugLog.makeHttpClient(),
								DebugLog.makeHttpPost(urlPath))) {
							logInfos.setLength(0);
						}
					} else {
						setGoogleFormFieldsByNet();
					}
					count = 0;
				}
				if (isDisableAdd && mMarkerQueue.size() == 0)
					mQuit = true;

			}
		}
	}
}
