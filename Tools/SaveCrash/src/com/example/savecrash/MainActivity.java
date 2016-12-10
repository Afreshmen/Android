package com.example.savecrash;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;


public class MainActivity extends Activity implements UncaughtExceptionHandler {

	private String TAG = "test";
	
	private Thread.UncaughtExceptionHandler mExceptionHandler;
	
	private final boolean mSaveCrashLog = true;		//是否保存crash日志
	
	private TextView tv;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initException();
        
		tv.setText("test");	//测试例子,空指针crash
    }
    
    public void initException() {
    	this.getExternalFilesDir(null);
        mExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
    }
    
	@Override
	public void uncaughtException(Thread arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		saveCrashInfo2File(arg1, getApplicationContext());
		if (mExceptionHandler != null && arg1 != null) {
			mExceptionHandler.uncaughtException(arg0, arg1);
		}
	}

	private String saveCrashInfo2File(Throwable ex, Context context) {
		if (mSaveCrashLog) {
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				StringBuffer sb = new StringBuffer();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
				Writer writer = new StringWriter();
				PrintWriter pw = new PrintWriter(writer);
				ex.printStackTrace(pw);
				Throwable cause = ex.getCause();
				while (cause != null) {
					cause.printStackTrace(pw);
					cause = cause.getCause();
				}
				pw.close();
				String result = writer.toString();
				String time = format.format(new java.util.Date());
				sb.append(time + "==  ==  ==" + result);

				long timetamp = System.currentTimeMillis();

				String fileName = getCurrentVersion(context) + "-crash-" + time + "-" + timetamp + ".log";	// 保存路径

				try {
					File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
							+ "crash_test");
					if (!dir.exists()) {
						dir.mkdir();
					}
					FileOutputStream fos = new FileOutputStream(new File(dir, fileName));
					fos.write(sb.toString().getBytes());
					fos.close();
					return fileName;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			else {
				Log.d(TAG, "getExternalStorage not exist");
			}
		}
		return null;
	}
	
	private String getCurrentVersion(Context context) {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				versionName = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return versionName;
	}
}
