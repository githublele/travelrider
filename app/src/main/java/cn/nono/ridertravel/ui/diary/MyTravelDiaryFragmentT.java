package cn.nono.ridertravel.ui.diary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.bean.av.AVTravelDiary;
import cn.nono.ridertravel.ui.LoginActivity;

public class MyTravelDiaryFragmentT extends Fragment implements OnClickListener{

	private final static int LOGIN_REQUEST_CODE = 1;
	private DisplayImageOptions mDisplayOptions = null;
	private Button diaryAddButton;
	private ListView listView;
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
			ImageLoader.getInstance().displayImage(avTravelDiary.getCover().getUrl(), viewHolder.coverImageView,mDisplayOptions);
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


	class ViewHolder {
		public TextView baseInfo;
		public ImageView coverImageView;
		public TextView headlineTextView;
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
			super.onAttach(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_my_travle_diary, container, false);
		init(view);
		return view;
	}

	private void init(View view) {
		// TODO Auto-generated method stub
	 	listView =	(ListView) view.findViewById(R.id.diarys_listview);
		listView.setAdapter(this.diaryAdapter);

		diaryAddButton = (Button) view.findViewById(R.id.add_diary_btn);
		diaryAddButton.setOnClickListener(this);

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

		super.onDestroy();
	}




}
