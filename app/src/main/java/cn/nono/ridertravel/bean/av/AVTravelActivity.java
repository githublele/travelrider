package cn.nono.ridertravel.bean.av;

import android.annotation.SuppressLint;

import com.avos.avoscloud.AVACL;
import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVRelation;

/**
 * Created by Administrator on 2016/4/15.
 */
@SuppressLint("ParcelCreator")
@AVClassName("TravelActivity")
public class AVTravelActivity extends AVObject{

    private final static String HEADLINE_KEY = "headline";
    private final static String INTRODUCE_KEY = "introduce";
    private final static String FALL_IN_PLACE_KEY = "fallInPlace";
    private final static String FALL_IN_PLACE_LAT_KEY = "fallInPlaceLat";
    private final static String FALL_IN_PLACE_LNG_KEY = "fallInPlaceLng";


    private final static String MAX_MEMBER_SIZE_KEY = "maxMemberSize";
    private final static String START_DATE_MILLISTIME_KEY = "startDateMillisTime";
    private final static String END_DATE_MILLISTIME_KEY = "endDateMillisTime";
    private final static String REGISTRATION_DEADLINE_KEY = "registrationDeadlineMillisTime";
    private final static String PHONE_KEY = "phone";
    private final static String ACTIVITY_STATE_KEY = "activityState";
    /**
     * 参加的人
     */
    public final static String PARTICIPATORS_BASE_INFO_RELATIONS_KEY = "participatorsBaseInfo";
    public final static String COMMEMT_RELATIONS_KEY = "commemts";

    public final static String ISSUER_POINTER_KEY ="issuer";
    public final static String ISSUER_BASE_INFO_POINTER_KEY ="issuerBaseInfoPointer";




    private final  static String TAVLE_MAP_PATH_KEY = "travelMapPath";

    public final static int ACTIVITY_STATE_DEFAULT = 0;
    public final static int ACTIVITY_STATE_WORKING = 10;
    public final static int ACTIVITY_STATE_FINISH = 11;

//
//    String headline;
//    String city;
//    Integer maxMemberSize;
//    Long    startDateMillisTime;
//    Long    endDateMillisTime;
//    String  phone;
//    Integer activityState;

    public AVTravelActivity() {
        super();
        AVACL avacl = new AVACL();
        avacl.setPublicWriteAccess(true);
        avacl.setPublicReadAccess(true);
        setACL(avacl);
    }

    public AVBaseUserInfo getIsserBaseInfo() {
        return getAVObject(ISSUER_BASE_INFO_POINTER_KEY);
    }

    public void setBaseUserInfo(AVBaseUserInfo avBaseUserInfo) {
         put(ISSUER_BASE_INFO_POINTER_KEY,avBaseUserInfo);
    }

    public AVMUser getIssuer() {
            return getAVObject(ISSUER_POINTER_KEY);
    }

    public void setIssuer(AVMUser avmUser) {
        put(ISSUER_POINTER_KEY,avmUser);
    }

    public AVTravelMapPath getMapPath() {
        return getAVObject(TAVLE_MAP_PATH_KEY);
    }

    public void setTravelMapPath(AVTravelMapPath path) {
        put(TAVLE_MAP_PATH_KEY,path);
    }


    public void setRegistrationDeadline(Long deadlineTime) {
        put(REGISTRATION_DEADLINE_KEY,deadlineTime);
    }

    public Long getRegistrationDeadline() {
        return getLong(REGISTRATION_DEADLINE_KEY);
    }



    public AVRelation<AVBaseUserInfo> getParticipatorsBaseInfo() {
        return getRelation(PARTICIPATORS_BASE_INFO_RELATIONS_KEY);
    }

    public Integer getActivityState() {
        return getInt(ACTIVITY_STATE_KEY);
    }

    public void setActivityState(Integer activityState) {
       put(ACTIVITY_STATE_KEY,activityState);
    }



    public String getFallInPlace() {
        return getString(FALL_IN_PLACE_KEY);
    }

    public void setFallInPlace(String addr) {
        put(FALL_IN_PLACE_KEY,addr);
    }

    public Double getFallInPlaceLat() {
        return getDouble(FALL_IN_PLACE_LAT_KEY);
    }

    public void setFallInPlaceLat(Double addrLat) {
        put(FALL_IN_PLACE_LAT_KEY,addrLat);
    }

    public Double getFallInPlaceLng() {
        return getDouble(FALL_IN_PLACE_LNG_KEY);
    }

    public void setFallInPlaceLng(Double addrLng) {
        put(FALL_IN_PLACE_LNG_KEY,addrLng);
    }


    public String getHeadline() {
       return getString(HEADLINE_KEY);
    }

    public void setHeadline(String headline) {
        put(HEADLINE_KEY,headline);
    }

    public Integer getMaxMemberSize() {
        return getInt(MAX_MEMBER_SIZE_KEY);
    }

    public void setMaxMemberSize(Integer maxMemberSize) {
        put(MAX_MEMBER_SIZE_KEY,maxMemberSize);
    }

    public String getPhone() {
        return getString(PHONE_KEY);
    }

    public void setPhone(String phone) {
        put(PHONE_KEY,phone);
    }

    public Long getEndDateMillisTime() {
        return getLong(END_DATE_MILLISTIME_KEY);
    }

    public void setEndDateMillisTime(Long endDateMillisTime) {
       put(END_DATE_MILLISTIME_KEY,endDateMillisTime);
    }

    public Long getStartDateMillisTime() {
        return getLong(START_DATE_MILLISTIME_KEY);
    }

    public void setStartDateMillisTime(Long startDateMillisTime) {
        put(START_DATE_MILLISTIME_KEY,startDateMillisTime);
    }


    public String getActivityIntroduce() {
        return getString(INTRODUCE_KEY);
    }

    public void setActivityIntroduce(String introduce) {
        put(INTRODUCE_KEY,introduce);
    }
}
