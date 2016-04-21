package cn.nono.ridertravel.bean.av;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;

/**
 * Created by Administrator on 2016/4/12.
 */
@AVClassName("TravelDiary")
public class AVTravelDiary extends AVObject{
    public static final String HEAD_LINE_KEY = "headline";
    public static final String PLACE_KEY = "place";
    public static final String TRAVEL_END_DATE_KEY = "travelEndDate";
    public static final String TRAVEL_START_DATE_KEY = "travelStartDate";
    public static final String COVER_KEY = "cover";
    public static final String DAYS_KEY = "days";
    public static final String PRAISE_TIMES_KEY = "praiseTimes";
    public static final String AUTHOR_KEY = "authorPointer";
    public static final String DIARYCONTENT_RELATIONS_KEY = "diaryContents";

//    private final String _key = "";


    public AVRelation<AVTravelDiaryContent> getDiaryContents() {
        return getRelation(DIARYCONTENT_RELATIONS_KEY);
    }

    public void setDiaryItems(AVRelation<AVTravelDiaryContent> diaryContents) {
        put(DIARYCONTENT_RELATIONS_KEY,diaryContents);
    }

    public String getHeadline() {
        return getString(HEAD_LINE_KEY);
    }

    public void setHeadline(String headline) {
       put(HEAD_LINE_KEY,headline);
    }

    public String getPlace() {
        return getString(PLACE_KEY);
    }

    public void setPlace(String place) {
       put(PLACE_KEY,place);
    }

    public String getTravelEndDate() {
        return getString(TRAVEL_END_DATE_KEY);
    }

    public void setTravelEndDate(String travelEndDate) {
       put(TRAVEL_END_DATE_KEY,travelEndDate);
    }

    public String getTravelStartDate() {
        return getString(TRAVEL_START_DATE_KEY);
    }

    public void setTravelStartDate(String travelStartDate) {
        put(TRAVEL_START_DATE_KEY,travelStartDate);
    }

    public AVFile getCover() {
        return getAVFile(COVER_KEY);
    }

    public void setCover(AVFile cover) {
        put(COVER_KEY,cover);
    }

    public Integer getDays() {
       return  getInt(DAYS_KEY);
    }

    public void setDays(Integer days) {
        put(DAYS_KEY,days);
    }

    public Integer getPraiseTimes() {
        return getInt(PRAISE_TIMES_KEY);
    }

    public AVUser getAuthorPointer() {
        return getAVUser(AUTHOR_KEY);
    }

    public void setAuthorPointer(AVUser authorPointer) {
        put(AUTHOR_KEY,authorPointer);
    }
}
