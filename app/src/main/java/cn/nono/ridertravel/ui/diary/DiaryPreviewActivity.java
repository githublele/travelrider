package cn.nono.ridertravel.ui.diary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.bean.DiaryBean;
import cn.nono.ridertravel.bean.DiarySheetBean;
import cn.nono.ridertravel.bean.PhotoBean;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;
import cn.nono.ridertravel.util.ImageLoaderOptionsSetting;
import cn.nono.ridertravel.util.StringUtil;

public class DiaryPreviewActivity extends BaseNoTitleActivity {

	ImageView userHeadIconImageView;
	TextView diaryNameTextView;
	TextView diaryBaseInfoTextView;
	ListView diaryContentListView;
	DiaryBean diaryBean;
	List<DiaryItem> listItems = new ArrayList<DiaryPreviewActivity.DiaryItem>();
	LayoutInflater layoutInflater = null;
	BaseAdapter  adapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			DiaryItem diaryItem = listItems.get(position);
			ViewHolder viewHolder;
			if(null == convertView) {
				convertView = layoutInflater.inflate(R.layout.item_diary_preview, null);
				viewHolder = new ViewHolder();
				viewHolder.photoDescriptionTextView = (TextView) convertView.findViewById(R.id.photo_description_tv);
				viewHolder.photoImageView = (ImageView) convertView.findViewById(R.id.photo_imageview);
				viewHolder.photoLinearLayout = (LinearLayout) convertView.findViewById(R.id.diary_sheet_content_ll);

				viewHolder.sheetDateTextView = (TextView) convertView.findViewById(R.id.diary_sheet_date_tv);
				viewHolder.sheetDaySortTextView = (TextView) convertView.findViewById(R.id.date_sort_tv);
				viewHolder.sheetLinearLayout = (LinearLayout) convertView.findViewById(R.id.diary_sheet_ll);

				convertView.setTag(viewHolder);
			}

			viewHolder = (ViewHolder) convertView.getTag();
			if(ItemType.PHOTO == diaryItem.type) {
				viewHolder.sheetLinearLayout.setVisibility(View.GONE);
				viewHolder.photoLinearLayout.setVisibility(View.VISIBLE);
				if(!StringUtil.empty(diaryItem.photo_description)) {
					viewHolder.photoDescriptionTextView.setText(diaryItem.photo_description);
					viewHolder.photoDescriptionTextView.setVisibility(View.VISIBLE);
				} else {
					viewHolder.photoDescriptionTextView.setVisibility(View.GONE);
				}

				if(!StringUtil.empty(diaryItem.photo_path)) {
					viewHolder.photoImageView.setVisibility(View.VISIBLE);
					com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage("file://"+diaryItem.photo_path,viewHolder.photoImageView, ImageLoaderOptionsSetting.getConstantImageLoaderDefaultOptions());
				} else {
					viewHolder.photoImageView.setVisibility(View.GONE);
				}

			} else if(ItemType.DIARY_SHEET == diaryItem.type){
				viewHolder.sheetLinearLayout.setVisibility(View.VISIBLE);
				viewHolder.photoLinearLayout.setVisibility(View.GONE);

				viewHolder.sheetDateTextView.setText(getDateInfo(diaryItem.sheet_year,diaryItem.sheet_monthOfYear,diaryItem.sheet_dayOfMonth));
			}

