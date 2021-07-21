package com.android.sdrive.Component.Elements;


import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.webkit.MimeTypeMap;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;


public class File_coper extends AsyncTask<String, Void, HashMap<String, String>> {

    private Context context;


    public interface AsyncResponse {
        void processFinish(HashMap<String, String> imagePath);
    }

    private AsyncResponse delegate = null;

    public File_coper(AsyncResponse delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    protected HashMap<String, String> doInBackground(String... strings) {
        if (strings.length == 0 || strings[0] == null)
            return null;
        return FileMoving(strings[0]);
    }

    protected void onPostExecute(HashMap<String, String> imagePath) {
        // imagePath is path of new compressed image.
        delegate.processFinish(imagePath);
    }


    private HashMap<String,String> FileMoving(String filePath) {

        // Map<String, Object> outputFile = getFilename(filePath);


        HashMap<String,String> Files = getFilename(filePath);

        String File_Papth = Files.get("File_Path");


        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(filePath);
            out = new FileOutputStream(File_Papth);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Files;
    }






    private HashMap<String,String> getFilename(String in_file_path) {

       /* Uri file = Uri.fromFile(new File(in_file_path));
        String fileExt = MimeTypeMap.getFileExtensionFromUrl(file.toString());*/

        HashMap<String,String> hashMap = file_type(in_file_path);

        String Folder_name = hashMap.get("Folder_name");
        String File_Extensions = hashMap.get(TPSString.File_Type);
        String File_Start_Name = hashMap.get("File_Start_Name");


        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Vlocker/"
                + "Files/" + Folder_name
                + "/Send");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }

        String mFileName = File_Start_Name + String.valueOf(System.currentTimeMillis()) + "." + File_Extensions;
        hashMap.put(TPSString.File_Id,String.valueOf(System.currentTimeMillis()));
        hashMap.put(TPSString.File_Path, (mediaStorageDir.getAbsolutePath() + "/" + mFileName));


        return hashMap;
    }


    private HashMap<String,String> file_type(String path) {

        HashMap<String,String> hashMap = new HashMap<String, String>();

        Uri file = Uri.fromFile(new File(path));
        String fileExt = MimeTypeMap.getFileExtensionFromUrl(file.toString());

        hashMap.put(TPSString.File_Name, new File(path).getName());
        hashMap.put(TPSString.File_Type, fileExt.toLowerCase());
        hashMap.put(TPSString.File_Size, toNumInUnits(new File(path).length()));

        if (Arrays.asList(TPSString.Image).contains(fileExt.toLowerCase())) {   // image
            hashMap.put("Folder_name", "Android Image");
            hashMap.put("File_Start_Name", "IMG_");
            hashMap.put(TPSString.File_View_Type, "1");
            // hashMap.put("File_Duration", "");
        } else if (Arrays.asList(TPSString.Video).contains(fileExt.toLowerCase())) {    // video
            hashMap.put("Folder_name", "Android Video");
            hashMap.put("File_Start_Name", "VID_");
            hashMap.put(TPSString.File_View_Type, "1");
            hashMap.put("File_Duration", getDuration(new File(path)));
        } else if (Arrays.asList(TPSString.Audio).contains(fileExt.toLowerCase())) {    // audio
            hashMap.put("Folder_name", "Android Audio");
            hashMap.put("File_Start_Name", "AUD_");
            hashMap.put(TPSString.File_View_Type, "1");
            hashMap.put("File_Duration", getDuration(new File(path)));
        } else {                                                                     // DOC
            hashMap.put("Folder_name", "Android Documents");
            hashMap.put("File_Start_Name", "DOC_");
            hashMap.put(TPSString.File_View_Type, "1");
            //hashMap.put("File_Duration", "");
        }

        return hashMap;
    }


    @SuppressLint("DefaultLocale")
    private static String toNumInUnits(long bytes) {
        int u = 0;
        for (;bytes > 1024*1024; bytes >>= 10) {
            u++;
        }
        if (bytes > 1024)
            u++;
        return String.format("%.1f %cB", bytes/1024f, " kMGTPE".charAt(u));
    }


    private static String getDuration(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return formateMilliSeccond(Long.parseLong(durationStr));
    }


    public static String formateMilliSeccond(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }

}