package com.mz.totalmemsizeinfo;

import android.app.ActivityManager;
import android.content.Context;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;

public class SystemMemory {

    private static String TAG = "SystemMemory";

    private static int initial_memory_int = 1;

    /**
     * * 获取android当前可用运行内存大小
     * * @param context
     * *
     */
    public static String getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        // mi.availMem; 当前系统的可用内存
        return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
    }

    private static String[] units = {"B", "KB", "MB", "GB", "TB"};

    /**
     * * 获取android总运行内存大小
     * * @param context
     * *
     */
    public static int getTotalMemory(Context context) {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            arrayOfString = str2.split("\\s+");
            // 获得系统总内存，单位是KB
            int i = Integer.valueOf(arrayOfString[1]).intValue();
            // int值乘以1024转换为long类型
            initial_memory = new Long((long) i * 1024);
            double initial_memory_double = (initial_memory / (1024.0 * 1024.0 * 1024.0));
            DecimalFormat df = new DecimalFormat("######0"); // 四色五入转换成整数
            initial_memory_int = Integer.parseInt(df.format(initial_memory_double));
            Log.d(TAG, "TotalMemory = " + initial_memory_int);
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return initial_memory_int;
    }

}