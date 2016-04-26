package cn.nono.ridertravel.bean.av;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

/**
 * Created by Administrator on 2016/4/26.
 */
@AVClassName("UserBaseInfo")
public class AVBaseUserInfo extends AVObject {
    public final static String NICKNAME_KEY = "nickName";
//    public final static String USER_POINTER_KEY = "userPointer";
    public final static String SEX_KEY = "sex";

    public void setNickName(String nickName) {
        put(NICKNAME_KEY,nickName);
    }

    public String getNickname(){
        return getString(NICKNAME_KEY);
    }

    public void setSex(String sex) {
        put(SEX_KEY,sex);
    }

    public String getSex(){
        return getString(SEX_KEY);
    }

//    public AVMUser getUser() {
//        return getAVObject(USER_POINTER_KEY);
//    }
//
//    public void setUser(AVMUser avmUser) {
//        put(USER_POINTER_KEY,avmUser);
//    }



}
