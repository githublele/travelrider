package cn.nono.ridertravel.ui.diary;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.adapter.DiaryEditPhotoAdapter;
import cn.nono.ridertravel.adapter.DiaryEditPhotoAdapter.ViewHolder;
import cn.nono.ridertravel.bean.DiaryBean;
import cn.nono.ridertravel.bean.DiarySheetBean;
import cn.nono.ridertravel.bean.PhotoBean;
import cn.nono.ridertravel.bean.av.AVMUser;
import cn.nono.ridertravel.debug.LogSimple;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.AddPhotoDescriptionActivity;
import cn.nono.ridertravel.ui.SelectPhotoActivity;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;
import cn.nono.ridertravel.util.DataSimpleGetUtil;
import cn.nono.ridertravel.util.UploadThread;

public class TravelDiaryEditActivity extends BaseNoTitleActivity implements
		OnClickListener {

	private final int SELECT_PHOTO_CODE = 1;
	private final int ADD_PHOTO_DESCRIPTION = 2;

	private ListView diaryListView;
	private Button addDiarySheetButton;
	private Button previewButton;
	private Button backButton;
	private Button uploadDiaryButton;
	private EditText diaryHeadlineEditText;
	private TextView dateStartTextView;
	private TextView dayCountTextView;
	private TextView photoCountTextView;

	private int mPhotoCount = 0;
	private int mDaysCount = 0;
	private long mStartTime = 0;
	private long mEndTime = 0;



	private int year = 1970, monthOfYear = 0, dayOfMonth = 0;
	private boolean datePickerHaveData = false;
	//当前操作的日记 章节位置
	private int currtEditPhotoDiarySheetPos = -1;
	//当前操作的图片
	private PhotoBean currtEditPhoto = null;

	private List<DiaryEditPhotoAdapter> diaryEditPhotoAdapters = new ArrayList<DiaryEditPhotoAdapter>();
	private OnItemClickListener diaryPhotoItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
			// TODO Auto-generated method stub

			DiaryEditPhotoAdapter.ViewHolder viewHolder = (ViewHolder) view.getTag();
			currtEditPhotoDiarySheetPos = (Integer) parent.getTag();
			Intent intent = null;
			if(viewHolder.isPhoto) {
				PhotoBean photo = (PhotoBean) diaryEditPhotoAdapters.get(currtEditPhotoDiarySheetPos).getItem(position);
				if(null == photo || null == photo.path || photo.path.isEmpty())
					return;
				currtEditPhoto = photo;
				intent = new Intent(TravelDiaryEditActivity.this,AddPhotoDescriptionActivity.class);
				intent.putExtra("photo", photo);
				startActivityForResult(intent,ADD_PHOTO_DESCRIPTION);
			} else {
				intent = new Intent(TravelDiaryEditActivity.this,SelectPhotoActivity.class);
				startActivityForResult(intent, SELECT_PHOTO_CODE);
			}

		}
	};
	private BaseAdapter listViewAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			DiaryEditPhotoAdapter diaryEditPhotoAdapter = diaryEditPhotoAdapters.get(position);
			DiaryViewHolder diaryViewHolder = null;
			if(null == convertView) {
				diaryViewHolder = new DiaryViewHolder();
				LayoutInflater layoutInflater= LayoutInflater.from(TravelDiaryEditActivity.this);
				convertView = layoutInflater.inflate(R.layout.item_travel_diary_edit_chapter,null);
				diaryViewHolder.diaryChapterTextView = (TextView) convertView.findViewById(R.id.diary_chapter_date_tv);
				diaryViewHolder.gridView = (GridView) convertView.findViewById(R.id.photos_gridlayout);
				diaryViewHolder.gridView.setOnItemClickListener(diaryPhotoItemClickListener);
				convertView.setTag(diaryViewHolder);
			}


			diaryViewHolder = (DiaryViewHolder) convertView.getTag();
			diaryViewHolder.diaryChapterTextView.setText(getDiaryChapterDateStr(diaryEditPhotoAdapter.getDiaryBean()));
			diaryViewHolder.gridView.setTag(position);
			diaryViewHolder.gridView.setAdapter(diaryEditPhotoAdapter);

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
			return diaryEditPhotoAdapters.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return diaryEditPhotoAdapters.size();
		}

		class DiaryViewHolder {
			public TextView diaryChapterTextView;
			public GridView gridView;
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_travel_diary_edit);

		initView();
		Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		monthOfYear = calendar.get(Calendar.MONTH);
		dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		calendar = null;
	}

	private void initView() {
		dateStartTextView = (TextView) findViewById(R.id.date_start_tv);
		photoCountTextView = (TextView) findViewById(R.id.photo_count_tv);
		dayCountTextView = (TextView) findViewById(R.id.days_count_tv);
		diaryHeadlineEditText = (EditText) findViewById(R.id.diary_headline_ev);

		previewButton = (Button) findViewById(R.id.preview_diary_btn);
		previewButton.setOnClickListener(this);
		backButton = (Button) findViewById(R.id.back_btn);
		backButton.setOnClickListener(this);
		uploadDiaryButton = (Button) findViewById(R.id.upload_diary_btn);
		uploadDiaryButton.setOnClickListener(this);

		addDiarySheetButton = (Button) findViewById(R.id.add_sheet_btn);
		addDiarySheetButton.setOnClickListener(this);
		diaryListView = (ListView) findViewById(R.id.diary_sheet_listview);
		diaryListView.setAdapter(listViewAdapter);
	}

	protected CharSequence getDiaryChapterDateStr(DiarySheetBean diaryBean) {
		// TODO Auto-generated method stub

		int year = diaryBean.year;
		int monthOfYear = diaryBean.monthOfYear;
		int dayOfMonth = diaryBean.dayOfMonth;

		StringBuilder builder = new StringBuilder();
		builder.append(year);
//		2016年03月01日 周二
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

	private DialogInterface.OnClickListener datePickerDialogButtonListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			if(AlertDialog.BUTTON_POSITIVE == which) {
				datePickerHaveData = true;
			} else {
				datePickerHaveData = false;
			}
		}

	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
			case R.id.add_sheet_btn:
				addSheet();
				break;
			case R.id.preview_diary_btn:
				previewDiary();
				break;
			case R.id.back_btn:
				finish();
				break;
			case R.id.upload_diary_btn:
				uploadDiary();
				break;
			default:
				break;
		}

	}

	AlertDialog uploadAlertDialog;
	private void uploadDiary() {
		DiaryBean diary = getCurrentEditDiary();
		//必要检查
		String msg = checkDiaryValidity(diary);
		if(null != msg) {
			ToastUtil.toastLong(this, msg);
			return;
		}

		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//取消上传


			}
		});
		builder.setTitle("正在上传");
		builder.setMessage("...");
		uploadAlertDialog = builder.create();
		uploadAlertDialog.show();

		uploadToNet(diary, (AVMUser) AVUser.getCurrentUser());

	}



