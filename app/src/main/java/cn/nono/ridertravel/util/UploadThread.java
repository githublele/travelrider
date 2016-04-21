package cn.nono.ridertravel.util;

import android.os.Handler;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;

import java.util.ArrayList;
import java.util.List;

import cn.nono.ridertravel.bean.DiaryBean;
import cn.nono.ridertravel.bean.DiarySheetBean;
import cn.nono.ridertravel.bean.PhotoBean;
import cn.nono.ridertravel.bean.av.AVTravelDiary;
import cn.nono.ridertravel.bean.av.AVTravelDiaryContent;

/**
 * Created by Administrator on 2016/4/12.
 */
public class UploadThread extends Thread {

    public interface  SaveCallBack {
            public void succeed(Handler handler);
            public void error(Handler handler,AVException e);
    }

    SaveCallBack callBack = null;
    Handler handler;
    DiaryBean diary;
    AVUser currentUser;

    public UploadThread(DiaryBean diary, AVUser currentUser,Handler handler,SaveCallBack saveCallBack) {
        super();
        this.diary = diary;
        this.currentUser = currentUser;
        this.handler = handler;
        this.callBack = saveCallBack;
    }
    @Override
    public void run() {

        AVTravelDiary avTravelDiary = new AVTravelDiary();

        AVFile cover = new AVFile("photo", ImageUtil.autoCompressImage2byte(diary.coverImagePath));
        avTravelDiary.setCover(cover);
        avTravelDiary.setDays(diary.days);
        avTravelDiary.setHeadline(diary.diaryTitle);
        avTravelDiary.setPlace(diary.location);
        avTravelDiary.setTravelStartDate(diary.startDateStr);
        avTravelDiary.setTravelStartDate(diary.endDateStr);
        avTravelDiary.setAuthorPointer(currentUser);
        AVRelation<AVTravelDiaryContent> diaryContentAVRelation = avTravelDiary.getDiaryContents();

        //日记内容获取
        List<DiarySheetBean> sheetBeans = diary.diarySheetBeans;

        int photosCount = 0;
        List<PhotoBean> photosBeans;
        PhotoBean photoBean;
        String photoPath;
        String description;
        AVFile avFile;
        AVTravelDiaryContent avTravelDiaryContent;
        DiarySheetBean diarySheet;

        int length = sheetBeans.size();

        //保存已经保存成功的内容.(用作回滚)
        List<AVTravelDiaryContent> savedContents = new ArrayList<AVTravelDiaryContent>();
        try {
            for (int i = 0; i < length; i++) {
                diarySheet = sheetBeans.get(i);
                photosBeans = diarySheet.photos;
                if (null == photosBeans || photosBeans.size() <= 0)
                    continue;
                //章节头
                avTravelDiaryContent = AVTravelDiaryContent.createChartContent(diarySheet.year + "年" + (diarySheet.monthOfYear + 1) + "月" + diarySheet.dayOfMonth + "日");
                avTravelDiaryContent.save();
                savedContents.add(avTravelDiaryContent);
                diaryContentAVRelation.add(avTravelDiaryContent);
                avTravelDiaryContent = null;
                //章节详细图文内容
                photosCount = photosBeans.size();
                for (int j = 0 ; j < photosCount ; j++) {

                    photoBean = photosBeans.get(j);
                    photoPath = photoBean.path;
                    description = photoBean.description;
                    if (PhotoBean.ONLY_PHOTO == photoBean.getType()) {
                        avTravelDiaryContent = AVTravelDiaryContent.createOnlyPhotoContent(new AVFile("photo",ImageUtil.autoCompressImage2byte(photoPath)));
                    }
                    else if (PhotoBean.ONLY_DESCRIPTION == photoBean.getType()) {
                        avTravelDiaryContent = AVTravelDiaryContent.createOnlyDescriptionContent(description);
                    }
                    else if (PhotoBean.PHOTO_AND_DESCRIPTION == photoBean.getType()) {
                        avTravelDiaryContent = AVTravelDiaryContent.createPhotoAndDescriptionContent(new AVFile("photo",ImageUtil.autoCompressImage2byte(photoPath)),description);
                    } else {
                        continue;
                    }

                    if(null != avTravelDiaryContent) {
                        avTravelDiaryContent.save();
                        savedContents.add(avTravelDiaryContent);
                        diaryContentAVRelation.add(avTravelDiaryContent);
                        avTravelDiaryContent = null;
                    }
                }
        }

            avTravelDiary.save();
            if (null != callBack)
                callBack.succeed(handler);

    }catch(AVException e){
        int saveSize = savedContents.size();
        if(saveSize > 0) {
            AVTravelDiaryContent cont;
            for (int i = 0; i < saveSize; i++) {
                cont = savedContents.get(i);
                cont.deleteInBackground();
            }
        }
        if (null != callBack)
            callBack.error(handler,e);
        }

    }

}