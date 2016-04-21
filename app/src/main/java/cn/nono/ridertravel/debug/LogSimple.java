package cn.nono.ridertravel.debug;

import android.util.Log;

public class LogSimple {

	private static final String TAG_INFO_NORMAL = "i_normal";
	public static void i(String msg) {
		Log.i(TAG_INFO_NORMAL, msg);
	}
	
	public static void i(String tag,String msg) {
		Log.i(tag, msg);
	}
}