			return convertView;
		}

		private String getDateInfo(int year,int monthOfYear,
								   int dayOfMonth) {
			// TODO Auto-generated method stub
			StringBuilder builder = new StringBuilder();
			builder.append(year);
//			2016年03月01日 周二
			builder.append("年");
			if(monthOfYear < 9) {
				builder.append("0");
			}
			builder.append(monthOfYear+1);
			builder.append("月");

			if(dayOfMonth < 10) {
				builder.append("0");
			}
			builder.append(dayOfMonth);
			builder.append("日	");

			Calendar calendar = Calendar.getInstance();
			calendar.clear();
			calendar.set(year, monthOfYear, dayOfMonth);
			int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
			switch (dayIndex) {
				case Calendar.SUNDAY:
					builder.append("周日");
					break;
				case Calendar.MONDAY:
					builder.append("周一");
					break;
				case Calendar.TUESDAY:
					builder.append("周二");
					break;
				case Calendar.WEDNESDAY:
					builder.append("周三");
					break;
				case Calendar.THURSDAY:
					builder.append("周四");
					break;
				case Calendar.FRIDAY:
					builder.append("周五");
					break;
				case Calendar.SATURDAY:
					builder.append("周六");
					break;
				default:
					break;
			}
			return builder.toString();
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listItems.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listItems.size();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diary_preview);

		layoutInflater = LayoutInflater.from(this);

		userHeadIconImageView = (ImageView) findViewById(R.id.user_head_icon_imageview);
		diaryNameTextView = (TextView) findViewById(R.id.diary_name_textview);
		diaryBaseInfoTextView = (TextView) findViewById(R.id.diary_base_info_textview);
		diaryContentListView = (ListView) findViewById(R.id.diary_content_listview);
		diaryBean = getIntent().getParcelableExtra("diary");
		if(null == diaryBean)
			finish();
		diaryNameTextView.setText(diaryBean.diaryTitle);
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat simpleDateFormat =	new SimpleDateFormat("yyyy.MM.dd");
		sb.append(diaryBean.startDateStr);
		sb.append("	");
		sb.append(diaryBean.days);
		sb.append("天");
		sb.append("	");
		sb.append(diaryBean.photoCount);
		sb.append("图");
		diaryBaseInfoTextView.setText(sb.toString());
		init();
	}


	private void init() {
		// TODO Auto-generated method stub
		//
		initList(diaryBean);


	}

	private void initList(DiaryBean diaryBean) {
		// TODO Auto-generated method stub
		List<DiarySheetBean> sheetList = diaryBean.diarySheetBeans;
		int sheetLength = sheetList.size();
		DiarySheetBean sheet;
		List<PhotoBean> photoList;
		int photoLength;
		PhotoBean photo;

		DiaryItem diaryItem;
		for (int i = 0; i < sheetLength; i++) {
			sheet = sheetList.get(i);
			diaryItem = new DiaryItem();
			diaryItem.type = ItemType.DIARY_SHEET;
			diaryItem.sheet_dayOfMonth = sheet.dayOfMonth;
			diaryItem.sheet_location = sheet.location;
			diaryItem.sheet_monthOfYear = sheet.monthOfYear;
			diaryItem.sheet_year = sheet.year;
			listItems.add(diaryItem);
			diaryItem = null;

			photoList = sheet.photos;
			photoLength = photoList.size();
			for (int j = 0; j < photoLength; j++) {
				photo = photoList.get(j);
				diaryItem = new DiaryItem();
				diaryItem.type = ItemType.PHOTO;
				diaryItem.photo_description = photo.description;
				diaryItem.photo_path = photo.path;
				listItems.add(diaryItem);
			}

//			DiaryItem diaryItemTemp = new DiaryItem();
//			diaryItemTemp.type = ItemType.PHOTO;
//			diaryItemTemp.photo_path = diaryItem.photo_path;
//			listItems.add(diaryItemTemp);
//
//			diaryItemTemp = new DiaryItem();
//			diaryItemTemp.type = ItemType.PHOTO;
//			diaryItemTemp.photo_description = diaryItem.photo_description;
//			listItems.add(diaryItemTemp);

		}

		diaryContentListView.setAdapter(adapter);
	}

	class ViewHolder {
		public Button location_btn;
		public Button like_btn;


		public LinearLayout sheetLinearLayout;
		public TextView sheetDaySortTextView;
		public TextView sheetDateTextView;

		public LinearLayout photoLinearLayout;
		public ImageView photoImageView;
		public TextView photoDescriptionTextView;
	}

	class DiaryItem {
		public ItemType type;

		public String photo_path;
		public String photo_description;

		public int sheet_year = -1;
		public int sheet_monthOfYear = -1;
		public int sheet_dayOfMonth = -1;
		public String sheet_location = null;
	}

	enum ItemType {
		PHOTO,DIARY_SHEET
	}
	
}
