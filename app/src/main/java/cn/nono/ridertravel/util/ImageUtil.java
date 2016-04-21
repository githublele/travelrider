package cn.nono.ridertravel.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/4/11.
 */
public class ImageUtil {

    private static final int IAMGE_WITH_MAX_SIZE =  500;

    public static Bitmap autoCompressImage(String path) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        //不把图片加载在内存，但是图片的其他字段（其他信息依然能读取）
        options.inJustDecodeBounds = true;
        //加载文件.
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = caculateInSampleSize(options);

        Log.i("xx",options.inSampleSize+"");

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);

    }

    public static byte[] autoCompressImage2byte(String path) {
        Bitmap bitmap = autoCompressImage(path);
        if (null == bitmap)
            return null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(null != baos)
            {
                try {
                    baos.close();
                    baos = null;
                } catch (IOException e) {
                    e.printStackTrace();
                    baos = null;
                }
            }
        }
    }

    private static int caculateInSampleSize(BitmapFactory.Options options) {
        int sampleSize = 1;
        int imageMaxEdgeLength = Math.max(options.outHeight,options.outWidth);
        if(imageMaxEdgeLength <= ImageUtil.IAMGE_WITH_MAX_SIZE)
            return sampleSize;

        sampleSize = Math.round(imageMaxEdgeLength*1.0f/IAMGE_WITH_MAX_SIZE);
        return sampleSize;
    }


}
