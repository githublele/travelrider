package cn.nono.ridertravel.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;

import cn.nono.ridertravel.R;

public class DiaryViewPageAdapter extends PagerAdapter {

	Context context;
	HashMap<Integer, View> views;
	
	public DiaryViewPageAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		views = new HashMap<Integer, View>();
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
//		super.destroyItem(container, position, object);
		container.removeView(views.get(position));
		
		
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
//		return super.instantiateItem(container, position);
		View v = views.get(position);
		if(null == v) {
			v = LayoutInflater.from(context).inflate(R.layout.item_diary_push, null);
			views.put(position, v);
			v.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(context, "xx",Toast.LENGTH_SHORT).show();
				}
			});
			switch (position) {
			case 0:
				v.setBackgroundResource(R.mipmap.photo_1);
				break;
			case 1:
				v.setBackgroundResource(R.mipmap.photo_2);

				break;
				
			case 2:
				v.setBackgroundResource(R.mipmap.photo_3);

				break;
			default:
				break;
			}
		}
		container.addView(v);
		
		return v;
		
		
		
	}
	
	
		
	
	
	

}
