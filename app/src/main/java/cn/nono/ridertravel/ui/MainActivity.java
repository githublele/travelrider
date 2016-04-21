package cn.nono.ridertravel.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import cn.nono.ridertravel.R;

public class MainActivity extends FragmentActivity {

	private TravelDiaryFragment travelDiaryFragment = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);
		if(null == travelDiaryFragment)
			travelDiaryFragment = new TravelDiaryFragment();
		//暂时这样
		getSupportFragmentManager().beginTransaction().add(R.id.content, travelDiaryFragment).show(travelDiaryFragment).commit();

	}
}
