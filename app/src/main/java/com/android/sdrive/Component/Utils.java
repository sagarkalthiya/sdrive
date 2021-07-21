package com.android.sdrive.Component;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.EditText;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Utils {

    public static String File_Id = "File_Id";
    public static String File_Name = "File_Name";
    public static String File_Type = "File_Type";
    public static String File_Path = "File_Path";
    public static String File_Size = "File_Size";
    public static String File_View_Type = "File_View_Type";

    public static String[] Video = {".mp4"};
    public static String[] Image = {".png", ".jpg"};
    public static String[] Audio = {".mp3"};

    public static String[] ZipType = {".zip", ".rar"};
    public static String[] PdfType = {".pdf"};
    public static String[] DocType = {".doc", ".docx", ".dot", ".dotx"};
    public static String[] ExcelType = {".xls", ".xlsx"};
    public static String[] PptType = {".ppt", ".pptx"};
    public static String[] TxtType = {".txt"};
    public static String[] ApkType = {".apk"};


    public static int DATE_View = 0;
    public static int File_View = 1;


    public static String UploadFileIntent = "UploadFileIntent";


    public static boolean isEmpty(EditText editText) {
        boolean isEmptyResult = false;
        String text = editText.getText().toString().trim();
        if (!text.isEmpty() && text.length() != 0 && !text.equals("") && text != null) {
            isEmptyResult = true;
        }
        return isEmptyResult;
    }


    public static JSONArray jsonArray(Cursor cursor) {
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        rowObject.put(cursor.getColumnName(i),
                                cursor.getString(i));
                    } catch (Exception e) {
                        Log.d("key", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        return resultSet;

    }

    public static String getTimestamp(String Format) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(Format);
        return dateFormat.format(new Date()); // Find todays date
    }




    public static String NewTime(String DateTime, String OldFormate, String NewFormate, Boolean[] Today) {


        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(OldFormate);
        Date date = null;
        try {
            date = dateFormat.parse(DateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTime(date);

        Calendar now = Calendar.getInstance();


        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return Today[0].equals(true) ? "Today" : (String) DateFormat.format(NewFormate, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return Today[1].equals(true) ? "Yesterday" : (String) DateFormat.format(NewFormate, smsTime);
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(NewFormate, smsTime).toString();
        } else {
            return DateFormat.format(NewFormate, smsTime).toString();
        }
    }


    public static String SpeakersTime(String DateTime,String OldFormate){

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(OldFormate);
        Date date = null;
        try {
            date = dateFormat.parse(DateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTime(date);
        Calendar now = Calendar.getInstance();

        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return (String) DateFormat.format("h:mm a", smsTime);
        }else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday";
        }else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format("dd MM yyyy", smsTime).toString();
        }else {
            return DateFormat.format("dd MM yyyy", smsTime).toString();
        }

    }

    public static String formattedDateFromString(String inputFormat, String outputFormat, String inputDate){
        if(inputFormat.equals("")){ // if inputFormat = "", set a default input format.
            inputFormat = "yyyy-MM-dd hh:mm:ss";
        }
        if(outputFormat.equals("")){
            outputFormat = "EEEE d 'de' MMMM 'del' yyyy"; // if inputFormat = "", set a default output format.
        }
        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        // You can set a different Locale, This example set a locale of Country Mexico.
        //SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, new Locale("es", "MX"));
        //SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, new Locale("es", "MX"));

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);
        } catch (Exception e) {
            Log.e("formattedDateFromString", "Exception in formateDateFromstring(): " + e.getMessage());
        }
        return outputDate;

    }




    public static boolean ifPermissionGranted(Context context, String accessFineLocation){
        boolean status = false;
        int hasPermission = ContextCompat.checkSelfPermission(context,accessFineLocation );
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            status = true;
        }
        return status;
    }


}
