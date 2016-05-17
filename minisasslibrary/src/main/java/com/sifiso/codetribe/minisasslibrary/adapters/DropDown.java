package com.sifiso.codetribe.minisasslibrary.adapters;

import android.graphics.drawable.Drawable;

/**
 * Created by aubreymalabie on 4/26/16.
 */
public class DropDown {
    String label;
    int drawableResourceID;
    Drawable drawable;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getDrawableResourceID() {
        return drawableResourceID;
    }

    public void setDrawableResourceID(int drawableResourceID) {
        this.drawableResourceID = drawableResourceID;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
