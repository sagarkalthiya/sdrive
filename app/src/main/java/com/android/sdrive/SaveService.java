package com.android.sdrive;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.sdrive.Component.Utils;
import com.android.sdrive.DataBase.DatabaseHelper;
import com.android.sdrive.DataBase.Tables.FileStorage;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;

import java.io.File;
import java.util.HashMap;

public class SaveService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        final DatabaseHelper database = new DatabaseHelper(getApplicationContext());
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
             final String file_id = intent.getStringExtra("file_id");
             String file_name = intent.getStringExtra("file_name");
             String file_path = intent.getStringExtra("file_path");


                Log.w("key", ">>>>>>>>>>>>>>>>>>>>>>>>>"+file_id);
                Log.w("key", ">>>>>>>>>>>>>>>>>>>>>>>>>"+file_name);
                Log.w("key", ">>>>>>>>>>>>>>>>>>>>>>>>>"+file_path);

              File root = android.os.Environment.getExternalStorageDirectory();
                AndroidNetworking.download("http://neemuchmandirate.com/sagar_vlocker_files/"+file_path, root.getAbsolutePath() + "/Vlocker/Download/", file_name)
                        .setTag("downloadTest")
                        .setPriority(Priority.MEDIUM)
                        .setPercentageThresholdForCancelling(50) // even if at the time of cancelling it will not cancel if 50%
                        .build()                                 // downloading is done.But can be cancalled with forceCancel.
                        .setDownloadProgressListener(new DownloadProgressListener() {
                            @Override
                            public void onProgress(long bytesDownloaded, long totalBytes) {
                                // do anything with progress

                                downloadProgresh(intent.getStringExtra("file_id"),(int)(bytesDownloaded*100/totalBytes));
                            }
                        })
                        .startDownload(new DownloadListener() {
                            @Override
                            public void onDownloadComplete() {

                                // do anything after completion
                                Log.w("key", ">>>>>>>>>>>>>>>>>>>>>>>>>download done");


                                HashMap<String, String> data = new HashMap<String, String>();
                                data.put(FileStorage.File_Upload_Status, "3");
                                if (database.Update(FileStorage.TABLE_NAME, data, new String[]{FileStorage.File_ID}, new String[]{file_id})) {

                                    DownloadStatus(intent.getStringExtra("file_id"));
                                }



                            }

                            @Override
                            public void onError(ANError error) {
                                // handle error'
                                //DownloadStatus(intent.getStringExtra("file_id"));
                                Log.w("key", ">>>>>>>>>>>>>>>>>>>>>>>>>download error" + error);
                            }
                        });

            }
        });

        return START_NOT_STICKY;
    }

    private void downloadProgresh(String id,int percentage){
        Intent intent = new Intent("file_download_progress");
        intent.putExtra("id", id);
        intent.putExtra("percentage", percentage);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    private void DownloadStatus(String id){
        Intent intent = new Intent("file_download_status");
        intent.putExtra("id", id);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {

    }
}
