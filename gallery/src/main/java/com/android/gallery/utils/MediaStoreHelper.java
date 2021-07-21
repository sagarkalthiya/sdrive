package com.android.gallery.utils;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


import com.android.gallery.FilePickerConst;
import com.android.gallery.cursors.DocScannerTask;
import com.android.gallery.cursors.loadercallbacks.FileMapResultCallback;
import com.android.gallery.cursors.loadercallbacks.FileResultCallback;
import com.android.gallery.cursors.loadercallbacks.PhotoDirLoaderCallbacks;
import com.android.gallery.models.Document;
import com.android.gallery.models.FileType;
import com.android.gallery.models.PhotoDirectory;

import java.util.Comparator;
import java.util.List;



public class MediaStoreHelper {

  public static void getPhotoDirs(FragmentActivity activity, Bundle args, FileResultCallback<PhotoDirectory> resultCallback) {
    if(activity.getSupportLoaderManager().getLoader(FilePickerConst.MEDIA_TYPE_IMAGE)!=null)
      activity.getSupportLoaderManager().restartLoader(FilePickerConst.MEDIA_TYPE_IMAGE, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
    else
      activity.getSupportLoaderManager().initLoader(FilePickerConst.MEDIA_TYPE_IMAGE, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
  }

  public static void getVideoDirs(FragmentActivity activity, Bundle args, FileResultCallback<PhotoDirectory> resultCallback) {
    if(activity.getSupportLoaderManager().getLoader(FilePickerConst.MEDIA_TYPE_VIDEO)!=null)
      activity.getSupportLoaderManager().restartLoader(FilePickerConst.MEDIA_TYPE_VIDEO, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
    else
      activity.getSupportLoaderManager().initLoader(FilePickerConst.MEDIA_TYPE_VIDEO, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
  }

  public static void getDocs(FragmentActivity activity,
                             List<FileType> fileTypes,
                             Comparator<Document> comparator,
                             FileMapResultCallback fileResultCallback)
  {
    new DocScannerTask(activity, fileTypes, comparator, fileResultCallback).execute();
  }
}