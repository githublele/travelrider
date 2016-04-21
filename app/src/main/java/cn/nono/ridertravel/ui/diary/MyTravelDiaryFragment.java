package cn.nono.ridertravel.ui.diary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import cn.nono.ridertravel.bean.av.AVTravelDiary;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.LoginActivity;

public class MyTravelDiaryFragment extends Fragment implements OnClickListener{


	private final static int LOGIN_REQUEST_CODE = 1;

	// 声明RequestQueue
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;



	private Button diaryAddButton = null;
	private ListView diaryListView;
	private List<AVTravelDiary> travelDiaries = new ArrayList<AVTravelDiary>();
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

		}
	};
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
			ViewHolder viewHolder;
			if(null == convertView) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_diary_list,null);
				viewHolder = new ViewHolder();
				viewHolder.coverImageView = (NetworkImageView) convertView.findViewById(R.id.cover_network_imageveiw);
				viewHolder.daysCountTextView = (TextView) convertView.findViewById(R.id.days_count_tv);
				viewHolder.locationTextView = (TextView) convertView.findViewById(R.id.location_tv);
				viewHolder.startDateTextView = (TextView) convertView.findViewById(R.id.start_date_tv);
				viewHolder.summaryTextView = (TextView) convertView.findViewById(R.id.summary_tv);
				convertView.setTag(viewHolder);
			}
			viewHolder = (ViewHolder) convertView.getTag();
			AVTravelDiary avTravelDiary = travelDiaries.get(position);
			viewHolder.daysCountTextView.setText(avTravelDiary.getDays()+"天");
			viewHolder.locationTextView.setText(avTravelDiary.getPlace());
			viewHolder.coverImageView.setDefaultImageResId(R.mipmap.ic_launcher);
			viewHolder.coverImageView.setImageUrl(avTravelDiary.getCover().getUrl(),mImageLoader);





			return convertView;
		}
	};

	class ViewHolder {
		public TextView startDateTextView;
		public TextView locationTextView;
//		public ImageView coverImageView;
		public NetworkImageView coverImageView;
		public TextView daysCountTextView;
		public TextView summaryTextView;
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		AVUser avUser = AVUser.getCurrentUser();
		if(null == avUser) {
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivityForResult(intent,LOGIN_REQUEST_CODE);
			return;
		}
		Intent intent = new Intent(getActivity(), TravelDiaryEditActivity.class);
		startActivity(intent);
	}


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		// 实例化请求队列
		mRequestQueue = Volley.newRequestQueue(getActivity());
		// 实例化图片加载器
		mImageLoader = new ImageLoader(mRequestQueue,((RiderTravelApplication)getActivity().getApplication()).getDiskBitmapCache());



	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_my_travle_diary, container, false);
		init(view);
		return view;
	}

	ProgressDialog progressDialog = null;
	private void init(View view) {
		// TODO Auto-generated method stub
		diaryAddButton = (Button) view.findViewById(R.id.add_diary_btn);
		diaryAddButton.setOnClickListener(this);
		diaryListView = (ListView) view.findViewById(R.id.diarys_listview);
		diaryListView.setAdapter(diaryAdapter);
		diaryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				AVTravelDiary avTravelDiary = travelDiaries.get(position);
				if(null == avTravelDiary)
					return;
				Intent intent = new Intent(getActivity(),DiaryBrowseActivity.class);
				intent.putExtra("avTravelDiary",avTravelDiary);
				startActivity(intent);


			}
		});


		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage("加载中。。。。");
		progressDialog.setCancelable(false);
		progressDialog.show();
		AVUser avUser = AVUser.getCurrentUser();
		AVQuery<AVTravelDiary> travelDiaryAVQuery = AVQuery.getQuery(AVTravelDiary.class);
		travelDiaryAVQuery.whereEqualTo(AVTravelDiary.AUTHOR_KEY,avUser);
		travelDiaryAVQuery.orderByDescending("createdAt");
		travelDiaryAVQuery.findInBackground(new FindCallback<AVTravelDiary>() {
			@Override
			public void done(List<AVTravelDiary> list, AVException e) {
				if(null != progressDialog) {
					progressDialog.dismiss();
					progressDialog = null;
				}

				if (null == e) {
					if(null != list && list.size() > 0) {
						travelDiaries.clear();
						travelDiaries.addAll(list);
						diaryAdapter.notifyDataSetInvalidated();
					}

				} else {
					e.printStackTrace();
					ToastUtil.toastShort(getActivity(),"拉取数据失败。error");
				}


			}
		});




	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(LOGIN_REQUEST_CODE == requestCode) {
			if(resultCode == Activity.RESULT_OK) {
				onClick(null);
				return;
			}
		}
	}



	@Override
	public void onDestroy() {

		mRequestQueue.stop();
		mRequestQueue = null;
		super.onDestroy();
	}


}
