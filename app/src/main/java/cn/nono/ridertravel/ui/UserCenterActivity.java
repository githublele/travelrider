package cn.nono.ridertravel.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.RiderTravelApplication;
import cn.nono.ridertravel.bean.av.AVBaseUserInfo;
import cn.nono.ridertravel.bean.av.AVMUser;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;

public class UserCenterActivity extends BaseNoTitleActivity implements OnClickListener {

	TextView nicknameTextView = null;
	TextView accountTextView = null;
	TextView sexTextView = null;
	TextView birthTextView = null;
	Button   logoutButton = null;

	AVMUser user = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_center);
		 user = (AVMUser) AVUser.getCurrentUser();
		 if(null == user) {
			 finish();
			 return;
		 }

		init();
		loadData();

	}


	private void init() {
		nicknameTextView = (TextView) findViewById(R.id.nickname_tv);
		accountTextView = (TextView) findViewById(R.id.account_tv);
		sexTextView = (TextView) findViewById(R.id.sex_tv);
		birthTextView = (TextView) findViewById(R.id.birth_tv);
		logoutButton = (Button) findViewById(R.id.logout_btn);
		logoutButton.setOnClickListener(this);

	}

	private ProgressDialog progressDialog = null;
	private void loadData() {

		if(progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setCancelable(true);
			progressDialog.setMessage("从服务器获取信息");
		}

		progressDialog.show();

		AVQuery<AVBaseUserInfo> query = AVQuery.getQuery(AVBaseUserInfo.class);
		query.getInBackground(user.getBaseInfo().getObjectId(), new GetCallback<AVBaseUserInfo>() {
			@Override
			public void done(AVBaseUserInfo avBaseUserInfo, AVException e) {
				progressDialog.dismiss();
				if(null != e) {
					ToastUtil.toastLong(UserCenterActivity.this,"网络异常");
					e.printStackTrace();
					finish();
				} else {
					((RiderTravelApplication)getApplication()).updateUserBaseInfo(avBaseUserInfo);
					nicknameTextView.setText(avBaseUserInfo.getNickname());
					sexTextView.setText(avBaseUserInfo.getSex());
					accountTextView.setText(user.getUsername());
				}
			}
		});


	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		AVUser.logOut();
		((RiderTravelApplication)getApplication()).updateUserBaseInfo(null);
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

}
