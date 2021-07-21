package com.android.sdrive;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.sdrive.Component.Utils;
import com.android.sdrive.DataBase.DatabaseHelper;
import com.android.sdrive.DataBase.Tables.FileStorage;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.androidnetworking.AndroidNetworking;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class UploadService extends Service {
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
        if (intent.getAction().equals(Utils.UploadFileIntent)) {

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    AndroidNetworking.upload(All_URL_End_Point.UPLOAD_URL)
                            .addMultipartFile("file", new File(intent.getStringExtra("file_path")))

                            .addMultipartParameter("user_id",intent.getStringExtra("user_id"))
                            .addMultipartParameter("file_id",intent.getStringExtra("file_id"))
                            .addMultipartParameter("file_name",intent.getStringExtra("file_name"))
                            //.addMultipartParameter("file_path",intent.getStringExtra("file_path"))
                            .addMultipartParameter("file_type",intent.getStringExtra("file_type"))
                            .addMultipartParameter("file_size",intent.getStringExtra("file_size"))
                            .addMultipartParameter("file_upload_status","0")
                            .addMultipartParameter("file_upload_time",intent.getStringExtra("file_upload_time"))
                            .addMultipartParameter("time_stamp",intent.getStringExtra("time_stamp"))

                            .setTag(intent.getStringExtra("file_id"))
                            .setPriority(Priority.HIGH)
                            .build()
                            .setUploadProgressListener(new UploadProgressListener() {
                                @Override
                                public void onProgress(long bytesUploaded, long totalBytes) {
                                 //   Log.w("key", "--------------->" + String.valueOf(bytesUploaded*100/totalBytes));
                                    updateProgresh(intent.getStringExtra("file_id"),(int)(bytesUploaded*100/totalBytes));
                                }
                            })
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    Toast.makeText(UploadService.this, "h"+response.toString(), Toast.LENGTH_SHORT).show();

                                    try {
                                        if (response.getBoolean("status")) {
                                            HashMap<String, String> data = new HashMap<String, String>();
                                            data.put(FileStorage.File_Upload_Status, "1");
                                            data.put(FileStorage.File_Path, response.getString("data"));
                                            if (database.Update(FileStorage.TABLE_NAME, data, new String[]{FileStorage.File_ID}, new String[]{response.getString("file_id")})) {

                                                updateStatush(response.getString("file_id"),response.getString("data"));
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    Log.w("key", ">>>>>>>>" + response.toString());
                                }

                                @Override
                                public void onError(ANError error) {
                                    // handle error
                                }
                            });
                }
            });

        }
        return START_NOT_STICKY;
    }

    private void updateProgresh(String id,int percentage){
        Intent intent = new Intent("file_upload_progress");
        intent.putExtra("id", id);
        intent.putExtra("percentage", percentage);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    private void updateStatush(String id,String path){
        Intent intent = new Intent("file_upload_status");
        intent.putExtra("id", id);
        intent.putExtra("path", path);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {

    }
}
