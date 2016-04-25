package cn.nono.ridertravel.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.ui.base.BaseNoTitleFragmentActivity;
import cn.nono.ridertravel.ui.travelact.TravelActivityFragment;
import cn.nono.ridertravel.ui.travelrecord.TravelRecordFragment;

public class MainActivity extends BaseNoTitleFragmentActivity implements RadioGroup.OnCheckedChangeListener{


	private RadioGroup mNavigationRedioGroup;


	private TravelDiaryFragment mDiaryFragemt = null;
	private TravelActivityFragment mActivityFragment = null;
	private TravelRecordFragment mTravelRecordFragment = null;
	private Fragment mCurrentShowFragment = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationRedioGroup = (RadioGroup) findViewById(R.id.navigation_radioGroup);
		mNavigationRedioGroup.setOnCheckedChangeListener(this);




		/*
		if(null == mDiaryFragemt)
			mDiaryFragemt = new TravelDiaryFragment();
		//暂时这样
		getSupportFragmentManager().beginTransaction().add(R.id.content, mDiaryFragemt).show(mDiaryFragemt).commit();
		*/
		mNavigationRedioGroup.check(R.id.diary_radioBtn);

	}

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
		}
		//R.id.record_radioBtn
		 else {
			if(null == mTravelRecordFragment) {
				mTravelRecordFragment = new TravelRecordFragment();
				tc.add(R.id.content, mTravelRecordFragment);
			}
			mCurrentShowFragment = mTravelRecordFragment;
		}

		tc.show(mCurrentShowFragment).commit();
	}
}
