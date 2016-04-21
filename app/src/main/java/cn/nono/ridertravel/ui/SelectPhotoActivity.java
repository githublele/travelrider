package cn.nono.ridertravel.ui;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;
import cn.nono.ridertravel.util.ImageLoader;

public class SelectPhotoActivity extends BaseNoTitleActivity{

	HashMap<String,String> selectPhotoPaths = new HashMap<String, String>();
	GridView gridView;
	ArrayList<String> paths = new ArrayList<String>();
	ArrayList<FolderBean> folderBeans = new ArrayList<SelectPhotoActivity.FolderBean>();

	OnClickListener photoOnclickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			CheckBox checkBox = (CheckBox) v;
			String path = checkBox.getTag().toString();
			if(checkBox.isChecked())
				selectPhotoPaths.put(path, path);
			else
				selectPhotoPaths.remove(path);
		}
	};


	BaseAdapter adapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			String path = paths.get(position);
			View v =  convertView;
			ViewHolder viewHolder = null;
			if(v == null) {
				viewHolder = new ViewHolder();
				v = LayoutInflater.from(SelectPhotoActivity.this).inflate(R.layout.item_photo, null);
				viewHolder.imageView = (ImageView) v.findViewById(R.id.photo_iv);
				viewHolder.checkBox = (CheckBox) v.findViewById(R.id.photo_check_checkbox);
				viewHolder.checkBox.setOnClickListener(photoOnclickListener);
				v.setTag(viewHolder);
			}

			viewHolder = (ViewHolder) v.getTag();
			viewHolder.checkBox.setTag(path);
			ImageLoader.getInstance().loadImage(path, viewHolder.imageView);
			return v;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return paths.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return paths.size();
		}
	};


	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			adapter.notifyDataSetChanged();
			progressDialog.dismiss();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_photo);
		gridView = (GridView) findViewById(R.id.gridView1);
		gridView.setAdapter(adapter);
		initData();

	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		if(!selectPhotoPaths.isEmpty()) {

			intent.putExtra("photoPaths", toStringArray(selectPhotoPaths.values()));
		}
		setResult(RESULT_OK, intent);
		super.onBackPressed();
	}

	private String[] toStringArray(Collection<String> values) {
		// TODO Auto-generated method stub
		if(null == values || values.size() <= 0)
			return null;
		String[] strArr = values.toArray(new String[0]);
		return strArr;

	}

	ProgressDialog progressDialog = null;
	private void initData() {
		// TODO Auto-generated method stub
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
			return;
		}

		progressDialog = ProgressDialog.show(this, "烧苗图片",null);

		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver cr = SelectPhotoActivity.this.getContentResolver();
				Cursor cs = cr.query(uri, null, MediaStore.Images.Media.MIME_TYPE+" =? or "+MediaStore.Images.Media.MIME_TYPE+" =?", new String[]{"image/jpeg","image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
				Set<String> mDirPaths = new HashSet<String>();
				while(cs.moveToNext()) {
					String path = cs.getString(cs.getColumnIndex(MediaStore.Images.Media.DATA));
					//常规过滤
					if(null == path || path.isEmpty())
						continue;

					File parentFile = new File(path).getParentFile();
					//
					if(null == parentFile)
						continue;

					String dirPath = parentFile.getAbsolutePath();
					//已经保存
					if(mDirPaths.contains(dirPath))
						continue;
					//有些需要权限的文件不允许访问（很奇怪）。
					if(parentFile.list() == null)
						continue;

					//合法并且允许范文的path

					FolderBean floBean = null;

					floBean = new FolderBean();
					floBean.dir = dirPath;
					floBean.firstImgPath = path;

					floBean.imageFileNames = parentFile.list(new FilenameFilter() {

						@Override
						public boolean accept(File dir, String filename) {
							// TODO Auto-generated method stub
							if(filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png"))
								return true;
							return false;
						}
					});

					//最后一次过滤
					if(null == floBean.imageFileNames || floBean.imageFileNames.length <= 0)
						continue;

					//
					folderBeans.add(floBean);
					mDirPaths.add(dirPath);
				}
				cs.close();
				//
				int count = folderBeans.size();
				String[] names = null;
				String parentPath = null;
				String path = null;
				HashMap<String, String> data = new HashMap<String, String>();
				for (int i = 0; i < count; i++) {
					FolderBean folderBean = folderBeans.get(i);
					names = folderBean.imageFileNames;
					parentPath = folderBean.dir;



					for (String FileName : names) {
						path =  parentPath+"/"+FileName;
						paths.add(path);
						data.put(path, path);
					}
				}

				handler.sendEmptyMessage(1);
			}

		}.start();

	}

	class ViewHolder {
		public ImageView imageView;
		public CheckBox checkBox;
	}

	class FolderBean{
		public String dir;
		public String firstImgPath;
		public String[] imageFileNames;
		public int size;
	}
}
