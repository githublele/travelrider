package cn.nono.ridertravel.bean.av;

import android.annotation.SuppressLint;

import com.avos.avoscloud.AVACL;
import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

/**
 * Created by Administrator on 2016/4/15.
 */
@SuppressLint("ParcelCreator")
@AVClassName("Comment")
public class AVComment extends AVObject {


//    private final String _key = "";
    public final static String USER_KEY = "user";
    public final static String USER_BASE_INFO_KEY = "userBaseInfo";
    public final static String CONTENT_KEY = "content";
    public final static String TRAVELACTIVITY_KEY = "TravelActivity";


    public AVComment() {
        super();
        AVACL avacl = new AVACL();
        avacl.setPublicReadAccess(true);
        avacl.setPublicWriteAccess(true);
        setACL(avacl);
    }
    public AVBaseUserInfo getUserBaseInfo() {
        return getAVObject(USER_BASE_INFO_KEY);
    }

    public void setUserBaseInfo(AVBaseUserInfo avBaseUserInfo) {
        put(USER_BASE_INFO_KEY,avBaseUserInfo);
    }

    public AVTravelActivity getTravelActivity() {
        return getAVObject(TRAVELACTIVITY_KEY);
    }

    public void setTravelActivity(AVTravelActivity avTravelActivity) {
        put(TRAVELACTIVITY_KEY,avTravelActivity);
    }

    public AVMUser getUser() {
        return getAVObject(USER_KEY);
    }

    public void setUser(AVMUser user) {
        put(USER_KEY,user);
    }

    public String getContent() {
        return getString(CONTENT_KEY);
    }

    public void setContent(String content) {
        put(CONTENT_KEY,content);
    }
}
