package cn.nono.ridertravel.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;
import cn.nono.ridertravel.util.MStringUtil;
import cn.nono.ridertravel.util.RegularMatchingUtil;

public class LoginActivity extends BaseNoTitleActivity implements OnClickListener{

	private Button loginButton = null;
	private Button registButton = null;
	private EditText accountEditText = null;
	private EditText passwordEditText = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		accountEditText = (EditText) findViewById(R.id.account_et);
		passwordEditText = (EditText) findViewById(R.id.password_et);
		loginButton = (Button) findViewById(R.id.login_bt);
		loginButton.setOnClickListener(this);
		registButton = (Button) findViewById(R.id.regist_bt);
		registButton.setOnClickListener(registOnClickListener);
	}

	OnClickListener registOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
			startActivity(intent);
		}
	};

	ProgressDialog progressDlg = null;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		AVUser avUser = new AVUser();
		String account  = accountEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		if(MStringUtil.isNullEmptyTrim(account)) {
			Toast.makeText(this, "账号不能为空",Toast.LENGTH_SHORT).show();
			avUser = null;
			return;
		}

		//是否合法email 否 提示并返还
		if(!RegularMatchingUtil.isEmail(account)) {
			Toast.makeText(this, "无效的邮箱",Toast.LENGTH_SHORT).show();
			avUser = null;
			return;
		}

		avUser.setUsername(account);

		if(MStringUtil.isNullEmptyTrim(password)) {
			Toast.makeText(this, "密码不能为空",Toast.LENGTH_SHORT).show();
			return;
		}
		avUser.setPassword(password);
		if(null == progressDlg)
			progressDlg = new ProgressDialog(this);
		progressDlg.setMessage("登陆。。。");
		progressDlg.show();
		AVUser.logInInBackground(account, password, new LogInCallback<AVUser>() {

			@Override
			public void done(AVUser avUser, AVException arg0) {
				// TODO Auto-generated method stub
				progressDlg.dismiss();
				if(null != arg0) {
					ToastUtil.toastShort(LoginActivity.this,arg0.getMessage());
				} else {
					ToastUtil.toastShort(LoginActivity.this, "登陆成功");
					finish();
				}
			}
		});

	}
}