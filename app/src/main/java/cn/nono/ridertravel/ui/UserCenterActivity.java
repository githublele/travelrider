package cn.nono.ridertravel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;

public class UserCenterActivity extends BaseNoTitleActivity implements OnClickListener {

	TextView nicknameTextView = null;
	TextView accountTextView = null;
	TextView sexTextView = null;
	TextView birthTextView = null;
	Button   logoutButton = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_center);
		init();
	}

	private void init() {
		nicknameTextView = (TextView) findViewById(R.id.nickname_tv);
		accountTextView = (TextView) findViewById(R.id.account_tv);
		sexTextView = (TextView) findViewById(R.id.sex_tv);
		birthTextView = (TextView) findViewById(R.id.birth_tv);
		logoutButton = (Button) findViewById(R.id.logout_btn);
		logoutButton.setOnClickListener(this);

		AVUser user = AVUser.getCurrentUser();
		if(null != user) {
			nicknameTextView.setText("昵称:"+user.getString("nickName"));
			accountTextView.setText("用户:"+user.getUsername());
			sexTextView.setText(user.getString("sex"));
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
			Date birthDate = user.getDate("birth");
			if(null != birthDate) {
				birthTextView.setText("生日:"+dateFormat.format(birthDate));
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		AVUser.logOut();
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

}
