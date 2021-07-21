package com.android.gallery.models.sort;



import com.android.gallery.models.Document;

import java.util.Comparator;





public class NameComparator implements Comparator<Document> {

    protected NameComparator() { }

    @Override
    public int compare(Document o1, Document o2) {
        return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
    }
}
