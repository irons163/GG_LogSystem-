package com.example.try_mylog;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

public class MyLog implements UncaughtExceptionHandler {
	public static final String V = "V";
	public static final String D = "D";
	public static final String I = "I";
	public static final String W = "W";
	public static final String E = "E";
	public static final String A = "A";

	private Context mContext;

	private Thread.UncaughtExceptionHandler defaultExceptionHandler;

	private static MyLog customException;

	public static String mac;

	static Handler handler;
	
	private MyLog() {
	}

	public static MyLog getInstance() {
		if (customException == null) {
			customException = new MyLog();
		}
		return customException;
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
								defaultExceptionHandler.uncaughtException(
										thread, exception);
							}
						});
				builder.show();
			}
		};

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (defaultExceptionHandler != null) {		
					StringWriter errors = new StringWriter();
					exception.printStackTrace(new PrintWriter(errors));
					
//					showLogInfo(exception, errors);
					
					String[] message = errors.toString().split("\n");
					long time = System.currentTimeMillis();
					Date d = new Date(time);
					DateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss.sss");
					String date = df.format(d);

					String tag = "CrashError";
					StringBuilder text = new StringBuilder();

					for (int i = 0; i < message.length; i++) {
						text.append(message[i]);
						if (i != message.length - 1) {
							text.append("\n");
						}
					}

					String urlPath = makeUrlStringByCrashLogInfo(mac, E, date,
							null, null, null, tag, text.toString());

					if(sendLog(makeHttpClient(), makeHttpPost(urlPath)))
						defaultExceptionHandler.uncaughtException(
								thread, exception);
				}
			}
		}).start();
	}

	public void init(Context context) {
		mContext = context;

		WifiManager wm = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		mac = wm.getConnectionInfo().getMacAddress();

		defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	// public MyLog() {
	// // TODO Auto-generated constructor stub
	// String str;
	// BufferedReader br;
	// try {
	// URL u=new URL("http://www.nctu.edu.tw/");
	// Object obj;
	//
	// obj = u.getContent();
	//
	// InputStreamReader isr= new InputStreamReader((InputStream) obj,"UTF-8");
	// br = new BufferedReader(isr);
	//
	// while((str=br.readLine())!=null)
	// System.out.println(str);
	// br.close();
	//
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	
	private void showLogInfo(Throwable exception, StringWriter errors){
		Log.e("tag", "exception1 >>>>>>>"
				+ exception.getCause().getMessage());
		Log.e("tag", "exception2 >>>>>>>" + exception.getMessage());
		Log.e("tag", "exception3 >>>>>>>"
				+ exception.getStackTrace().toString());
		Log.e("tag", "exception4 >>>>>>>" + exception.toString());

		for (StackTraceElement stackTraceElement : exception
				.getStackTrace()) {
			Log.e("tag",
					"exception5 >>>>>>>"
							+ stackTraceElement.toString()
							+ stackTraceElement.getLineNumber());
		}
		Log.e("tag", "exception6 >>>>>>>" + errors.toString());
	}
	
	public static HttpPost makeHttpPost(String urlPath){
		HttpPost httpPost = null;
		if (urlPath != null)
			httpPost = new HttpPost(
					"https://docs.google.com/forms/d/12pVe4Ep3CR0l4RoUg8ah7d2Kb7Usg4MPHeN2sWV1nw0/formResponse"
							+ urlPath);
		return httpPost;
	}
	
	public static DefaultHttpClient makeHttpClient(){
		DefaultHttpClient client = new DefaultHttpClient();
		HttpParams params = new BasicHttpParams();
		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(params,
				timeoutConnection);
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(params, timeoutSocket);

		client.setParams(params);
		return client;
	}

	public static boolean sendLog(DefaultHttpClient client, HttpPost httpPost) {
		HttpResponse response = null;

		try {
			if (httpPost != null)
				response = client.execute(httpPost);

		} catch (IOException e) {
			handler.sendEmptyMessage(0);
		}

		boolean isUploadInfoSuccess = false;
		
		if (response != null) {
			StatusLine statusLine = response.getStatusLine();
			if (statusLine != null) {
				String statusCode = Integer.toString(response
						.getStatusLine().getStatusCode());

				if (!(!statusCode.equals("409") && !statusCode.equals("403") && (statusCode.startsWith("4") || statusCode
								.startsWith("5")))) {							
					isUploadInfoSuccess = true;
				} 
			}
		} 
		
		return isUploadInfoSuccess;
	}

	public static String makeUrlStringByCrashLogInfo(String Mac, String level,
			String Time, String PID, String TID, String Application,
			String Tag, String Text) {
		String logUploadParameter = "?";

		if (Mac != null)
			logUploadParameter = logUploadParameter
					+ getUrlEncodeString("entry.1737553841") + "="
					+ getUrlEncodeString(Mac);
		if (level != null)
			logUploadParameter = logUploadParameter + "&"
					+ getUrlEncodeString("entry.1157992142") + "="
					+ getUrlEncodeString(level);
		if (Time != null)
			logUploadParameter = logUploadParameter + "&"
					+ getUrlEncodeString("entry.347303194") + "="
					+ getUrlEncodeString(Time);
		if (PID != null)
			logUploadParameter = logUploadParameter + "&"
					+ getUrlEncodeString("entry.823423011") + "="
					+ getUrlEncodeString(PID);
		if (TID != null)
			logUploadParameter = logUploadParameter + "&"
					+ getUrlEncodeString("entry.1814456667") + "="
					+ getUrlEncodeString(TID);
		if (Application != null)
			logUploadParameter = logUploadParameter + "&"
					+ getUrlEncodeString("entry.1857980750") + "="
					+ getUrlEncodeString(Application);
		if (Tag != null)
			logUploadParameter = logUploadParameter + "&"
					+ getUrlEncodeString("entry.29067698") + "="
					+ getUrlEncodeString(Tag);
		if (Text != null)
			logUploadParameter = logUploadParameter + "&"
					+ getUrlEncodeString("entry.447091630") + "="
					+ getUrlEncodeString(Text);

		logUploadParameter += "&submit=Submit";

		String logUploadPath = "https://docs.google.com/forms/d/12pVe4Ep3CR0l4RoUg8ah7d2Kb7Usg4MPHeN2sWV1nw0/formResponse";

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
}
