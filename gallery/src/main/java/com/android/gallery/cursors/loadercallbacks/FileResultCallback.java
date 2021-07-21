package com.android.gallery.cursors.loadercallbacks;

import java.util.List;

public interface FileResultCallback<T> {
    void onResultCallback(List<T> files);
}