Handler handler = new Handler() {

	@Override
	public void handleMessage(Message msg) {
		if(null != uploadAlertDialog) {
			uploadAlertDialog.dismiss();
			uploadAlertDialog = null;
		}
		ToastUtil.toastShort(TravelDiaryEditActivity.this,msg.obj.toString());
	}
};

	private void uploadToNet(DiaryBean diary, AVMUser currentUser) {

	new UploadThread(diary,currentUser,handler, new UploadThread.SaveCallBack() {
		@Override
		public void succeed(Handler handler) {
			Message msg = new Message();
			msg.obj = "OK";
			handler.sendMessage(msg);
		}

		@Override
		public void error(Handler handler, AVException e) {
			Message msg = new Message();
			msg.obj = "error";
			handler.sendMessage(msg);
			e.printStackTrace();
		}
	}).start();


	}

	private String checkDiaryValidity(DiaryBean diary) {
		return null;
	}

	private void previewDiary() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, DiaryPreviewActivity.class);
		DiaryBean diary = getCurrentEditDiary();
		intent.putExtra("diary",diary);
		startActivity(intent);
	}

	private DiaryBean getCurrentEditDiary() {
		// TODO Auto-generated method stub
		DiaryBean diaryBean = new DiaryBean();




		diaryBean.diaryTitle = DataSimpleGetUtil.getEditTextData(diaryHeadlineEditText);
		diaryBean.location = "日本";
		diaryBean.days = mDaysCount;
		diaryBean.photoCount = mPhotoCount;
		int length = diaryEditPhotoAdapters.size();
		DiaryEditPhotoAdapter diaryEditPhotoAdapter;
		String coverPath = null;
		for (int i = 0; i < length; i++) {
			diaryEditPhotoAdapter = diaryEditPhotoAdapters.get(i);
			if(coverPath == null) {
				List<PhotoBean> photos = diaryEditPhotoAdapter.getDiaryBean().photos;
				if(null != photos && photos.size() > 0) {
					for (PhotoBean bean:photos) {
						if(null != bean.path) {
							coverPath = bean.path;
							break;
						}

					}
				}
			}
			diaryBean.addSheet(diaryEditPhotoAdapter.getDiaryBean());
			diaryBean.photoCount = diaryBean.photoCount + diaryEditPhotoAdapter.getDiaryBean().photos.size();
		}

		diaryBean.coverImagePath = coverPath;
		return diaryBean;
	}

	private void addSheet() {
		// TODO Auto-generated method stub
		DatePickerDialog datePickerDialog = new DatePickerDialog(this,
				new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
										  int monthOfYear, int dayOfMonth) {
						// TODO Auto-generated method stub

						if(datePickerHaveData) {
							addDiarySheet(year,monthOfYear,dayOfMonth);
							datePickerHaveData = false;
						}
						LogSimple.i(System.currentTimeMillis()+"");
					}
				}, year, monthOfYear, dayOfMonth);
		datePickerDialog.setCancelable(true);
		datePickerDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",datePickerDialogButtonListener);
		datePickerDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",datePickerDialogButtonListener);
		datePickerDialog.show();
	}

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
	/**
	 * 添加日记
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 */

	private void addDiarySheet(int year, int monthOfYear, int dayOfMonth) {
		// TODO Auto-generated method stub
		//默认全部添加
		DiarySheetBean diaryBean = new DiarySheetBean();
		diaryBean.year = year;
		diaryBean.monthOfYear = monthOfYear;
		diaryBean.dayOfMonth = dayOfMonth;
		diaryEditPhotoAdapters.add(new DiaryEditPhotoAdapter(diaryBean, this));
		listViewAdapter.notifyDataSetChanged();

		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year,monthOfYear,dayOfMonth);
		long time = calendar.getTimeInMillis();
		boolean timeUpdate =false;
		//第一次
		if(mEndTime == 0 && mStartTime == 0) {
			mStartTime = time;
			mEndTime = time;
			mDaysCount = 1;
			timeUpdate = true;
			dayCountTextView.setText(mDaysCount+"天");
			dateStartTextView.setText(simpleDateFormat.format(new Date(mStartTime)));
		} else {

			if(this.mStartTime > time) {
				this.mStartTime = time;
				dateStartTextView.setText(simpleDateFormat.format(new Date(mStartTime)));
				timeUpdate = true;
			}

			if(this.mEndTime < time) {
				this.mEndTime = time;
				timeUpdate = true;
			}

			if(timeUpdate) {
				mDaysCount = 1 + (int) (((mEndTime - mStartTime)) / 86400000);
				dayCountTextView.setText(mDaysCount+"天");
			}
		}
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == SELECT_PHOTO_CODE && null != data && RESULT_OK == resultCode) {
			addPhotoPaths(data);
		}

		else if (requestCode == ADD_PHOTO_DESCRIPTION && null != data && RESULT_OK == resultCode) {
			addPhotoDescription(data);
		}

	}

	private void addPhotoPaths(Intent data) {
		String[] photoPaths = data.getStringArrayExtra("photoPaths");
		if(null == photoPaths || photoPaths.length <= 0)
			return;

		mPhotoCount = mPhotoCount + photoPaths.length;
		photoCountTextView.setText(mPhotoCount+"图");

		DiaryEditPhotoAdapter adapter = this.diaryEditPhotoAdapters.get(currtEditPhotoDiarySheetPos);
		for (String path : photoPaths) {
			adapter.addPhoto(path);
		}
		adapter.notifyDataSetInvalidated();
		listViewAdapter.notifyDataSetInvalidated();

		this.currtEditPhotoDiarySheetPos = -1;
	}

	private void addPhotoDescription(Intent data) {
		PhotoBean photoBean = (PhotoBean) data.getParcelableExtra("photo");
		if(null == photoBean)
			return;

		this.currtEditPhoto.description = photoBean.description;
		this.currtEditPhoto = null;

	}
}
