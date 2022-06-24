package com.mz.totalmemsizeinfo;

import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MAZHUANG";

    private TextView sizeTxt;

    private Context mContext;

    private int unit = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;

        sizeTxt = findViewById(R.id.sizeTxt);

        queryStorageSize();
    }

    private void queryStorageSize() {
        StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        try {
            Method getVolumes = StorageManager.class.getDeclaredMethod("getVolumes");
            List<Object> getVolumeInfo = (List<Object>) getVolumes.invoke(storageManager);
            long total = 0L, used = 0L;
            for (Object obj : getVolumeInfo) {

                Field getType = obj.getClass().getField("type");
                int type = getType.getInt(obj);

                if (type == 1) {
                    long totalSize = 0L;
                    //获取内置内存总大小
                    unit = 1000;
                    Method getFsUuid = obj.getClass().getDeclaredMethod("getFsUuid");
                    String fsUuid = (String) getFsUuid.invoke(obj);
                    totalSize = getTotalSize(fsUuid);

                    long systemSize = 0L;
                    Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
                    boolean readable = (boolean) isMountedReadable.invoke(obj);
                    if (readable) {
                        Method file = obj.getClass().getDeclaredMethod("getPath");
                        File f = (File) file.invoke(obj);
                        if (totalSize == 0) {
                            totalSize = f.getTotalSpace();
                        }
                        systemSize = totalSize - f.getTotalSpace();
                        used += totalSize - f.getFreeSpace();
                        total += totalSize;
                    }

                } else if (type == 0) {
                    //外置存储
                    Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
                    boolean readable = (boolean) isMountedReadable.invoke(obj);
                    if (readable) {
                        Method file = obj.getClass().getDeclaredMethod("getPath");
                        File f = (File) file.invoke(obj);
                        used += f.getTotalSpace() - f.getFreeSpace();
                        total += f.getTotalSpace();
                    }
                } else if (type == 2) {

                }
            }
            Log.d(TAG, "总内存 total = " + getUnit(total, 1000) + " \n已用 used = " + getUnit(used, 1000) + "\n可用 available = " + getUnit(total - used, 1000));
            sizeTxt.setText("总内存 total = " + getUnit(total, 1000) + " \n已用 used = " + getUnit(used, 1000) + "\n可用 available = " + getUnit(total - used, 1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取总共容量大小，包括系统大小
     */
    private long getTotalSize(String fsUuid) {
        try {
            UUID id;
            if (fsUuid == null) {
                id = StorageManager.UUID_DEFAULT;
            } else {
                id = UUID.fromString(fsUuid);
            }
            StorageStatsManager stats = getSystemService(StorageStatsManager.class);
            return stats.getTotalBytes(id);
        } catch (NoSuchFieldError | NoClassDefFoundError | NullPointerException | IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private String[] units = {"B", "KB", "MB", "GB", "TB"};

    /**
     * 单位转换
     */
    private String getUnit(float size, int unit) {
        int index = 0;
        while (size > unit && index < 4) {
            size = size / unit;
            index++;
        }
        return String.format(Locale.getDefault(), " %.2f %s", size, units[index]);
    }


}