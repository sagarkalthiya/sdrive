package com.android.sdrive.Home.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.gallery.FilePickerBuilder;
import com.android.gallery.FilePickerConst;
import com.android.gallery.models.sort.SortingTypes;
import com.android.gallery.utils.Orientation;
import com.android.sdrive.All_URL_End_Point;
import com.android.sdrive.Component.Elements.File_coper;
import com.android.sdrive.Component.SessionManager;
import com.android.sdrive.Component.Utils;
import com.android.sdrive.DataBase.DatabaseHelper;
import com.android.sdrive.DataBase.Tables.FileStorage;
import com.android.sdrive.Home.Fragment.Adapter.File_Adapter;
import com.android.sdrive.Home.Fragment.Adapter.File_model;
import com.android.sdrive.R;
import com.android.sdrive.UploadService;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import butterknife.ButterKnife;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class HomeFragment extends Fragment {


    File_Adapter file_adapter = null;
    ArrayList<File_model> file_list = new ArrayList<>();
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;

    DatabaseHelper databaseHelper;
    ProgressDialog progressDialog;
    SessionManager session;
    Toolbar toolbar;
    Context context;
    EditText search_et;
    String user_id;
    private int MAX_ATTACHMENT_COUNT = 10;
    public static final int RequestPermissionCode = 7;


    private static final int PHOTO_REQUEST_CODE = 531;
    private static final int VIDEO_REQUEST_CODE = 532;
    private static final int AUDIO_REQUEST_CODE = 533;
    private static final int DOC_REQUEST_CODE = 534;

    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<String> videoPaths = new ArrayList<>();
    private ArrayList<String> audioPaths = new ArrayList<>();
    private ArrayList<String> docPaths = new ArrayList<>();
    SwipeRefreshLayout mSwipeRefreshLayout;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, null);
        AndroidNetworking.initialize(getActivity());
        session = new SessionManager(getActivity());
        session.checkLogin();
        databaseHelper = new DatabaseHelper(getActivity());
        ButterKnife.bind(this, rootView);
        HashMap<String, String> user = session.getUserDetails();

        user_id = user.get(SessionManager.KEY_USER_ID);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setTitle("please wait..."); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.setCancelable(false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);

        //mSwipeRefreshLayout.setColorScheme(getResources().getColor(R.color.colorPrimary), R.color.green, R.color.orange, R.color.purple);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fatch_file();
            }
        });

        fatch_file();
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.upload_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(CheckingPermissionIsEnabledOrNot())
                {
                   // Toast.makeText(MainActivity.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
                    showBottomSheetDialog();
                }

                // If, If permission is not enabled then else condition will execute.
                else {

                    //Calling method to enable permission.
                    RequestMultiplePermission();

                }


            }
        });


        recyclerView = (RecyclerView) rootView.findViewById(R.id.File_RecyclerView);

        layoutManager = new LinearLayoutManager(getActivity());
        // layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(layoutManager);
        file_adapter = new File_Adapter(getActivity(), file_list,this);
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(file_adapter);


        search_et = (EditText) rootView.findViewById(R.id.search_et);
        search_et.setFocusable(false);
        search_et.setFocusableInTouchMode(false);
        search_et.setClickable(false);
        search_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_et.setFocusable(true);
                search_et.setFocusableInTouchMode(true);
                search_et.setClickable(true);
            }
        });

        search_et.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // filter((String) s);

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {


            }

            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        // getCenterHeader();

        Drives();
        return rootView;
    }

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        ImageView audio = (ImageView) view.findViewById(R.id.audio);
        ImageView video = (ImageView) view.findViewById(R.id.video);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        ImageView document = (ImageView) view.findViewById(R.id.document);

        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickAudio();
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickVideo();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto();
            }
        });

        document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickDoc();
            }
        });


        BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(view);
        dialog.show();
    }

    public void onPickAudio() {
        String[] AudioType = {".mp3"};
        audioPaths.clear();
        FilePickerBuilder.getInstance()
                .setMaxCount(MAX_ATTACHMENT_COUNT)
                .setSelectedFiles(audioPaths)
                .setActivityTheme(R.style.FileLibrary)
                .setActivityTitle("Audio")
                .addFileSupport("MP3", AudioType, R.drawable.icon_file_mp3)
                .enableDocSupport(false)
                .enableSelectAll(true)
                .sortDocumentsBy(SortingTypes.name)
                .withOrientation(Orientation.PORTRAIT_ONLY)
                .pickFile(this, AUDIO_REQUEST_CODE);

    }

    public void onPickDoc() {

        docPaths.clear();
        FilePickerBuilder.getInstance()
                .setMaxCount(MAX_ATTACHMENT_COUNT)
                .setSelectedFiles(docPaths)
                .setActivityTheme(R.style.FileLibrary)
                .setActivityTitle("Please select doc")
                .addFileSupport("ZIP", Utils.ZipType, R.drawable.icon_file_zips)
                .addFileSupport("PDF", Utils.PdfType, R.drawable.icon_file_pdfs)
                .addFileSupport("DOC", Utils.DocType, R.drawable.icon_file_docs)
                .addFileSupport("XLS", Utils.ExcelType, R.drawable.icon_file_xlss)
                .addFileSupport("PPT", Utils.PptType, R.drawable.icon_file_ppts)
                .addFileSupport("TXT", Utils.TxtType, R.drawable.icon_file_txts)
                .addFileSupport("APK", Utils.ApkType, R.drawable.icon_file_apk)
                .enableDocSupport(false)
                .enableSelectAll(true)
                .sortDocumentsBy(SortingTypes.name)
                .withOrientation(Orientation.PORTRAIT_ONLY)
                .pickFile(this, DOC_REQUEST_CODE);

    }


    public void onPickPhoto() {
        photoPaths.clear();
        FilePickerBuilder.getInstance()
                .setMaxCount(MAX_ATTACHMENT_COUNT)
                .setSelectedFiles(photoPaths)
                .setActivityTheme(R.style.FileLibrary)
                .setActivityTitle("Please select media")
                .enableVideoPicker(false)
                .enableCameraSupport(false)
                .showGifs(false)
                .showFolderView(true)
                .enableSelectAll(false)
                .enableImagePicker(true)
                .withOrientation(Orientation.PORTRAIT_ONLY)
                .pickPhoto(this, PHOTO_REQUEST_CODE);

    }


    public void onPickVideo() {
        videoPaths.clear();
        FilePickerBuilder.getInstance()
                .setMaxCount(MAX_ATTACHMENT_COUNT)
                .setSelectedFiles(videoPaths)
                .setActivityTheme(R.style.FileLibrary)
                .setActivityTitle("Please select media")
                .enableVideoPicker(true)
                .enableCameraSupport(false)
                .showGifs(false)
                .showFolderView(true)
                .enableSelectAll(false)
                .enableImagePicker(false)
                .withOrientation(Orientation.PORTRAIT_ONLY)
                .pickPhoto(this, VIDEO_REQUEST_CODE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                    for (int v = 0; v < photoPaths.size(); v++) {
                        file_hendler(photoPaths.get(v));
                    }
                }
                break;
            case VIDEO_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    videoPaths = new ArrayList<>();
                    videoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                    for (int x = 0; x < videoPaths.size(); x++) {
                        file_hendler(videoPaths.get(x));
                    }
                }
                break;

            case AUDIO_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    audioPaths = new ArrayList<>();
                    audioPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                    for (int y = 0; y < audioPaths.size(); y++) {
                        file_hendler(audioPaths.get(y));
                    }
                }
                break;
            case DOC_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                    for (int z = 0; z < docPaths.size(); z++) {
                        file_hendler(docPaths.get(z));
                    }
                }
                break;
        }

    }

    public static byte[] decodeFile(byte[] key, byte[] fileData) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        byte[] decrypted = cipher.doFinal(fileData);

        return decrypted;
    }


    public void file_hendler(String filepath) {
        final File_coper file_coper = new File_coper(new File_coper.AsyncResponse() {
            @Override
            public void processFinish(HashMap<String, String> Data) {

                HashMap<String, String> data = new HashMap<String, String>();
                data.put(FileStorage.File_ID, Data.get(Utils.File_Id));
                data.put(FileStorage.File_Name, Data.get(Utils.File_Name));
                data.put(FileStorage.File_Path, Data.get(Utils.File_Path));
                data.put(FileStorage.File_Type, Data.get(Utils.File_Type));
                data.put(FileStorage.File_Size, Data.get(Utils.File_Size));
                data.put(FileStorage.File_Upload_Status, "0");
                data.put(FileStorage.File_Upload_Time, Utils.getTimestamp("yyyy-MM-dd HH:mm:ss"));
                data.put(FileStorage.TimeStamp, Utils.getTimestamp("yyyy-MM-dd"));

                //insert all message in data base
                int isInserted = databaseHelper.Insert(FileStorage.TABLE_NAME, data);
                if (isInserted != -1) {

                    File_model model = new File_model();
                    model.setTimeStamp(Utils.getTimestamp("yyyy-MM-dd"));
                    model.setFileid(Data.get(Utils.File_Id));
                    model.setFileName(Data.get(Utils.File_Name));
                    model.setFileType(Data.get(Utils.File_Type));
                    model.setFilePath(Data.get(Utils.File_Path));
                    model.setFileUploadStatus("0");
                    model.setFileUploadTime(Utils.getTimestamp("yyyy-MM-dd HH:mm:ss"));
                    model.setHeaderTimes(Utils.NewTime(Utils.getTimestamp("yyyy-MM-dd"), "yyyy-MM-dd", "MMMM d, yyyy", new Boolean[]{true, true}));
                    model.setViewType(1);
                    file_list.add(0, model);
                    file_adapter.notifyDataSetChanged();
                   // Toast.makeText(getActivity(), "Uploading...", Toast.LENGTH_SHORT).show();
                } else {
                   // Toast.makeText(getActivity(), "local db Errors...", Toast.LENGTH_SHORT).show();
                }


                Intent intent = new Intent(getActivity(), UploadService.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("file_id", Data.get(Utils.File_Id));
                intent.putExtra("file_name", Data.get(Utils.File_Name));
                intent.putExtra("file_path", Data.get(Utils.File_Path));
                intent.putExtra("file_type", Data.get(Utils.File_Type));
                intent.putExtra("file_size", Data.get(Utils.File_Size));
                intent.putExtra("file_upload_status", "0");
                intent.putExtra("file_upload_time", Utils.getTimestamp("yyyy-MM-dd HH:mm:ss"));
                intent.putExtra("time_stamp", Utils.getTimestamp("yyyy-MM-dd"));
                intent.setAction(Utils.UploadFileIntent);
                getActivity().startService(intent);

            }

        }, getActivity());
        file_coper.execute(filepath);

    }


    private void Drives() {
        file_list.clear();
        JSONArray res = databaseHelper.getRow(FileStorage.TABLE_NAME);

        if (res.length() > 0) {
            for (int i = 0; i < res.length(); i++) {
                try {
                    JSONObject row = res.getJSONObject(i);
                    File_model model = new File_model();
                    model.setFileid(row.getString(FileStorage.File_ID));
                    model.setTimeStamp(row.getString(FileStorage.TimeStamp));
                    model.setFileName(row.getString(FileStorage.File_Name));
                    model.setFileType(row.getString(FileStorage.File_Type));
                    model.setFilePath(row.getString(FileStorage.File_Path));
                    model.setFileUploadTime(row.getString(FileStorage.File_Upload_Time));
                    model.setFileUploadStatus(row.getString(FileStorage.File_Upload_Status));
                    model.setHeaderTimes(Utils.NewTime(row.getString(FileStorage.TimeStamp), "yyyy-MM-dd", "MMMM d, yyyy", new Boolean[]{true, true}));
                    model.setViewType(1);


                    File root = android.os.Environment.getExternalStorageDirectory();
                    File file = new File(root.getAbsolutePath() + "/Vlocker/Download/"+row.getString(FileStorage.File_Name));

                    Log.w("key", "aaaaaa" + file);

                   // Toast.makeText(getActivity(), row.getString(FileStorage.File_Path), Toast.LENGTH_SHORT).show();
                    file_list.add(0, model);

                    if (!file.exists() && row.getString(FileStorage.File_Upload_Status).equals("3")) {
                        //Toast.makeText(getActivity(), row.getString(FileStorage.File_Name)+"downloaded", Toast.LENGTH_SHORT).show();
                        model.setFileUploadStatus("1");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            file_adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateProgreshReceiver, new IntentFilter("file_upload_progress"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(downloadProgreshReceiver, new IntentFilter("file_download_progress"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateStatusReceiver, new IntentFilter("file_upload_status"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(downloadStatusReceiver, new IntentFilter("file_download_status"));

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateProgreshReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(downloadProgreshReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateStatusReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(downloadStatusReceiver);
        Drives();
    }

    @Override
    public void onStart() {
        super.onStart();
        Drives();
    }


    private BroadcastReceiver updateProgreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (intent.getAction().equals("file_upload_progress")) {
                // Log.w("key", "--------------->" + intent.getIntExtra("percentage", 0));
                if (file_list != null && file_list.size() > 0) {
                    int pop = getIdPos(intent.getStringExtra("id"));
                    Log.w("key", "--------------->" + pop);
                    if (pop != -1) {
                        file_list.get((int) pop).setPercentage(intent.getIntExtra("percentage", 0));
                        file_adapter.notifyItemChanged((int) pop);
                    }
                }
            }
        }
    };

    private BroadcastReceiver downloadProgreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (intent.getAction().equals("file_download_progress")) {
                // Log.w("key", "--------------->" + intent.getIntExtra("percentage", 0));
                if (file_list != null && file_list.size() > 0) {
                    int pop = getIdPos(intent.getStringExtra("id"));
                    Log.w("key", "--------------->" + pop);
                    if (pop != -1) {
                        file_list.get((int) pop).setFileUploadStatus("2");
                        file_list.get((int) pop).setPercentage(intent.getIntExtra("percentage", 0));
                        file_adapter.notifyItemChanged((int) pop);
                    }
                }
            }
        }
    };


    private BroadcastReceiver updateStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (intent.getAction().equals("file_upload_status")) {
                if (file_list != null && file_list.size() > 0) {
                    int pop = getIdPos(intent.getStringExtra("id"));
                    if (pop != -1) {
                        file_list.get((int) pop).setFileUploadStatus("1");
                        file_list.get((int) pop).setFilePath(intent.getStringExtra("path"));
                        file_adapter.notifyItemChanged((int) pop);
                    }
                }
            }
        }
    };

    private BroadcastReceiver downloadStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (intent.getAction().equals("file_download_status")) {
                if (file_list != null && file_list.size() > 0) {
                    int pop = getIdPos(intent.getStringExtra("id"));
                    if (pop != -1) {
                        file_list.get((int) pop).setFileUploadStatus("3");
                        //  file_list.get((int) pop).setFilePath(intent.getStringExtra("path"));
                        file_adapter.notifyItemChanged((int) pop);
                    }
                }
            }
        }
    };

    private int getIdPos(String id) {
        for (int i = 0; i < file_list.size(); ++i) {
            if (file_list.get(i).getFileid().equals(id)) return i;
        }
        return -1;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<File_model> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (File_model s : file_list) {
            //if the existing elements contains the search input
            if (s.getFileName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        file_adapter.filterList(filterdNames);
    }


    private void fatch_file() {

        AndroidNetworking.post(All_URL_End_Point.Fatch_files)
                .addBodyParameter("user_id", user_id)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response get by server

                        // do anything with response
                        Log.w("------", "hello+--" + response);
                        try {

                            //  status chacking by the condition if it is true then you go for the home page or not valid then it goes else condition

                            JSONArray message = new JSONArray(response.getString("data"));
                            mSwipeRefreshLayout.setRefreshing(false);
                            for (int i = 0; i < message.length(); i++) {
                                JSONObject row = message.getJSONObject(i);
                                Log.w("------", "hello+--" + row.getString("filename"));


                                JSONArray jsonArray = databaseHelper.getResult(FileStorage.TABLE_NAME, new String[]{FileStorage.File_ID}, new String[]{row.getString("fileid")});

                                if (jsonArray.length() > 0) {
                                    Log.w("key", "----------------------------------------------yes");
                                    //updateStatush(response.getString("file_id"),response.getString("data"));
                                } else {
                                    HashMap<String, String> data = new HashMap<String, String>();
                                    data.put(FileStorage.File_ID, row.getString("fileid"));
                                    data.put(FileStorage.File_Name, row.getString("filename"));
                                    data.put(FileStorage.File_Path, row.getString("filepath"));
                                    data.put(FileStorage.File_Type, row.getString("filetype"));
                                    data.put(FileStorage.File_Size, row.getString("filesize"));
                                    data.put(FileStorage.File_Upload_Status, row.getString("fileuploadstatus"));
                                    data.put(FileStorage.File_Upload_Time, row.getString("fileuploadtime"));
                                    data.put(FileStorage.TimeStamp, row.getString("timestamp"));

                                    //insert all message in data base
                                    int isInserted = databaseHelper.Insert(FileStorage.TABLE_NAME, data);
                                }

                            }
                            Drives();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.w("------", "hello" + error);
                        progressDialog.dismiss();
                        // handle error
                    }
                });
    }


    //Permission function starts from here
    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(getActivity(), new String[]
                {
                        READ_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE,
                }, RequestPermissionCode);

    }

    // Calling override method.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordAudioPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (CameraPermission && RecordAudioPermission) {

                        Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getActivity(),"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    // Checking permission is enabled or not using function starts from here.
    public boolean CheckingPermissionIsEnabledOrNot() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED ;
    }


    public void openFile(String url) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "image/*");
        context.startActivity(intent);

     /*   try {

            Uri uri = Uri.fromFile(url);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav");
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") ||
                    url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                intent.setDataAndType(uri, "*//*");
            }

          /*  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }*/
    }





}
