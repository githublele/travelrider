package cn.nono.ridertravel.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.adapter.DiaryViewPageAdapter;
import cn.nono.ridertravel.bean.av.AVBaseUserInfo;
import cn.nono.ridertravel.bean.av.AVTravelDiary;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.diary.DiaryBrowseActivity;
import cn.nono.ridertravel.ui.diary.MyTravelDiaryCenterActivity;

public final class TravelDiaryFragment extends Fragment {

	private Button userInfoBtn = null;
	private Button userSetingBtn = null;
	private ViewPager viewPager = null;
	private PagerAdapter pagerAdapter = null;
	private ListView listView = null;
	private Button addDiaryBtn = null;

	List<AVTravelDiary> diaries = new ArrayList<AVTravelDiary>();
	private DisplayImageOptions mDisplayOptions;

	public TravelDiaryFragment(){
		parentAct = getActivity();
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(null == mDisplayOptions) {
			mDisplayOptions = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.picture_default) //设置图片在下载期间显示的图片
					.showImageForEmptyUri(R.drawable.picture_default_damaged)//设置图片Uri为空或是错误的时候显示的图片
					.showImageOnFail(R.drawable.picture_default_damaged)  //设置图片加载/解码过程中错误时候显示的图片
					.cacheInMemory(true)//设置下载的图片是否缓存在内存中
					.cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
					.considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
					.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
					.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
					.resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
					.build();//构建完成
		}

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		AVQuery<AVTravelDiary> query = AVQuery.getQuery(AVTravelDiary.class);
		query.include(AVTravelDiary.AUTHOR_BASE_INFO_POINTER_KEY);
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
				viewHolder.coverImageView = (ImageView) convertView.findViewById(R.id.diary_cover_imageview);
				viewHolder.baseInfoTextView = (TextView) convertView.findViewById(R.id.diary_base_info_tv);
				viewHolder.headlineTextView = (TextView) convertView.findViewById(R.id.diary_headline_tv);
				viewHolder.praiseTimesTextView = (TextView) convertView.findViewById(R.id.praise_times_tv);
				viewHolder.userNicknameTextView = (TextView) convertView.findViewById(R.id.user_nickname_tv);
				convertView.setTag(viewHolder);
			}

			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.headlineTextView.setText(avTravelDiary.getHeadline());
			viewHolder.baseInfoTextView.setText(getDiaryBaseInfo(avTravelDiary));
			viewHolder.praiseTimesTextView.setText(avTravelDiary.getPraiseTimes()+"");
			com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(avTravelDiary.getCover().getUrl(), viewHolder.coverImageView,mDisplayOptions);
			AVBaseUserInfo avBaseUserInfo = avTravelDiary.getAuthorBaseInfo();
			if(null != avBaseUserInfo)
				viewHolder.userNicknameTextView.setText(avBaseUserInfo.getNickname());

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
		public TextView baseInfoTextView;
		public ImageView coverImageView;
		public TextView headlineTextView;
		public TextView userNicknameTextView;
		public TextView praiseTimesTextView;
	}


	private void init(View view) {
		// TODO Auto-generated method stub
		if(null == parentAct)
			parentAct = getActivity();

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
					Intent intent = new Intent(getActivity(), UserInfoActivity.class);
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

	private String getDiaryBaseInfo(AVTravelDiary avTravelDiary) {
		if(null == avTravelDiary)
			return "";
		Integer days =	avTravelDiary.getDays();
		if(null == days)
			days = 1;
		String startDate = avTravelDiary.getTravelStartDate();
		if(null == startDate) {
			startDate = "";
		}

		StringBuilder stringBuilder = new StringBuilder(32);
		stringBuilder.append(startDate);
		stringBuilder.append("	");
		stringBuilder.append(days);
		stringBuilder.append("天");
		return stringBuilder.toString();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

}
