package cn.nono.ridertravel.bean.av;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;

/**
 * Created by Administrator on 2016/4/13.
 */
@AVClassName("_User")
public class AVMUser extends AVUser{

    private final static String JOINED_ACTIVITY_RELATION_KEY = "TravelActivitys";
    private final static String TRAVEL_MAP_PATH_KEY = "TravelMapPaths";
    private final static String CREATE_ACTIVITY_RELATION_KEY = "CreateTravelActivitys";

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
