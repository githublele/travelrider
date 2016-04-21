package cn.nono.ridertravel.util;

public class MStringUtil {
	
	public static boolean isNullEmpty(String str) {
		if(null == str || str.isEmpty()) {
			return true;
		}
		return false;
	}
	
	public static boolean isNullEmptyTrim(String str) {
		if(null == str || str.isEmpty() || str.trim().isEmpty()) {
			return true;
		}
		return false;
	}

}
