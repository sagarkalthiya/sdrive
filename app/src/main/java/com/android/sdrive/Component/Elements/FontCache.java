package com.android.sdrive.Component.Elements;

import android.content.Context;
import android.graphics.Typeface;
import java.util.HashMap;

public class FontCache {

    private static HashMap<String, Typeface> fontCache = new HashMap<>();

    public static Typeface getTypeface(Context context, String customFont) {
        Typeface typeface = fontCache.get(customFont);
        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), customFont);
            } catch (Exception e) {
                return null;
            }
            fontCache.put(customFont, typeface);
        }
        return typeface;
    }
}