package cn.nono.ridertravel.ui.diary;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.List;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.RiderTravelApplication;
import cn.nono.ridertravel.bean.av.AVTravelDiary;
import cn.nono.ridertravel.bean.av.AVTravelDiaryContent;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;
import cn.nono.ridertravel.util.StringUtil;

/**
 * Created by Administrator on 2016/4/13.
 */
public class DiaryBrowseActivity extends BaseNoTitleActivity {


    // 声明RequestQueue
    private RequestQueue mRequestQueue;
    private com.android.volley.toolbox.ImageLoader mImageLoader;






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
                viewHolder.photoImageView = (NetworkImageView) convertView.findViewById(R.id.photo_network_imageview);
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
                    viewHolder.photoImageView.setDefaultImageResId(R.mipmap.ic_launcher);
                    viewHolder.photoImageView.setImageUrl(diaryContent.getPhoto().getUrl(),mImageLoader);
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
        public Button location_btn;
        public Button like_btn;


        public LinearLayout sheetLinearLayout;
        public TextView sheetDaySortTextView;
        public TextView sheetDateTextView;

        public LinearLayout photoLinearLayout;
        public NetworkImageView photoImageView;
        public TextView photoDescriptionTextView;
    }


    ProgressDialog progressDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        avTravelDiary = getIntent().getParcelableExtra("avTravelDiary");
        if(null == avTravelDiary)
            finish();
        setContentView(R.layout.activity_diary_browse);

        // 实例化请求队列
        mRequestQueue = Volley.newRequestQueue(this);
        // 实例化图片加载器
        mImageLoader = new com.android.volley.toolbox.ImageLoader(mRequestQueue, ((RiderTravelApplication)getApplication()).getDiskBitmapCache());

        layoutInflater = LayoutInflater.from(this);
        diaryContentListView = (ListView) findViewById(R.id.diary_content_listview);
        diaryContentListView.setAdapter(baseAdapter);

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
        mRequestQueue.stop();
        super.onDestroy();
    }
}
