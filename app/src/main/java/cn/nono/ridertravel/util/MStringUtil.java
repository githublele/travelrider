package cn.nono.ridertravel.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MStringUtil {

	private static final String REG_EXP_PHONE ="^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
	private static boolean check(String regExp,String str) {
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(str);
		return m.find();
	}

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

	public static boolean isMobilePhone(String phone) {
		if(isNullEmptyTrim(phone))
			return false;
		return check(REG_EXP_PHONE,phone);
	}




}
