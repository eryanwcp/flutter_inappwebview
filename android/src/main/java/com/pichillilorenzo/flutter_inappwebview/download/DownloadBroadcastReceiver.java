/**
 * Copyright (c) 2012-2020 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.pichillilorenzo.flutter_inappwebview.download;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.FileProvider;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2020-05-15 
 */
public class DownloadBroadcastReceiver extends BroadcastReceiver {

    static final String LOG_TAG = "DownloadBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            //下载完成了
            //获取当前完成任务的ID
            long  id = intent.getLongExtra( DownloadManager.EXTRA_DOWNLOAD_ID , -1 );

            DownloadManager.Query query = new DownloadManager.Query().setFilterById(id);
            Cursor cursor = dm.query(query);
            if (cursor == null || !cursor.moveToFirst()) {
                return;
            }
            String fileName = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
            String mimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + fileName);
            Intent openIntent = new Intent(Intent.ACTION_VIEW);
            openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Uri uri = null;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0以上
                uri = FileProvider.getUriForFile(context,  context.getPackageName() + ".fileprovider", file);
                openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(file);
            }

            openIntent.setDataAndType(uri, mimeType);

            try {
                context.startActivity(openIntent);
            } catch (ActivityNotFoundException e) {
                Log.e(LOG_TAG, id + ": " + e.toString());
            }
        }else if(DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())){
            long[] ids = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
            //点击通知栏取消下载
            dm.remove(ids);
            Toast.makeText(context, "已经取消下载 ", Toast.LENGTH_LONG).show();
        }

    }
}
