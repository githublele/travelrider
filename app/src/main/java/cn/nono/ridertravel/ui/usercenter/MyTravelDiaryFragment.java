package cn.nono.ridertravel.ui.usercenter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.bean.av.AVMUser;
import cn.nono.ridertravel.bean.av.AVTravelDiary;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.base.BaseFragment;
import cn.nono.ridertravel.util.ImageLoaderOptionsSetting;

public class MyTravelDiaryFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

	private ListView listView;
	private SwipeRefreshLayout mSwipRefreshLayout;
	private List<AVTravelDiary> travelDiaries = new ArrayList<AVTravelDiary>();


	private BaseAdapter diaryAdapter = new BaseAdapter() {
		@Override
		public int getCount() {
			return travelDiaries.size();
		}

		@Override
		public Object getItem(int position) {
			return travelDiaries.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AVTravelDiary avTravelDiary = travelDiaries.get(position);
			ViewHolder viewHolder;
			if(null == convertView) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_my_diary_list,null);
				viewHolder = new ViewHolder();
				viewHolder.coverImageView = (ImageView) convertView.findViewById(R.id.diary_cover_imageview);
				viewHolder.headlineTextView = (TextView) convertView.findViewById(R.id.diary_headline_tv);
				viewHolder.baseInfo = (TextView) convertView.findViewById(R.id.diary_base_info_tv);
				convertView.setTag(viewHolder);
			}
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.headlineTextView.setText(avTravelDiary.getHeadline());
			viewHolder.baseInfo.setText(getDiaryBaseInfo(avTravelDiary));
			ImageLoader.getInstance().displayImage(avTravelDiary.getCover().getUrl(), viewHolder.coverImageView, ImageLoaderOptionsSetting.getConstantImageLoaderDefaultOptions());
			return convertView;
		}
	};

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
	public void onRefresh() {

		AVMUser avUser = (AVMUser) AVUser.getCurrentUser();
		//循例
		if(null != avUser) {
			mSwipRefreshLayout.setRefreshing(true);
			AVQuery<AVTravelDiary> query = avUser.getCreateDiariesRelation().getQuery();
			query.orderByDescending(AVObject.CREATED_AT);
			query.setLimit(10);
			query.findInBackground(new FindCallback<AVTravelDiary>() {
				@Override
				public void done(List<AVTravelDiary> list, AVException e) {
					mSwipRefreshLayout.setRefreshing(false);
					if(null != e && AVException.OBJECT_NOT_FOUND != e.getCode()) {
						ToastUtil.toastShort(getActivity(),"网络数据获取失败"+e.getCode());
						return;
					}

					if(null != list && list.size() > 0) {
						travelDiaries.clear();
						travelDiaries.addAll(list);
						diaryAdapter.notifyDataSetInvalidated();
						return;
					}

						ToastUtil.toastShort(getActivity(),"没有数据。");
				}
			});
		}



	}


	class ViewHolder {
		public TextView baseInfo;
		public ImageView coverImageView;
		public TextView headlineTextView;
	}

	@Override
	public void onAttach(Context context) {
			super.onAttach(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_my_travle_diary, container, false);
		init(view);
		initDate();
		return view;
	}

	private void initDate() {
		onRefresh();
	}

	private void init(View view) {
		// TODO Auto-generated method stub
	 	listView =	(ListView) view.findViewById(R.id.diarys_listview);
		listView.setAdapter(this.diaryAdapter);
		mSwipRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_ly);
		mSwipRefreshLayout.setOnRefreshListener(this);

	}



}
