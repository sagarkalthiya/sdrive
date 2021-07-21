package com.android.gallery.cursors.loadercallbacks;



import com.android.gallery.models.Document;
import com.android.gallery.models.FileType;

import java.util.List;
import java.util.Map;


public interface FileMapResultCallback {
    void onResultCallback(Map<FileType, List<Document>> files);
}

