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

    public AVRelation<AVTravelActivity> getJoinedTravelActivitysRelation() {
        return  getRelation(JOINED_ACTIVITY_RELATION_KEY);
    }
}
