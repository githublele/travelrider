package cn.nono.ridertravel;

import android.app.Application;
import android.graphics.Bitmap;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;

import cn.nono.ridertravel.bean.av.AVBaseUserInfo;
import cn.nono.ridertravel.bean.av.AVComment;
import cn.nono.ridertravel.bean.av.AVMUser;
import cn.nono.ridertravel.bean.av.AVTravelActivity;
import cn.nono.ridertravel.bean.av.AVTravelDiary;
import cn.nono.ridertravel.bean.av.AVTravelDiaryContent;
import cn.nono.ridertravel.bean.av.AVTravelMapPath;

public class RiderTravelApplication extends Application {

	private AVBaseUserInfo mBaseUserInfo;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		AVUser.alwaysUseSubUserClass(AVMUser.class);
		AVObject.registerSubclass(AVTravelDiaryContent.class);
		AVObject.registerSubclass(AVTravelDiary.class);
		AVObject.registerSubclass(AVComment.class);
		AVObject.registerSubclass(AVTravelMapPath.class);
		AVObject.registerSubclass(AVTravelActivity.class);
		AVObject.registerSubclass(AVBaseUserInfo.class);


		AVOSCloud.isDebugLogEnabled();
		AVOSCloud.initialize(this, "znwHiaY89nTixY6q6qHucyvl-gzGzoHsz", "moGC4z10Dwbz8CjzTbgT150l");

		SDKInitializer.initialize(getApplicationContext());

		initImageLoader();

	}

	private void initImageLoader() {

		File cacheDir = this.getExternalCacheDir();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
				.memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
				.discCacheExtraOptions(480, 800, Bitmap.CompressFormat.JPEG, 75, null) // Can slow ImageLoader, use it carefully (Better don't use it)/设置缓存的详细信息，最好不要设置这个
				.threadPoolSize(3)//线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
				.memoryCacheSize(2 * 1024 * 1024)
				.discCacheSize(50 * 1024 * 1024)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCacheFileCount(100) //缓存的文件数量
				.discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
				.writeDebugLogs() // Remove for release app
				.build();//开始构建
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);

	}

	public AVBaseUserInfo getUserBaseInfo() {
		return mBaseUserInfo;
	}

	public void updateUserBaseInfo(AVBaseUserInfo userInfo) {
		this.mBaseUserInfo = userInfo;
	}


}
