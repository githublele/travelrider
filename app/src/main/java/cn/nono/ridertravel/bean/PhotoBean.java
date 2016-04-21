package cn.nono.ridertravel.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class PhotoBean implements Parcelable{

	public static final int ONLY_PHOTO = 0;
	public static final int ONLY_DESCRIPTION = 1;
	public static final int PHOTO_AND_DESCRIPTION = 2;

	public String path;
	public String description;


	public int getType() {
		if(null != path && null != description) {
			return PHOTO_AND_DESCRIPTION;
		}

		if (null == path && null != description) {
			return ONLY_DESCRIPTION;
		}

		if (null != path && null == description) {
			return ONLY_PHOTO;
		}

		return -1;
	}

	public PhotoBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(path);
		dest.writeString(description);
	}
	
	private PhotoBean(Parcel in) {
		path = in.readString();
		description = in.readString();
    }

	public static final Parcelable.Creator<PhotoBean> CREATOR = new Parcelable.Creator<PhotoBean>() {
		public PhotoBean createFromParcel(Parcel in) {
		    return new PhotoBean(in);
		}
		
		public PhotoBean[] newArray(int size) {
		    return new PhotoBean[size];
		}
	};

}
