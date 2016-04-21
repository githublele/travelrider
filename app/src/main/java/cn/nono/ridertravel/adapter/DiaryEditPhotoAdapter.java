package cn.nono.ridertravel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.bean.DiarySheetBean;
import cn.nono.ridertravel.bean.PhotoBean;
import cn.nono.ridertravel.util.ImageLoader;

public class DiaryEditPhotoAdapter extends BaseAdapter {

	private DiarySheetBean mDiary = null;
	private List<PhotoBean> photos = null;
	private LayoutInflater inflater = null;
	private int lastPosition = 0;
		
	public DiaryEditPhotoAdapter(DiarySheetBean mDiary,Context context) {
		super();
		this.mDiary = mDiary;
		photos = mDiary.photos;
		inflater = LayoutInflater.from(context);
	}

	public void addPhotos(List<String> photoPaths) {
		if(null == photoPaths || photoPaths.size() <= 0)
			return;
		int length = photoPaths.size();
		PhotoBean photoBean = null;
		for (int i = 0; i < length; i++) {
			photoBean = new PhotoBean();
			photoBean.path = photoPaths.get(i);
			this.photos.add(photoBean);
		}
		
		lastPosition = lastPosition + length;
	}
	
	public void addPhoto(String photoPath) {
		if(null == photoPath || photoPath.isEmpty())
			return;
		PhotoBean photoBean = new PhotoBean();
		photoBean.path = photoPath;
		photos.add(photoBean);
		lastPosition = lastPosition + 1;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return photos.size()+1;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(position >= lastPosition)
			return null;
		return photos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if(null == convertView) {
			convertView =  inflater.inflate(R.layout.item_diary_photo_show, null);
			viewHolder = new ViewHolder();
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.photo_imageview);
			if(position == lastPosition)
				viewHolder.isPhoto = false;
			convertView.setTag(viewHolder);
		} 
		viewHolder = (ViewHolder) convertView.getTag();		
		if(position != lastPosition) {
			ImageLoader.getInstance().loadImage(this.photos.get(position).path, viewHolder.imageView);
			viewHolder.isPhoto = true;
		} else {
			viewHolder.imageView.setBackgroundResource(R.mipmap.camera);
			viewHolder.isPhoto = false;
		}		
		return convertView;
	}

	public class ViewHolder {
		public ImageView imageView = null;
		public boolean isPhoto = true;
	}
	
	public DiarySheetBean getDiaryBean() {
		return mDiary;
	}
}
