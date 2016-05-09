package cn.nono.ridertravel.ui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import cn.nono.ridertravel.RiderTravelApplication;
import cn.nono.ridertravel.bean.av.AVBaseUserInfo;
import cn.nono.ridertravel.ui.LoginActivity;

public class BaseNoTitleActivity extends Activity {
	private static final int LOGIN_REQ_CODE = 99;

	private ProgressDialog mProgDlg = null;
	protected ProgressDialog progressDialogCreate() {
		ProgressDialog dlg = new ProgressDialog(this);
		dlg.setMessage("数据加载中");
		dlg.setCancelable(false);
		return dlg;
	}

	public void backFun(View v) {
		finish();
	}

	protected void showProgressDialg() {
		showProgressDialg(null);
	}

	protected void showProgressDialg(String dlgMsg) {
		if(null == mProgDlg) {
			mProgDlg = progressDialogCreate();
		}
		if(mProgDlg.isShowing())
			return;
		if(null == dlgMsg)
			mProgDlg.setMessage("数据加载中");
		else
			mProgDlg.setMessage(dlgMsg);

		mProgDlg.show();
	}

	protected void hideProgressDialg() {
		if(null == mProgDlg)
			return;
		if(mProgDlg.isShowing())
			mProgDlg.dismiss();
	}

	protected AVBaseUserInfo getAplicationBaseUserInfoCache() {
		return ((RiderTravelApplication)getApplication()).getUserBaseInfo();
	}

	protected void updateAplicationBaseUserInfoCache(AVBaseUserInfo avBaseUserInfo) {
		((RiderTravelApplication)getApplication()).updateUserBaseInfo(avBaseUserInfo);
	}
	
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
