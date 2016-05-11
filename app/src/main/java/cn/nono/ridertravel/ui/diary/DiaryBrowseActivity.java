package cn.nono.ridertravel.ui.diary;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.bean.av.AVBaseUserInfo;
import cn.nono.ridertravel.bean.av.AVMUser;
import cn.nono.ridertravel.bean.av.AVTravelDiary;
import cn.nono.ridertravel.bean.av.AVTravelDiaryContent;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;
import cn.nono.ridertravel.util.ImageLoaderOptionsSetting;
import cn.nono.ridertravel.util.StringUtil;

/**
 * Created by Administrator on 2016/4/13.
 */
public class DiaryBrowseActivity extends BaseNoTitleActivity {

    LayoutInflater layoutInflater = null;
    AVTravelDiary avTravelDiary;
    ListView diaryContentListView;
    List<AVTravelDiaryContent> diaryContents = new ArrayList<AVTravelDiaryContent>();

    BaseAdapter baseAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return diaryContents.size();
        }

        @Override
        public Object getItem(int position) {
            return diaryContents.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AVTravelDiaryContent diaryContent = diaryContents.get(position);
            ViewHolder viewHolder;
            if(null == convertView) {
                convertView = layoutInflater.inflate(R.layout.item_diary_browse, null);
                viewHolder = new ViewHolder();
                viewHolder.photoDescriptionTextView = (TextView) convertView.findViewById(R.id.photo_description_tv);
                viewHolder.photoImageView = (ImageView) convertView.findViewById(R.id.photo_imageview);
                viewHolder.photoLinearLayout = (LinearLayout) convertView.findViewById(R.id.diary_sheet_content_ll);

                viewHolder.sheetDateTextView = (TextView) convertView.findViewById(R.id.diary_sheet_date_tv);
                viewHolder.sheetDaySortTextView = (TextView) convertView.findViewById(R.id.date_sort_tv);
                viewHolder.sheetLinearLayout = (LinearLayout) convertView.findViewById(R.id.diary_sheet_ll);

                convertView.setTag(viewHolder);
            }

            viewHolder = (ViewHolder) convertView.getTag();
            int type = diaryContent.getContentType();
            if (AVTravelDiaryContent.CONTENT_TYPE_CHAPTER == type) {

                if( viewHolder.sheetLinearLayout.getVisibility() != View.VISIBLE)
                    viewHolder.sheetLinearLayout.setVisibility(View.VISIBLE);
                if( viewHolder.photoLinearLayout.getVisibility() != View.GONE)
                    viewHolder.photoLinearLayout.setVisibility(View.GONE);

                viewHolder.sheetDateTextView.setText(diaryContent.getDate());
            } else {

                if( viewHolder.sheetLinearLayout.getVisibility() != View.GONE)
                    viewHolder.sheetLinearLayout.setVisibility(View.GONE);
                if( viewHolder.photoLinearLayout.getVisibility() != View.VISIBLE)
                    viewHolder.photoLinearLayout.setVisibility(View.VISIBLE);

                if(!StringUtil.empty(diaryContent.getDescription())) {
                    viewHolder.photoDescriptionTextView.setText(diaryContent.getDescription());
                    if( viewHolder.photoDescriptionTextView.getVisibility() != View.VISIBLE)
                        viewHolder.photoDescriptionTextView.setVisibility(View.VISIBLE);
                } else {
                    if( viewHolder.photoDescriptionTextView.getVisibility() != View.GONE)
                    viewHolder.photoDescriptionTextView.setVisibility(View.GONE);
                }

                if(null != diaryContent.getPhoto()) {
                    ImageLoader.getInstance().displayImage(diaryContent.getPhoto().getUrl(),viewHolder.photoImageView,ImageLoaderOptionsSetting.getConstantImageLoaderDefaultOptions());
                    if( viewHolder.photoImageView.getVisibility() != View.VISIBLE)
                        viewHolder.photoImageView.setVisibility(View.VISIBLE);
                } else {
                    if( viewHolder.photoImageView.getVisibility() != View.GONE)
                        viewHolder.photoImageView.setVisibility(View.GONE);
                }

            }

            return convertView;

        }
    };

    class ViewHolder {
        public LinearLayout sheetLinearLayout;
        public TextView sheetDaySortTextView;
        public TextView sheetDateTextView;

        public LinearLayout photoLinearLayout;
        public ImageView photoImageView;
        public TextView photoDescriptionTextView;
    }




    ProgressDialog progressDialog = null;

    //没有登录的用户只能浏览游记。
    private AVMUser avmUser = null;

    //listView 头部的一些View
    private ImageView mAuthorHeadImageView;
    private ImageView mDiaryCoverImageView;

    private CheckBox mCollectCheckBox;
    //网上数据库 对于本游记 是否已经做了收藏
    private boolean mlastCollectStateOnNet = false;
    private CheckBox mPraiseCheckBox;
    //网上数据库 对于本游记 是否已经做了点赞
    private boolean mlastPraiseStateOnNet = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        avTravelDiary = getIntent().getParcelableExtra("avTravelDiary");
        if(null == avTravelDiary) {
            finish();
            return;
        }
        setContentView(R.layout.activity_diary_browse);

        initView();
        loadNetData();
    }

    private void initView() {
        mCollectCheckBox = (CheckBox) findViewById(R.id.collect_checkBox);
        mPraiseCheckBox = (CheckBox) findViewById(R.id.zan_checkBox);

        layoutInflater = LayoutInflater.from(this);
        diaryContentListView = (ListView) findViewById(R.id.diary_content_listview);
        //添加listview head　和　foot

        //添加listView 头部
        View headView = layoutInflater.inflate(R.layout.item_diary_browse_head,null);
        mAuthorHeadImageView = (ImageView) headView.findViewById(R.id.head_icon_imageview);
        mDiaryCoverImageView = (ImageView) headView.findViewById(R.id.diary_cover_imageview);


        //头部不可点击
        diaryContentListView.addHeaderView(headView,null,false);
        View footView = layoutInflater.inflate(R.layout.item_diary_browse_foot,null);
        diaryContentListView.addFooterView(footView,null,false);

        diaryContentListView.setAdapter(baseAdapter);
    }

    private void loadNetData() {

        //加载封面信息（填充listView head view）
        AVFile coverFile = avTravelDiary.getCover();
        if(null != coverFile) {
            ImageLoader.getInstance().displayImage(coverFile.getUrl(),mDiaryCoverImageView, ImageLoaderOptionsSetting.getConstantImageLoaderDefaultOptions());
        }

        AVBaseUserInfo avBaseUserInfo = avTravelDiary.getAuthorBaseInfo();
        if(null != avBaseUserInfo) {
            AVQuery.getQuery(AVBaseUserInfo.class).getInBackground(avBaseUserInfo.getObjectId(), new GetCallback<AVBaseUserInfo>() {
                @Override
                public void done(AVBaseUserInfo avBaseUserInfo, AVException e) {
                    if(null != e) {
                        ToastUtil.toastShort(DiaryBrowseActivity.this, "网络异常" + e.getCode());
                        e.printStackTrace();
                        return;
                    }
                    if(null != avBaseUserInfo) {
                        AVFile headFile = avBaseUserInfo.getHead();
                        if(null != headFile) {
                            ImageLoader.getInstance().displayImage(headFile.getUrl(),mAuthorHeadImageView,ImageLoaderOptionsSetting.getConstantImageLoaderDefaultOptions());
                        }
                    }
                }
            });
        }


        //加载 收藏 + 点赞 信息（登录用户）
        avmUser = (AVMUser) AVUser.getCurrentUser();
        if(null != avmUser) {
            AVQuery<AVMUser> queryCollect = avTravelDiary.getCollectedUsers().getQuery();
            queryCollect.whereEqualTo(AVObject.OBJECT_ID,avmUser.getObjectId());
            queryCollect.getFirstInBackground(new GetCallback<AVMUser>() {
                @Override
                public void done(AVMUser avmUser, AVException e) {
                    if(null != e && e.getCode() != AVException.OBJECT_NOT_FOUND) {
                        ToastUtil.toastShort(DiaryBrowseActivity.this,"网络数据获取异常"+e.getCode());
                        e.printStackTrace();
                        return;
                    }

                    //没有收藏
                    if (null == avmUser) {
                        mlastCollectStateOnNet = false;
                        mCollectCheckBox.setChecked(false);
                    } else {
                        mlastCollectStateOnNet = true;
                        mCollectCheckBox.setChecked(true);
                    }
                }
            });


            AVQuery<AVMUser> queryPraise = avTravelDiary.getPraiseUsers().getQuery();
            queryPraise.whereEqualTo(AVObject.OBJECT_ID,avmUser.getObjectId());

            queryPraise.getFirstInBackground(new GetCallback<AVMUser>() {
                @Override
                public void done(AVMUser avmUser, AVException e) {
                    if(null != e && e.getCode() != AVException.OBJECT_NOT_FOUND) {
                        ToastUtil.toastShort(DiaryBrowseActivity.this,"网络数据获取异常"+e.getCode());
                        e.printStackTrace();
                        return;
                    }

                    //没有点赞
                    if (null == avmUser) {
                        mlastPraiseStateOnNet = false;
                        mPraiseCheckBox.setChecked(false);
                    } else {
                        mlastPraiseStateOnNet = true;
                        mPraiseCheckBox.setChecked(true);
                    }
                }
            });

        }

        //加载游记具体信息。
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("加载中。。。。");
        progressDialog.setCancelable(false);
        progressDialog.show();

        avTravelDiary.getDiaryContents().getQuery().findInBackground(new FindCallback<AVTravelDiaryContent>() {
            @Override
            public void done(List<AVTravelDiaryContent> list, AVException e) {

                if(null != progressDialog) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }

                if (null == e) {
                    if(null != list && list.size() > 0) {
                        diaryContents.clear();
                        diaryContents.addAll(list);
                        baseAdapter.notifyDataSetInvalidated();
                    }

                } else {
                    e.printStackTrace();
                    ToastUtil.toastShort(DiaryBrowseActivity.this,"拉取数据失败。error");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (null != avmUser) {
//由于没有事务！！！！！所以多表操作出现异常无法回滚！！！  期待云代码实现多表操作
            try {
                AVTravelDiary avDiary = AVObject.createWithoutData(AVTravelDiary.class,avTravelDiary.getObjectId());

                Integer a = -1;
                if (mlastPraiseStateOnNet != mPraiseCheckBox.isChecked()) {
                    if(mPraiseCheckBox.isChecked()) {
                        avDiary.getPraiseUsers().add(avmUser);
                        avDiary.increment(AVTravelDiary.PRAISE_TIMES_KEY);

                    } else {
                        avDiary.getPraiseUsers().remove(avmUser);
                        avDiary.increment(AVTravelDiary.PRAISE_TIMES_KEY,a);
                    }
                    avDiary.saveInBackground();

                }

                if (mlastCollectStateOnNet != mCollectCheckBox.isChecked()) {
                    if(mCollectCheckBox.isChecked()) {
                        avDiary.getCollectedUsers().add(avmUser);
                        avDiary.increment(AVTravelDiary.COLLECTED_TIMES);
                        avmUser.getCollectionDiariesRelation().add(avDiary);
                    } else {
                        avDiary.getCollectedUsers().remove(avmUser);
                        avDiary.increment(AVTravelDiary.COLLECTED_TIMES,a);
                        avmUser.getCollectionDiariesRelation().remove(avDiary);
                    }
                    avDiary.saveInBackground();
                    avmUser.saveInBackground();
                }


            } catch (AVException e) {
                e.printStackTrace();
            }

        }
        super.onDestroy();
    }
}
