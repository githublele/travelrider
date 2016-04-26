package cn.nono.ridertravel.util;

import android.widget.EditText;

public class DataSimpleGetUtil {
	/**
	 *
	 * @param editText
	 * @return 无效或者没有字符 返回Null.其余情况返回相应的str
	 */
	@Deprecated
	public static String getEditTextData(EditText editText) {
		if(null == editText)
			return null;
		if(editText.length() <= 0)
			return null;
		String data = editText.getText().toString();
		if(data.isEmpty())
			return null;
		return data;
	}

	/**
	 *
	 * @param editText
	 * @return 无效或者没有字符 返回空字符串("").其余情况返回相应的str
     */
	public static String getEditTextStr(EditText editText) {
		if(null == editText)
			return "";
		if(editText.length() <= 0)
			return "";
		String data = editText.getText().toString();
		if(data.isEmpty())
			return "";
		return data;
	}

	public static String getEditTextData(EditText editText,String defaultStr) {
		String str = getEditTextData(editText);
		if(null == str)
			return defaultStr;
		return str;
	}


}
