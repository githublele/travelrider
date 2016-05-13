package cn.nono.ridertravel.util;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import cn.nono.ridertravel.R;

/**
 * Created by Administrator on 2016/5/7.
 */
public class ImageLoaderOptionsSetting {

    public final static DisplayImageOptions mDisplayOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.picture_default) //设置图片在下载期间显示的图片
    .showImageForEmptyUri(R.drawable.picture_default_damaged)//设置图片Uri为空或是错误的时候显示的图片
    .showImageOnFail(R.drawable.picture_default_damaged)  //设置图片加载/解码过程中错误时候显示的图片
    .cacheInMemory(true)//设置下载的图片是否缓存在内存中
    .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
    .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
    .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
    .build();//构建完成

    /**
     * 获取app 默认的 DisplayImageOptions（返回对象为常量 不可修改）
     * @return
     */
    public static DisplayImageOptions getConstantImageLoaderDefaultOptions() {
        return mDisplayOptions;
    }


}
