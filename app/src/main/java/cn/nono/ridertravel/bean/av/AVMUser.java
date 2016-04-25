package cn.nono.ridertravel.bean.av;

import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;

/**
 * Created by Administrator on 2016/4/13.
 */
public class AVMUser extends AVUser{

    public final static String JOINED_ACTIVITY_RELATION_KEY = "TravelActivitys";
    public final static String TRAVEL_MAP_PATH_KEY = "TravelMapPaths";
    public final static String CREATE_ACTIVITY_RELATION_KEY = "CreateTravelActivitys";
    public final static String BASE_INFO_KEY = "baseInfoPointer";


    public AVBaseUserInfo getBaseInfo() {
        return getAVObject(BASE_INFO_KEY);
    }

    public void setBaseInfo(AVBaseUserInfo baseInfo) {
        put(BASE_INFO_KEY,baseInfo);
    }


    public  AVRelation<AVTravelActivity> getCreateTravelActivitysRelation() {
        return getRelation(CREATE_ACTIVITY_RELATION_KEY);
    }

    public AVRelation<AVTravelActivity> getJoinedTravelActivitysRelation() {
        return  getRelation(JOINED_ACTIVITY_RELATION_KEY);
    }

    public AVRelation<AVTravelMapPath> getTravelMapPathRelation() {
        return  getRelation(TRAVEL_MAP_PATH_KEY);
    }


}
