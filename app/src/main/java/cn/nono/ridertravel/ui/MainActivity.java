package cn.nono.ridertravel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.avos.avoscloud.AVUser;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.bean.av.AVMUser;
import cn.nono.ridertravel.ui.base.BaseNoTitleFragmentActivity;
import cn.nono.ridertravel.ui.travelact.TravelActivityFragment;
import cn.nono.ridertravel.ui.travelrecord.TravelRecordFragment;
import cn.nono.ridertravel.ui.usercenter.UserCenterFragment;

public class MainActivity extends BaseNoTitleFragmentActivity implements RadioGroup.OnCheckedChangeListener{


	private RadioGroup mNavigationRedioGroup;
	private RadioButton mDiaryRadioBtn;
	private RadioButton mActivityRadioBtn;
	private RadioButton mNotificationRadioBtn;
	private RadioButton mUserCenterBtn;



	private TravelDiaryFragment mDiaryFragemt = null;
	private TravelActivityFragment mActivityFragment = null;
	private UserCenterFragment mUserCenterFragment = null;

	private TravelRecordFragment mTravelRecordFragment = null;



	private Fragment mCurrentShowFragment = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationRedioGroup = (RadioGroup) findViewById(R.id.navigation_radioGroup);
		mNavigationRedioGroup.setOnCheckedChangeListener(this);
		mNavigationRedioGroup.check(R.id.diary_radioBtn);

		mDiaryRadioBtn = (RadioButton) findViewById(R.id.diary_radioBtn);
		mActivityRadioBtn = (RadioButton) findViewById(R.id.activity_radioBtn);
		mNotificationRadioBtn = (RadioButton) findViewById(R.id.notification_radioBtn);
		mUserCenterBtn = (RadioButton) findViewById(R.id.user_center_radioBtn);

	}

	//标志
	private int mLoginFlag = -1;

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		FragmentTransaction tc = getSupportFragmentManager().beginTransaction();
		if(null != mCurrentShowFragment)
			tc.hide(mCurrentShowFragment);

		if(R.id.diary_radioBtn == checkedId) {
			if(null == mDiaryFragemt) {
				mDiaryFragemt = new TravelDiaryFragment();
				tc.add(R.id.content, mDiaryFragemt);
			}
			mCurrentShowFragment = mDiaryFragemt;
		} else if (R.id.activity_radioBtn == checkedId) {
			if(null == mActivityFragment) {
				mActivityFragment = new TravelActivityFragment();
				tc.add(R.id.content, mActivityFragment);
			}
			mCurrentShowFragment = mActivityFragment;
		} else if (R.id.user_center_radioBtn == checkedId) {
			AVMUser avmUser = (AVMUser) AVUser.getCurrentUser();
			if(null == avmUser) {
				mLoginFlag = 2;
				login();
				//阻止本次的点击（回复点击前的显示状态）
				if(mCurrentShowFragment == mDiaryFragemt) {
					mDiaryRadioBtn.setChecked(true);
				} else if(mCurrentShowFragment == mActivityFragment) {
					mActivityRadioBtn.setChecked(true);
				} else {
					mNotificationRadioBtn.setChecked(true);
				}
				return;
			}
			if(null == mActivityFragment) {
				mUserCenterFragment = new UserCenterFragment();
				tc.add(R.id.content,mUserCenterFragment);
			}
			mCurrentShowFragment = mUserCenterFragment;
		}

		//R.id.notificatRadioBtn
		 else {
			AVMUser avmUser = (AVMUser) AVUser.getCurrentUser();
			if(null == avmUser) {
				mLoginFlag = 3;
				login();
				//阻止本次的点击（回复点击前的显示状态）
				if(mCurrentShowFragment == mDiaryFragemt) {
					mDiaryRadioBtn.setChecked(true);
				} else if(mCurrentShowFragment == mActivityFragment) {
					mActivityRadioBtn.setChecked(true);
				} else {
					mUserCenterBtn.setChecked(true);
				}
				return;
			}

			if(null == mTravelRecordFragment) {
				mTravelRecordFragment = new TravelRecordFragment();
				tc.add(R.id.content, mTravelRecordFragment);
			}
			mCurrentShowFragment = mTravelRecordFragment;
		}

		tc.show(mCurrentShowFragment).commit();
	}

	@Override
	protected void onLoginActivityResult(int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			if (2 == mLoginFlag) {
				mNavigationRedioGroup.check(R.id.user_center_radioBtn);
			}
			if (3 == mLoginFlag) {
					mNavigationRedioGroup.check(R.id.notification_radioBtn);
				}
				mLoginFlag = -1;
			}
		}
}
