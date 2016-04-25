package cn.nono.ridertravel;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.baidu.mapapi.SDKInitializer;

import cn.nono.ridertravel.bean.av.AVBaseUserInfo;
import cn.nono.ridertravel.bean.av.AVComment;
import cn.nono.ridertravel.bean.av.AVMUser;
import cn.nono.ridertravel.bean.av.AVTravelActivity;
import cn.nono.ridertravel.bean.av.AVTravelDiary;
import cn.nono.ridertravel.bean.av.AVTravelDiaryContent;
import cn.nono.ridertravel.bean.av.AVTravelMapPath;
import cn.nono.ridertravel.util.DiskBitmapCache;

public class RiderTravelApplication extends Application {

	private DiskBitmapCache mDiskBitmapCache;
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

		mDiskBitmapCache = new DiskBitmapCache(getCacheDir(),10 * 1024 * 1024);

		SDKInitializer.initialize(getApplicationContext());

	}

	public DiskBitmapCache getDiskBitmapCache() {
		return mDiskBitmapCache;
	}

	public AVBaseUserInfo getUserBaseInfo() {
		return mBaseUserInfo;
	}

	public void updateUserBaseInfo(AVBaseUserInfo userInfo) {
		this.mBaseUserInfo = userInfo;
	}


}
