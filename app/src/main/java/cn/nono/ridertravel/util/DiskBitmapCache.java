package cn.nono.ridertravel.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.volley.toolbox.*;

import java.io.File;

/**
 * Created by Administrator on 2016/4/14.
 */
public class DiskBitmapCache extends DiskBasedCache implements com.android.volley.toolbox.ImageLoader.ImageCache
{

    public DiskBitmapCache(File rootDirectory, int maxCacheSizeInBytes)
    {
        super(rootDirectory, maxCacheSizeInBytes);
    }

    public DiskBitmapCache(File cacheDir)
    {
        super(cacheDir);
    }

    public Bitmap getBitmap(String url)
    {
        final Entry requestedItem = get(url);

        if (requestedItem == null) return null;

        return BitmapFactory.decodeByteArray(requestedItem.data, 0, requestedItem.data.length);
    }

    public void putBitmap(String url, Bitmap bitmap)
    {
        final Entry entry = new Entry();
        entry.data = BitmapUtil.convertBitmapToBytes(bitmap);
        put(url, entry);
    }
}
