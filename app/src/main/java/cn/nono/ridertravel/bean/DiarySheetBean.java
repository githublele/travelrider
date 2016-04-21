package cn.nono.ridertravel.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class DiarySheetBean implements Parcelable {
	
	public int year = -1;
	public int monthOfYear = -1;
	public int dayOfMonth = -1;
	public String location = null;
	public List<PhotoBean> photos = new ArrayList<PhotoBean>();
	
	public DiarySheetBean(int year, int monthOfYear, int dayOfMonth, String location) {
		super();
		this.year = year;
		this.monthOfYear = monthOfYear;
		this.dayOfMonth = dayOfMonth;
		this.location = location;
	}
	
	public DiarySheetBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void addPhoto(PhotoBean photo) {
		if(null == photo || null == photo.path || photo.path.isEmpty())
			return;
		this.photos.add(photo);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(year);
		dest.writeInt(dayOfMonth);
		dest.writeInt(monthOfYear);
		dest.writeString(location);
		dest.writeList(photos);
	}
	
	
	private DiarySheetBean(Parcel in) {
        year = in.readInt();
        dayOfMonth = in.readInt();
        monthOfYear = in.readInt();
        location = in.readString();
        in.readList(photos, this.getClass().getClassLoader());
    }

	public static final Parcelable.Creator<DiarySheetBean> CREATOR = new Parcelable.Creator<DiarySheetBean>() {
		public DiarySheetBean createFromParcel(Parcel in) {
		    return new DiarySheetBean(in);
		}
		
		public DiarySheetBean[] newArray(int size) {
		    return new DiarySheetBean[size];
		}
	};

}
