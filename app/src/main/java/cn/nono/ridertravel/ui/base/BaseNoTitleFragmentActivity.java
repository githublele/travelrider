package cn.nono.ridertravel.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

import cn.nono.ridertravel.ui.LoginActivity;

public class BaseNoTitleFragmentActivity extends FragmentActivity {
	private static final int LOGIN_REQ_CODE = 99;
	public void backFun(View v) {
		finish();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	protected void login() {
		Intent intent = new Intent(BaseNoTitleFragmentActivity.this, LoginActivity.class);
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
