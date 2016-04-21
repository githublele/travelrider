package cn.nono.ridertravel.util;


public class RegularMatchingUtil {
	
	private static String EMAIL_REGULAR = "^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$";
	
	public static boolean isEmail(String email) {
		/*
		if(MStringUtil.isNullEmptyTrim(email)) {
			return false;
		}
		return  email.matches(EMAIL_REGULAR);
	*/
		return true;
	}

}
