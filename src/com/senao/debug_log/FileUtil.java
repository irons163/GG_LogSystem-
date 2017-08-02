package com.senao.debug_log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;

import android.util.Log;

public class FileUtil {
	private static ObjectOutputStream mObjectOutputStream;
	
    public static void writeLogBySerializableObject(String logInfo) {
    	File mLogFile = new File(DebugConfig.ROOT, DebugConfig.FILE_NAME);

        try {
        	if(!mLogFile.exists())
        		mLogFile.createNewFile();
            if (mObjectOutputStream == null) {
                mObjectOutputStream = new ObjectOutputStream(new FileOutputStream(mLogFile));
            }
            mObjectOutputStream.writeObject(logInfo);
        } catch (IOException e) {
            Log.e(DebugConfig.TAG, e.getMessage(), e);
        }
    }
    
    public static void writeLogToFile(String content){
		try {
			File file = new File(DebugConfig.logFile);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter write = new FileWriter(file, true);
			write.write(content);
			write.flush();
			write.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}


