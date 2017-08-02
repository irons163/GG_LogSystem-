package com.example.try_mylog;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.senao.debug_log.DebugLog;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DebugLog myLog2 = DebugLog.getInstance();
		myLog2.init(getApplicationContext(), "12pVe4Ep3CR0l4RoUg8ah7d2Kb7Usg4MPHeN2sWV1nw0", true);
		myLog2.setShowDebugLogToLogCat(true);
		myLog2.setWriteLogToSdcard(true);
		myLog2.setSendLogByNet(true);
		DebugLog.d("ssSDds");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				DebugLog.e("GG", "YYY");
				}
			}
		}).start();
		
		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DebugLog.e("XXX");
			
			String a = null;
			String b = null;

			a.equals(b);
		}
		
//		MyLog myLog = MyLog.getInstance();
//		myLog.init(getApplicationContext());
//

		

		
//		final Handler handler = new Handler(){
//			public void handleMessage(android.os.Message msg) {
//
//				Builder builder = new Builder(MainActivity.this);
//				builder.setTitle("Cannot send debug log!");
//				builder.setPositiveButton("OK",
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								// TODO Auto-generated method stub
////								defaultExceptionHandler.uncaughtException(
////										thread, exception);
//							}
//						});
//				builder.show();
//			}
//		};
		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				
//		DefaultHttpClient client = new DefaultHttpClient();
//		HttpParams params = new BasicHttpParams();
//		// params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
//		// params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
//		// HttpParams httpParameters = new BasicHttpParams();
//		// Set the timeout in milliseconds until a connection is established.
//		// The default value is zero, that means the timeout is not used.
//		int timeoutConnection = 3000;
//		HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
//		// Set the default socket timeout (SO_TIMEOUT)
//		// in milliseconds which is the timeout for waiting for data.
//		int timeoutSocket = 5000;
//		HttpConnectionParams.setSoTimeout(params, timeoutSocket);
//
//		// DefaultHttpClient httpClient = new DefaultHttpClient(params);
//		// HttpResponse response = httpClient.execute(httpGet);
//		client.setParams(params);
//		HttpResponse response = null;

//		long time = System.currentTimeMillis();
//		Date d = new Date(time);
//		DateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss.sss");
//		String date = df.format(d);
//
//		String tag = "CrashError";
//		String s = MyLog.makeUrlStringByCrashLogInfo(MyLog.mac,
//				MyLog.E, date, null, null, null, tag, "aa\nbb");
//		HttpPost httpPost = new HttpPost("https://docs.google.com/forms/d/12pVe4Ep3CR0l4RoUg8ah7d2Kb7Usg4MPHeN2sWV1nw0/formResponse"+s);

//		try {
//			response = client.execute(httpPost);
//		} catch (IOException e) {
//			handler.sendEmptyMessage(0);
//		}
//		
//		if (response != null) {
//			StatusLine statusLine = response.getStatusLine();
//			if (statusLine != null) {
//				String statusCode = Integer.toString(response.getStatusLine()
//						.getStatusCode());
//
//				if (!statusCode.equals("409") // 409 return code means
//												// that the
//												// report has been
//												// received
//												// already. So we can
//												// discard it.
//						&& !statusCode.equals("403") // a 403 error code
//														// is an
//														// explicit data
//														// validation
//														// refusal
//														// from the
//														// server. The
//														// request must
//														// not be
//														// repeated.
//														// Discard it.
//						&& (statusCode.startsWith("4") || statusCode
//								.startsWith("5"))) {
//					// String responseString =
//					// EntityUtils.toString(entity, "UTF-8");
//					handler.sendEmptyMessage(0);
//				} else {
////					defaultExceptionHandler
////							.uncaughtException(thread, exception);
//				}
//			} else {
////				defaultExceptionHandler.uncaughtException(thread, exception);
//			}
//		} else {
////			defaultExceptionHandler.uncaughtException(thread, exception);
//		}
//		
//			}
//		}).start();
	}
	
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
//		MyLog2.quit();
		
		super.onDestroy();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
