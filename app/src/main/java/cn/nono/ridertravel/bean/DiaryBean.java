package cn.nono.ridertravel.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class DiaryBean implements Parcelable{

	public String diaryTitle;
	public String location;
	public String startDateStr;
	public String endDateStr;
	public int photoCount = 0;
	public String coverImagePath = null;
	public List<DiarySheetBean> diarySheetBeans = new ArrayList<DiarySheetBean>();
	public int days = 0;

	public void addSheet(DiarySheetBean sheet){
		diarySheetBeans.add(sheet);
	}
	
	public DiaryBean() {
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
		dest.writeString(diaryTitle);
		dest.writeString(location);
		dest.writeString(startDateStr);
		dest.writeString(endDateStr);
		dest.writeInt(photoCount);
		dest.writeString(coverImagePath);
		dest.writeList(diarySheetBeans);
		dest.writeInt(days);
	}

	 private DiaryBean(Parcel in) {
         diaryTitle = in.readString();
         location = in.readString();
         startDateStr = in.readString();
         endDateStr = in.readString();
         photoCount = in.readInt();
         coverImagePath = in.readString();
         in.readList(diarySheetBeans, this.getClass().getClassLoader());
		 days = in.readInt();
     }

	 public static final Parcelable.Creator<DiaryBean> CREATOR = new Parcelable.Creator<DiaryBean>() {
		 public DiaryBean createFromParcel(Parcel in) {
		     return new DiaryBean(in);
		 }
		
		 public DiaryBean[] newArray(int size) {
		     return new DiaryBean[size];
		 }
	 };

	
}
