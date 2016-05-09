package cn.nono.ridertravel.bean.av;

import android.annotation.SuppressLint;

import com.avos.avoscloud.AVACL;
import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;

/**
 * Created by Administrator on 2016/4/14.
 */
@SuppressLint("ParcelCreator")
@AVClassName("TravelDiaryContent")
public class AVTravelDiaryContent extends AVObject{

    public static final int CONTENT_TYPE_CHAPTER = 1;
    public static final int CONTENT_TYPE_CONTENT_ONLY_PHOTO = 20;
    public static final int CONTENT_TYPE_CONTENT_ONLY_DESCRIPTION = 21;
    public static final int CONTENT_TYPE_CONTENT_PHOTO_AND_DECRIPTION = 22;


    public AVTravelDiaryContent() {
        super();
        AVACL avacl = new AVACL();
        avacl.setPublicReadAccess(true);
        avacl.setPublicWriteAccess(true);
        setACL(avacl);
    }

    public static final String CONTENT_TYPE_KEY = "contentType";
    //日记详细内容（图文）
    public static final String PHOTO_KEY = "photo";
    public static final String DESCRIPTION_KEY = "description";
    //章节特有字段
    public static final String DATE_KEY = "date";

    public String getDescription() {
        return getString(DESCRIPTION_KEY);
    }

    public void setDescription(String description) {
        put(DESCRIPTION_KEY,description);
    }

    public AVFile getPhoto() {
        return getAVFile(PHOTO_KEY);
    }

    public void setPhoto(AVFile photo) {
        put(PHOTO_KEY,photo);
    }

    public Integer getContentType() {
        return getInt(CONTENT_TYPE_KEY);
    }

    public void setContentType(Integer contentType) {
        put(CONTENT_TYPE_KEY,contentType);
    }

    public String getDate() {
        return getString(DATE_KEY);
    }

    public void setDate(String date) {
        put(DATE_KEY,date);
    }

    private static AVTravelDiaryContent createContent(int type,AVFile photo,String description,String date) {
        AVTravelDiaryContent avTravelDiaryContent = new AVTravelDiaryContent();
        avTravelDiaryContent.setContentType(type);
        if(null != photo && (type == CONTENT_TYPE_CONTENT_ONLY_PHOTO || type == CONTENT_TYPE_CONTENT_PHOTO_AND_DECRIPTION))
            avTravelDiaryContent.setPhoto(photo);
        if(description != null && (type == CONTENT_TYPE_CONTENT_ONLY_DESCRIPTION || type == CONTENT_TYPE_CONTENT_PHOTO_AND_DECRIPTION))
            avTravelDiaryContent.setDescription(description);
        if(date != null && type == CONTENT_TYPE_CHAPTER)
            avTravelDiaryContent.setDate(date);
        return avTravelDiaryContent;
    }

    public static AVTravelDiaryContent createChartContent(String date) {
        return createContent(CONTENT_TYPE_CHAPTER,null,null,date);
    }

    public static AVTravelDiaryContent createOnlyDescriptionContent(String description) {
        return createContent(CONTENT_TYPE_CONTENT_ONLY_DESCRIPTION,null,description,null);
    }

    public static AVTravelDiaryContent createOnlyPhotoContent(AVFile photo) {
        return createContent(CONTENT_TYPE_CONTENT_ONLY_PHOTO,photo,null,null);
    }

    public static AVTravelDiaryContent createPhotoAndDescriptionContent(AVFile photo,String description) {
        return createContent(CONTENT_TYPE_CONTENT_PHOTO_AND_DECRIPTION,photo,description,null);
    }


}
