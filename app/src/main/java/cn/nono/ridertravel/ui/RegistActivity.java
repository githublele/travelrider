package cn.nono.ridertravel.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;
import cn.nono.ridertravel.util.MStringUtil;
import cn.nono.ridertravel.util.RegularMatchingUtil;

public class RegistActivity extends BaseNoTitleActivity implements OnClickListener{

	private Button registButton = null;
	private EditText nickNameEditText = null;
	private EditText accountEditText = null;
	private EditText passwordEditText = null;
	private RadioGroup sexRadioGroup = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);

		registButton = (Button) findViewById(R.id.regist_bt);
		registButton.setOnClickListener(this);
		nickNameEditText = (EditText) findViewById(R.id.nickname_et);
		accountEditText = (EditText) findViewById(R.id.account_et);
		passwordEditText = (EditText) findViewById(R.id.password_et);
		sexRadioGroup = (RadioGroup) findViewById(R.id.sex_rg);


	}


	private AVUser avUser = null;



	ProgressDialog progressDlg = null;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		avUser = new AVUser();
		String nickName = nickNameEditText.getText().toString();
		String account  = accountEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		String sex = null;
		if(R.id.man_rb == sexRadioGroup.getCheckedRadioButtonId()) {
			sex = "男";
		} else {
			sex = "女";
		}
		avUser.put("sex", sex);

		if(MStringUtil.isNullEmptyTrim(nickName)) {
			Toast.makeText(this, "昵称不能为空",Toast.LENGTH_SHORT).show();
			avUser = null;
			return;
		}
		avUser.put("nickName", nickName);

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
		avUser.setEmail(account);

		if(MStringUtil.isNullEmptyTrim(password)) {
			Toast.makeText(this, "密码不能为空",Toast.LENGTH_SHORT).show();
			return;
		}
		avUser.setPassword(password);
		if(null == progressDlg)
			progressDlg = new ProgressDialog(this);
		progressDlg.setMessage("注册。。。");
		progressDlg.show();
		avUser.signUpInBackground(new SignUpCallback() {

			@Override
			public void done(AVException arg0) {
				// TODO Auto-generated method stub
				progressDlg.dismiss();
				if(null != arg0) {
					ToastUtil.toastShort(RegistActivity.this,arg0.getMessage());
				} else {
					ToastUtil.toastShort(RegistActivity.this, "注册成功");
				}
			}
		});

	}

}