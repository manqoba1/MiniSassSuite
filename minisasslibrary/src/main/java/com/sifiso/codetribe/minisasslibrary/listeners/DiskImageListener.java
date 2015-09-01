package com.sifiso.codetribe.minisasslibrary.listeners;

import android.graphics.Bitmap;


public interface DiskImageListener {

	public void onBitmapReturned(Bitmap bitmap);
	public void onError();
}
