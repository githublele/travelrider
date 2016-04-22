package cn.nono.ridertravel.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import cn.nono.ridertravel.ui.LoginActivity;

public class BaseNoTitleActivity extends Activity {
	private static final int LOGIN_REQ_CODE = 99999;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}


	protected void login() {
		Intent intent = new Intent(BaseNoTitleActivity.this, LoginActivity.class);
		startActivityForResult(intent,LOGIN_REQ_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(LOGIN_REQ_CODE == requestCode) {
			onLoginActivityResult(resultCode,data);
		}
	}

	/**
	 * 登录成功重写该方法
	 * @param resultCode
	 * @param data
     */
	protected void onLoginActivityResult(int resultCode, Intent data) {

	}


}
