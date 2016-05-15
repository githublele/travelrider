package cn.nono.ridertravel.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.List;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.adapter.DiaryViewPageAdapter;
import cn.nono.ridertravel.bean.av.AVBaseUserInfo;
import cn.nono.ridertravel.bean.av.AVMUser;
import cn.nono.ridertravel.bean.av.AVTravelDiary;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.base.BaseFragment;
import cn.nono.ridertravel.ui.diary.DiaryBrowseActivity;
import cn.nono.ridertravel.ui.diary.TravelDiaryEditActivity;
import cn.nono.ridertravel.util.ImageLoaderOptionsSetting;
import cn.nono.ridertravel.view.MSwipeRefreshLayout;

public final class TravelDiaryFragment extends BaseFragment implements OnClickListener,SwipeRefreshLayout.OnRefreshListener{

	private final static int ADD_DIRAY_REQUEST_CODE = 1;

	private Button userInfoBtn = null;
	private Button userSetingBtn = null;
	private ViewPager viewPager = null;
	private PagerAdapter pagerAdapter = null;
	private ListView listView = null;
	private Button addDiaryBtn = null;

	List<AVTravelDiary> diaries = new ArrayList<AVTravelDiary>();
	private int mLastItemIndex = -1;
	private MSwipeRefreshLayout mSwipRefreshLayout;
	private View mFootView;
	private boolean mIsLoading = false;

	public TravelDiaryFragment(){
		parentAct = getActivity();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		onRefresh();
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
			com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(avTravelDiary.getCover().getUrl(), viewHolder.coverImageView, ImageLoaderOptionsSetting.getConstantImageLoaderDefaultOptions());
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

	@Override
	public void onClick(View v) {
		AVMUser avmUser = (AVMUser) AVUser.getCurrentUser();
		if (null == avmUser) {
			login();
			return;
		}
		//jump to add diary act
		Intent intent = new Intent(getActivity(), TravelDiaryEditActivity.class);
		startActivityForResult(intent,ADD_DIRAY_REQUEST_CODE);
	}

	//刷新
	@Override
	public void onRefresh() {

		mSwipRefreshLayout.setRefreshing(true);
		AVQuery<AVTravelDiary> query = AVQuery.getQuery(AVTravelDiary.class);
		query.include(AVTravelDiary.AUTHOR_BASE_INFO_POINTER_KEY);
		query.orderByDescending(AVObject.CREATED_AT);
		query.setLimit(10);
		query.findInBackground(new FindCallback<AVTravelDiary>() {
			@Override
			public void done(List<AVTravelDiary> list, AVException e) {

				for (AVTravelDiary diary:list
					 ) {
					Log.i("bb",diary.getCreatedAt().toString());
				}
				mSwipRefreshLayout.setRefreshing(false);
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
		 mFootView = LayoutInflater.from(getActivity()).inflate(R.layout.item_listview_foot_addmore,null);
		listView.addFooterView(mFootView,null,false);
		listView.setAdapter(lisAdapter);
		listView.removeFooterView(mFootView);
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

		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

				Log.i("bb","scrollState:"+scrollState);
		Log.i("bb","getLastVisiblePosition:"+listView.getLastVisiblePosition());

				if(AbsListView.OnScrollListener.SCROLL_STATE_IDLE == scrollState) {
					//离最后一个数据还有2个间隔的时候 就把footerView加上 并加载数据。
					if(listView.getLastVisiblePosition() >= (lisAdapter.getCount() - 3)) {
						if(listView.getFooterViewsCount() == 0)
							listView.addFooterView(mFootView,null,false);
						if(!mIsLoading)
							loadMoreNetData();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				Log.i("bb","firstVisibleItem:"+firstVisibleItem +" visibleItemCount:"+visibleItemCount + " totalItemCount:"+totalItemCount);
				Log.i("bb","getFooterViewsCount:"+listView.getFooterViewsCount());
				//这里控制footView 的显示


				}




		});



		addDiaryBtn = (Button) view.findViewById(R.id.add_diary_btn);
		addDiaryBtn.setOnClickListener(this);

		mSwipRefreshLayout = (MSwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_ly);
		mSwipRefreshLayout.setOnRefreshListener(this);
		mSwipRefreshLayout.setBaseListView(this.listView);


	}

	private void loadMoreNetData() {
		Log.i("bb","loadMoreNetData");
		if(null == diaries || diaries.size() <= 0) {
			return;
		}


		AVTravelDiary lastDairy = diaries.get(diaries.size() - 1);
		if(null == lastDairy) {
			if(listView.getFooterViewsCount() > 0)
				listView.removeFooterView(mFootView);
			return;
		}

		AVQuery<AVTravelDiary> query = AVQuery.getQuery(AVTravelDiary.class);
		query.orderByDescending(AVObject.CREATED_AT);
		query.include(AVTravelDiary.AUTHOR_BASE_INFO_POINTER_KEY);
		query.whereLessThan(AVObject.CREATED_AT,lastDairy.getCreatedAt());
		query.whereNotEqualTo(AVObject.OBJECT_ID,lastDairy.getObjectId());
		query.setLimit(10);
		mIsLoading = true;
		query.findInBackground(new FindCallback<AVTravelDiary>() {
			@Override
			public void done(List<AVTravelDiary> list, AVException e) {
				mIsLoading = false;
				if(listView.getFooterViewsCount() > 0) {
					listView.removeFooterView(mFootView);
				}

				if (null == e) {
					if (null != list && list.size() > 0) {
						diaries.addAll(list);
						lisAdapter.notifyDataSetChanged();

					} else {
						ToastUtil.toastShort(getActivity(),"没有更多了。");
					}
					return;
				}

				if(AVException.OBJECT_NOT_FOUND == e.getCode()) {
					ToastUtil.toastShort(getActivity(),"没有更多了。");
					return;
				}

				ToastUtil.toastShort(getActivity(),"网络异常，加载失败。"+e.getCode());
				e.printStackTrace();
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
	protected void onLoginActivityResult(int resultCode, Intent data) {
		if(Activity.RESULT_OK == resultCode) {
			onClick(null);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (ADD_DIRAY_REQUEST_CODE == requestCode && Activity.RESULT_OK == resultCode) {
			//新的游记添加完成 == >  重新刷新列表
			onRefresh();
		}
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}



}
