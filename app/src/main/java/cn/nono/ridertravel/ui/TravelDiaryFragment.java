package cn.nono.ridertravel.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.List;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.RiderTravelApplication;
import cn.nono.ridertravel.adapter.DiaryViewPageAdapter;
import cn.nono.ridertravel.bean.av.AVTravelDiary;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.diary.DiaryBrowseActivity;
import cn.nono.ridertravel.ui.diary.MyTravelDiaryCenterActivity;

public final class TravelDiaryFragment extends Fragment {

	private SearchView searchView = null;
	private Button userInfoBtn = null;
	private Button userSetingBtn = null;
	private ViewPager viewPager = null;
	private PagerAdapter pagerAdapter = null;
	private ListView listView = null;
	private Button addDiaryBtn = null;

	private ImageLoader mImageLoader;
	private RequestQueue mRequestQueue;

	List<AVTravelDiary> diaries = new ArrayList<AVTravelDiary>();

	public TravelDiaryFragment(){
		parentAct = getActivity();
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mRequestQueue = Volley.newRequestQueue(getActivity());
		mImageLoader = new ImageLoader(mRequestQueue,((RiderTravelApplication)getActivity().getApplication()).getDiskBitmapCache());


	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		AVQuery<AVTravelDiary> query = AVQuery.getQuery(AVTravelDiary.class);
		query.orderByDescending("createAt");
		query.findInBackground(new FindCallback<AVTravelDiary>() {
			@Override
			public void done(List<AVTravelDiary> list, AVException e) {
				diaries.clear();
				if(null != e) {
					ToastUtil.toastLong(getActivity(), "拉取数据失败");
					e.printStackTrace();
					return;
				}

				diaries.addAll(list);
				lisAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//
		View view = inflater.inflate(R.layout.fragment_diary, container, false);
		init(view);
		return view;
	}

	private Activity parentAct = null;
	private BaseAdapter lisAdapter = new BaseAdapter() {


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder viewHolder;
			AVTravelDiary avTravelDiary = diaries.get(position);
			if(null == convertView) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(parentAct).inflate(R.layout.item_diary_list,null);
				viewHolder.coverNetWorkImageView = (NetworkImageView) convertView.findViewById(R.id.diary_cover_networkimageview);
				viewHolder.daysCountTextView = (TextView) convertView.findViewById(R.id.days_count_tv);
				viewHolder.startDateTextView = (TextView) convertView.findViewById(R.id.start_date_tv);
				viewHolder.placeTextView = (TextView) convertView.findViewById(R.id.location_tv);
				viewHolder.summaryTextView = (TextView) convertView.findViewById(R.id.summary_tv);
				convertView.setTag(viewHolder);
			}

			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.coverNetWorkImageView.setDefaultImageResId(R.mipmap.ic_launcher);
			viewHolder.coverNetWorkImageView.setImageUrl(avTravelDiary.getCover().getUrl(),mImageLoader);
			viewHolder.placeTextView.setText(avTravelDiary.getPlace());
			viewHolder.startDateTextView.setText(avTravelDiary.getTravelStartDate());
			viewHolder.daysCountTextView.setText(avTravelDiary.getDays()+"");
			viewHolder.summaryTextView.setText(avTravelDiary.getHeadline()+"");

			return convertView;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return diaries.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return diaries.size();
		}
	};

	class ViewHolder {
		public TextView startDateTextView;
		public TextView daysCountTextView;
		public TextView placeTextView;
		public TextView summaryTextView;
		public NetworkImageView coverNetWorkImageView;
	}


	private void init(View view) {
		// TODO Auto-generated method stub
		if(null == parentAct)
			parentAct = getActivity();

		searchView = (SearchView) view.findViewById(R.id.search);
		userInfoBtn = (Button) view.findViewById(R.id.user_info);
		userSetingBtn = (Button) view.findViewById(R.id.user_seting);
		userInfoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//判断是否已经登陆
				AVUser user = AVUser.getCurrentUser();
				//已经登陆 显示用户信息,没有登陆进入登陆页面
				if (null != user) {
					Intent intent = new Intent(getActivity(), UserCenterActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					startActivity(intent);
				}
			}
		});

		userSetingBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(parentAct,UserSetingActivity.class);
				parentAct.startActivity(intent);
			}
		});

		viewPager = (ViewPager) view.findViewById(R.id.viewPager);
		pagerAdapter = new DiaryViewPageAdapter(getActivity());
		viewPager.setAdapter(pagerAdapter);

		listView = (ListView) view.findViewById(R.id.diary_list);
		listView.setAdapter(lisAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), DiaryBrowseActivity.class);
				intent.putExtra("avTravelDiary",diaries.get(position));
				startActivity(intent);

			}
		});

		addDiaryBtn = (Button) view.findViewById(R.id.add_diary_btn);
		addDiaryBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//jump to add diary act
				Intent intent = new Intent(getActivity(), MyTravelDiaryCenterActivity.class);
				startActivity(intent);
			}
		});

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mRequestQueue.start();
		mRequestQueue = null;
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}
	
	

}
