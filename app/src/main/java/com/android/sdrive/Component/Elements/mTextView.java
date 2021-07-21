package com.android.sdrive.Component.Elements;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.android.sdrive.R;


public class mTextView extends AppCompatTextView {

    public mTextView(Context context) {
        super(context);
    }

    public mTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        applyCustomTypeface(context, attributeSet);
    }

    public mTextView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        applyCustomTypeface(context, attributeSet);
    }

    private void applyCustomTypeface(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CustomTypeface);
        String customFont = typedArray.getString(R.styleable.CustomTypeface_custom_typeface);
        applyCustomTypeface(context, customFont);
        typedArray.recycle();
    }

    public boolean applyCustomTypeface(Context context, String asset) {
        setTypeface(FontCache.getTypeface(context, asset));
        return true;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text.length() > 0) {
            text = String.valueOf(text.charAt(0)).toUpperCase() + text.subSequence(1, text.length());
        }
        super.setText(text, type);
    }
}
