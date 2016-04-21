package cn.nono.ridertravel;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;

import cn.nono.ridertravel.bean.av.AVMUser;
import cn.nono.ridertravel.bean.av.AVTravelDiary;
import cn.nono.ridertravel.bean.av.AVTravelDiaryContent;
import cn.nono.ridertravel.util.DiskBitmapCache;

public class RiderTravelApplication extends Application {

	private DiskBitmapCache mDiskBitmapCache;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		AVObject.registerSubclass(AVMUser.class);
		AVObject.registerSubclass(AVTravelDiaryContent.class);
		AVObject.registerSubclass(AVTravelDiary.class);
		AVOSCloud.isDebugLogEnabled();
		AVOSCloud.initialize(this, "znwHiaY89nTixY6q6qHucyvl-gzGzoHsz", "moGC4z10Dwbz8CjzTbgT150l");

		mDiskBitmapCache = new DiskBitmapCache(getCacheDir(),10 * 1024 * 1024);
	}

	public DiskBitmapCache getDiskBitmapCache() {
		return mDiskBitmapCache;
	}

}
