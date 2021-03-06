package cn.nono.ridertravel.bean.av;

import android.annotation.SuppressLint;

import com.avos.avoscloud.AVACL;
import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;

/**
 * Created by Administrator on 2016/4/26.
 */
@SuppressLint("ParcelCreator")
@AVClassName("UserBaseInfo")
public class AVBaseUserInfo extends AVObject {
    public final static String NICKNAME_KEY = "nickName";
//    public final static String USER_POINTER_KEY = "userPointer";
    public final static String SEX_KEY = "sex";
    public final static String SIGNATURE_KEY = "signature";

    public final static String SEX_MAN = "man";
    public final static String SEX_WOMAN = "woman";

    public final static String HEAD_KEY = "head";

    public AVBaseUserInfo() {
        super();
        setSexMan();
        AVACL avacl = new AVACL();
        avacl.setPublicReadAccess(true);
        avacl.setPublicWriteAccess(true);
        setACL(avacl);
    }

    public void setHead(AVFile headFile) {
        put(HEAD_KEY,headFile);
    }

    public AVFile getHead() {
        return getAVFile(HEAD_KEY);
    }

    public void setSignature(String signature) {
        put(SIGNATURE_KEY,signature);
    }

    public String getSignature() {
        return getString(SIGNATURE_KEY);
    }


    public void setNickName(String nickName) {
        put(NICKNAME_KEY,nickName);
    }

    public String getNickname(){
        return getString(NICKNAME_KEY);
    }

    public void setSex(String sex) {
        put(SEX_KEY,sex);
    }
    public void setSexWoman() {
        put(SEX_KEY,SEX_WOMAN);
    }
    public void setSexMan() {
        put(SEX_KEY,SEX_MAN);
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
