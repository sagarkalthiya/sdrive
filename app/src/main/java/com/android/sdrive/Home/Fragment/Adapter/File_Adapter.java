package com.android.sdrive.Home.Fragment.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Filter;
import android.widget.Filterable;

import com.android.sdrive.All_URL_End_Point;
import com.android.sdrive.Component.Elements.ProgressButton;
import com.android.sdrive.Component.SessionManager;
import com.android.sdrive.Component.Utils;
import com.android.sdrive.DataBase.DatabaseHelper;
import com.android.sdrive.DataBase.Tables.FileStorage;
import com.android.sdrive.DownloadService;
import com.android.sdrive.Home.About_us.Aboutus_Activity;
import com.android.sdrive.Home.Fragment.HomeFragment;
import com.android.sdrive.Login_Pages.Login_Activity;
import com.android.sdrive.R;
import com.android.sdrive.SaveService;
import com.android.sdrive.UploadService;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class File_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context = null;
    private int lastPosition = -1;
    public ArrayList<File_model> File_list = null;
    private ArrayList<File_model> mArrayList;
    private ArrayList<File_model> mFilteredList;
    String user_id;
    SessionManager session;
    ProgressDialog progressDialog;
    DatabaseHelper databaseHelper;
    HomeFragment homeFragment;

    public File_Adapter(Context context, ArrayList<File_model> File_list, HomeFragment homeFragment) {
        this.context = context;
        this.File_list = File_list;
        this.session = new SessionManager(context);
        this.progressDialog = new ProgressDialog(context);
        this.databaseHelper = new DatabaseHelper(context);
        this.homeFragment = homeFragment;
        //progressDialog = new ProgressDialog(getActivity());

    }

    @Override
    public int getItemViewType(int position) {

        if (File_list.get(position).getViewType() == Utils.DATE_View) {

            return Utils.DATE_View;

        } else if (File_list.get(position).getViewType() == Utils.File_View) {

            return Utils.File_View;

        } else {

            return 404;
        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        if (viewType == Utils.DATE_View) {

            View view = LayoutInflater.from(context).inflate(R.layout.rv_item_date, parent, false);

            Center_Bubble_Date center_bubble_date = new Center_Bubble_Date(view);

            center_bubble_date.itemView.setTag(String.valueOf(Utils.DATE_View));

            return center_bubble_date;

        } else if (viewType == Utils.File_View) {
            View view = LayoutInflater.from(context).inflate(R.layout.rv_item_file, parent, false);

            Right_Bubble_Text right_bubble_text = new Right_Bubble_Text(view);

            right_bubble_text.itemView.setTag(String.valueOf(Utils.File_View));

            return right_bubble_text;

        }
        return null;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final File_model model = File_list.get(position);

        if (holder instanceof Center_Bubble_Date) {                                                 // date view

            Center_Bubble_Date center_bubble_date = (Center_Bubble_Date) holder;
            center_bubble_date.CenterTime.setText(model.getFileUploadTime());

        } else if (holder instanceof Right_Bubble_Text) {
            final Right_Bubble_Text right_bubble_text = (Right_Bubble_Text) holder;
            HashMap<String, String> user = session.getUserDetails();
            user_id = user.get(SessionManager.KEY_USER_ID);

            progressDialog.setMessage("Loading..."); // Setting Message
            progressDialog.setTitle("please wait..."); // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog.setCancelable(false);


            if (Arrays.asList(Utils.ZipType).contains("." + model.getFileType().toLowerCase())) {
                right_bubble_text.upload_file_icon.setImageResource(R.drawable.icon_file_zips);
            } else if (Arrays.asList(Utils.PdfType).contains("." + model.getFileType().toLowerCase())) {
                right_bubble_text.upload_file_icon.setImageResource(R.drawable.icon_file_pdfs);
            } else if (Arrays.asList(Utils.DocType).contains("." + model.getFileType().toLowerCase())) {
                right_bubble_text.upload_file_icon.setImageResource(R.drawable.icon_file_docs);
            } else if (Arrays.asList(Utils.Audio).contains("." + model.getFileType().toLowerCase())) {
                right_bubble_text.upload_file_icon.setImageResource(R.drawable.icon_file_mp3);
            } else if (Arrays.asList(Utils.Video).contains("." + model.getFileType().toLowerCase())) {
                right_bubble_text.upload_file_icon.setImageResource(R.drawable.icon_file_video);
            } else if (Arrays.asList(Utils.Image).contains("." + model.getFileType().toLowerCase())) {
                right_bubble_text.upload_file_icon.setImageResource(R.drawable.icon_file_image);
            } else {
                right_bubble_text.upload_file_icon.setImageResource(R.drawable.icon_file_txts);
            }


            right_bubble_text.upload_CenterTime.setText(model.getHeaderTimes());
            right_bubble_text.upload_CenterTime.setVisibility(View.VISIBLE);
            if (position > 0) {
                if (File_list.get(position - 1).getTimeStamp().equals(model.getTimeStamp())) {
                    right_bubble_text.upload_CenterTime.setVisibility(View.GONE);
                } else {
                    right_bubble_text.upload_CenterTime.setVisibility(View.VISIBLE);
                }
            }


            right_bubble_text.upload_file_name.setText(model.getFileName());

            String[] status = new String[]{"Uploading", "Uploaded", "Downloading", "Downloded"};

            right_bubble_text.upload_file_status.setText(model.getFileType() + " • " + status[Integer.valueOf(model.getFileUploadStatus())] + " • " + Utils.formattedDateFromString("yyyy-MM-dd HH:mm:ss", "h:mm a", model.getFileUploadTime()));

            right_bubble_text.item_drop_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(context, right_bubble_text.item_drop_menu, Gravity.CENTER_HORIZONTAL);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.options_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.download:
                                    //handle menu1 click
                                    File root = android.os.Environment.getExternalStorageDirectory();
                                    File fileee = new File(root.getAbsolutePath() + "/Vlocker/Download/" + model.getFileName());
                                   // String file_path = file.toString();
                                    //  File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/brouchure.pdf");
                                    if (model.getFileUploadStatus().equals("1")) {
                                        Intent intent = new Intent(context, SaveService.class);
                                        intent.putExtra("file_id", model.getFileid());
                                        intent.putExtra("file_name", model.getFileName());
                                        intent.putExtra("file_path", model.getFilePath());
                                        intent.setAction(Utils.UploadFileIntent);
                                        context.startService(intent);

                                    } else if (model.getFileUploadStatus().equals("3")) {

                                      //String file_path=  root.getAbsolutePath() + "/Vlocker/Download/" + model.getFileName();
                                        if (fileee.toString().contains(".pdf")||fileee.toString().contains(".zip") || fileee.toString().contains(".rar")){
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_VIEW);
                                            intent.setDataAndType(Uri.parse(root.getAbsolutePath() + "/Vlocker/Download/" + model.getFileName()), "application/*");
                                            context.startActivity(intent);

                                        }else if (fileee.toString().contains(".3gp") || fileee.toString().contains(".mpg") ||
                                                fileee.toString().contains(".mpeg")  || fileee.toString().contains(".mp4") || fileee.toString().contains(".avi")){
                                            Toast.makeText(context, fileee.toString(), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_VIEW);
                                            intent.setDataAndType(Uri.parse(root.getAbsolutePath() + "/Vlocker/Download/" + model.getFileName()), "video/*");
                                            context.startActivity(intent);
                                        }else if (fileee.toString().contains(".wav") || fileee.toString().contains(".mp3")){
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_VIEW);
                                            intent.setDataAndType(Uri.parse(root.getAbsolutePath() + "/Vlocker/Download/" + model.getFileName()), "audio/*");
                                            context.startActivity(intent);
                                        }else if (fileee.toString().contains(".txt")) {
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_VIEW);
                                            intent.setDataAndType(Uri.parse(root.getAbsolutePath() + "/Vlocker/Download/" + model.getFileName()), "text/*");
                                            context.startActivity(intent);
                                        }else {
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_VIEW);
                                            intent.setDataAndType(Uri.parse(root.getAbsolutePath() + "/Vlocker/Download/" + model.getFileName()), "image/*");
                                            context.startActivity(intent);
                                        }

                                        ////open file
                                       // openFile(fileee);
                                      //  homeFragment.openFile(file_path);


                                    }


                                    break;
                                case R.id.delete:
                                    progressDialog.show();
                                    AndroidNetworking.post(All_URL_End_Point.DELETE_FILE)
                                            .addBodyParameter("user_id", user_id)
                                            .addBodyParameter("file_id", model.getFileid())
                                            .setTag("test")
                                            .setPriority(Priority.MEDIUM)
                                            .build()
                                            .getAsJSONObject(new JSONObjectRequestListener() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    // response get by server
                                                    progressDialog.dismiss();
                                                    // do anything with response
                                                    Log.w("------", "*********sagar" + response.toString());

                                                    try {
                                                        if (response.getBoolean("status")) {
                                                            if (databaseHelper.delete(FileStorage.TABLE_NAME, new String[]{FileStorage.File_ID}, new String[]{model.getFileid()})) {
                                                                Remove(position);
                                                            }


                                                        }
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
                                    break;
                            }
                            return false;
                        }
                    });

                    File root = android.os.Environment.getExternalStorageDirectory();
                    File file = new File(root.getAbsolutePath() + "/Vlocker/Download/" + model.getFileName());

                    Log.w("key", "aaaaaa" + file);

                    // Toast.makeText(getActivity(), row.getString(FileStorage.File_Path), Toast.LENGTH_SHORT).show();
                    Menu menuOpts = popup.getMenu();
                    if (file.exists()) {
                        menuOpts.getItem(0).setTitle("Open");
                        // openFile(file);
                    } else {
                        menuOpts.getItem(0).setTitle("Download");

                    }

                    //displaying the popup
                    popup.show();
                }
            });


            right_bubble_text.fileUploadBtn.setDeterminate();

            right_bubble_text.fileUploadBtn.setMaxProgress(100);
            right_bubble_text.fileUploadBtn.setProgress(model.getPercentage());


            right_bubble_text.fileUploadBtn.addOnClickListener(new ProgressButton.OnClickListener() {
                @Override
                public void onIdleButtonClick(View view) {

                }

                @Override
                public void onCancelButtonClick(View view) {

                }

                @Override
                public void onFinishButtonClick(View view) {
                    //Toast.makeText(context, "Finish", Toast.LENGTH_SHORT).show();
                }
            });


            if (model.getFileUploadStatus().equals("0")) {
                right_bubble_text.item_drop_menu.setVisibility(View.GONE);
                right_bubble_text.fileUploadBtn.setVisibility(View.VISIBLE);
            } else if (model.getFileUploadStatus().equals("1")) {
                right_bubble_text.fileUploadBtn.setVisibility(View.GONE);
                right_bubble_text.item_drop_menu.setVisibility(View.VISIBLE);
            } else if (model.getFileUploadStatus().equals("2")) {
                right_bubble_text.item_drop_menu.setVisibility(View.GONE);
                right_bubble_text.fileUploadBtn.setVisibility(View.VISIBLE);
            } else if (model.getFileUploadStatus().equals("3")) {
                right_bubble_text.fileUploadBtn.setVisibility(View.GONE);
                right_bubble_text.item_drop_menu.setVisibility(View.VISIBLE);
            }

        } else if (holder instanceof Left_Bubble_Text) {                                            // Left Text view
            Left_Bubble_Text left_bubble_text = (Left_Bubble_Text) holder;

        }
    }

    @Override
    public int getItemCount() {
        return File_list.size();
    }

    public void filterList(ArrayList<File_model> filterdNames) {
        this.File_list = filterdNames;
        notifyDataSetChanged();
    }


    public class Center_Bubble_Date extends RecyclerView.ViewHolder {
        TextView CenterTime;

        public Center_Bubble_Date(View itemView) {
            super(itemView);
            CenterTime = (TextView) itemView.findViewById(R.id.CenterTime);
        }
    }

    public class Right_Bubble_Text extends RecyclerView.ViewHolder {

        TextView upload_file_name, upload_file_status, upload_CenterTime;
        ImageView upload_file_icon, item_drop_menu;
        ProgressButton fileUploadBtn;


        public Right_Bubble_Text(View itemView) {
            super(itemView);


            upload_CenterTime = (TextView) itemView.findViewById(R.id.upload_CenterTime);
            fileUploadBtn = (ProgressButton) itemView.findViewById(R.id.fileUploadBtn);
            upload_file_name = (TextView) itemView.findViewById(R.id.upload_file_name);
            upload_file_status = (TextView) itemView.findViewById(R.id.upload_file_status);
            upload_file_icon = (ImageView) itemView.findViewById(R.id.upload_file_icon);
            item_drop_menu = (ImageView) itemView.findViewById(R.id.item_drop_menu);


        }
    }

    public class Left_Bubble_Text extends RecyclerView.ViewHolder {
        TextView messageLeftTv;

        public Left_Bubble_Text(View itemView) {
            super(itemView);

            // messageLeftTv = (TextView) itemView.findViewById(R.id.messageLeftTv);


        }
    }

    private void Remove(int removeIndex) {
        File_list.remove(removeIndex);
        notifyItemRemoved(removeIndex);
    }


}
