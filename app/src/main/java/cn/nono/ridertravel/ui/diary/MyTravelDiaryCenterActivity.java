package cn.nono.ridertravel.ui.diary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.viewpagerindicator.TabPageIndicator;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.ui.base.BaseNoTitleFragmentActivity;

public class MyTravelDiaryCenterActivity extends BaseNoTitleFragmentActivity implements View.OnClickListener{

	/**
	 * Tab标题
	 */
	private static final String[] TITLE = new String[] { "我的游记", "收藏游记", "没想好X"};
	private Button mBackBtn;



	class MyAdapter extends FragmentPagerAdapter {
		Fragment[] fragments = new Fragment[3];

		public MyAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
			fragments[0] = new MyTravelDiaryFragment();
			fragments[1] = new MyTravelDiaryCollectionFragment();
			fragments[2] = new OtherFragment();
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return fragments[arg0];
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return TITLE.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub
			return TITLE[position % TITLE.length];
		}




	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_diary_center);

		FragmentPagerAdapter adapter = new MyAdapter(getSupportFragmentManager());
		ViewPager pager = (ViewPager) findViewById(R.id.diary_center_content_viewpager);
		pager.setAdapter(adapter);

		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.diary_center_content_tab_indicator);
		indicator.setViewPager(pager);

		mBackBtn = (Button) findViewById(R.id.back_btn);
		mBackBtn.setOnClickListener(this);


	}


	@Override
	public void onClick(View v) {
			finish();
	}
}
