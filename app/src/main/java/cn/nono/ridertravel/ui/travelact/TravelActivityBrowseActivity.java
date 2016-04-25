package cn.nono.ridertravel.ui.travelact;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.bean.av.AVMUser;
import cn.nono.ridertravel.bean.av.AVTravelActivity;
import cn.nono.ridertravel.bean.av.AVTravelMapPath;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.baidumap.MapPathActivity;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;
import cn.nono.ridertravel.util.SimpleDateUtil;

/**
 * Created by Administrator on 2016/4/22.
 */
public class TravelActivityBrowseActivity extends BaseNoTitleActivity implements View.OnClickListener {

    public static final String AV_TRAVEL_ACT_KEY = "AV_ACT";

    BaseAdapter mSomeUsersAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(TravelActivityBrowseActivity.this).inflate(android.R.layout.simple_list_item_1, null);
            TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
            textView.setText("xxx" + position);
            return convertView;
        }
    };

    BaseAdapter mSomeCommentAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            int count = 0;
            if(count <= 0)
                showNoCommemtTipsView();
            else
                hideNoCommemtTipsView();
            return count;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    };



    private ScrollView mScrollView;
    private ImageView mActThumbnailMapImageView;
    private TextView mActStateTextView;
    private TextView mCityTextView;
    private TextView mActStarDateTextView;
    private TextView mActEndDateTextView;
    private TextView mPhoneTextView;
    private TextView mIssuerTextView;
    private TextView mFallInPlaceTextView;
    private TextView mActIntroductionTextView;
    private GridView mSomeUserGridView;
    private ListView mSomeCommemtListView;
    private TextView mNoCommemtTipsTextView;

    private Button mBackBtn;
    private Button mJoinActBtn;
    private Button mAddCommemtBtn;

    private LinearLayout mBottomBtnsLinearLayout;

    private LinearLayout mCommemtLinearLayout;
    private EditText mCommemtEditText;
    private Button mUploadCommemtBtn;


    private AVTravelActivity mTravelAct;
    private AVMUser mUser = null;
    private boolean mNoCommemtTipsVisible = false;
    private boolean mCommemtLinearLayoutVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_browser);
        mTravelAct = getIntent().getParcelableExtra(AV_TRAVEL_ACT_KEY);
        if (mTravelAct == null) {
            finish();
            return;
        }
        initView();
        loadNetDate();

    }

    //加载其余的网络数据
    private void loadNetDate() {

    }

    private void initView() {

        mScrollView = (ScrollView) findViewById(R.id.content_scrollView);
        mScrollView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    hideCommemtEditView();
            }
        });
        
        mActThumbnailMapImageView = (ImageView) findViewById(R.id.map_thumbnail_imageview);
        mActThumbnailMapImageView.setOnClickListener(this);
        mActStateTextView = (TextView) findViewById(R.id.activity_state_tv);

        mCityTextView = (TextView) findViewById(R.id.city_tv);
        mCityTextView.setText("默认城市 T");

        mActStarDateTextView = (TextView) findViewById(R.id.activity_start_date_tv);
        mActStarDateTextView.setText(SimpleDateUtil.formatDate(SimpleDateUtil.YMDHMS,mTravelAct.getStartDateMillisTime()));
        mActEndDateTextView = (TextView) findViewById(R.id.activity_end_date_tv);
        mActEndDateTextView.setText(SimpleDateUtil.formatDate(SimpleDateUtil.YMDHMS,mTravelAct.getEndDateMillisTime()));

        mPhoneTextView = (TextView) findViewById(R.id.phone_tv);
        mPhoneTextView.setText(mTravelAct.getPhone());
        mIssuerTextView = (TextView) findViewById(R.id.issuer_tv);
        mFallInPlaceTextView = (TextView) findViewById(R.id.fall_in_place_tv);
        mFallInPlaceTextView.setText(mTravelAct.getFallInPlace());
        mActIntroductionTextView = (TextView) findViewById(R.id.activity_introduction_tv);
        mActIntroductionTextView.setText(mTravelAct.getActivityIntroduce());
        mSomeUserGridView = (GridView) findViewById(R.id.user_list_gridview);
        mSomeUserGridView.setAdapter(mSomeUsersAdapter);
        mNoCommemtTipsTextView = (TextView) findViewById(R.id.tips_no_commemt_tv);
        mSomeCommemtListView = (ListView) findViewById(R.id.commemt_listview);
        mSomeCommemtListView.setAdapter(mSomeCommentAdapter);
        mBackBtn = (Button) findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(this);
        mJoinActBtn = (Button) findViewById(R.id.join_activity_btn);
        mJoinActBtn.setOnClickListener(this);
        mAddCommemtBtn = (Button) findViewById(R.id.comment_btn);
        mAddCommemtBtn.setOnClickListener(this);

        mBottomBtnsLinearLayout = (LinearLayout) findViewById(R.id.bottom_btns_ll);
        mCommemtEditText = (EditText) findViewById(R.id.comment_edittext);
        mCommemtLinearLayout = (LinearLayout) findViewById(R.id.commemt_ll);

        mUploadCommemtBtn = (Button) findViewById(R.id.add_commemt_btn);
        mUploadCommemtBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.map_thumbnail_imageview:
                browseActivityTravelMapPath();
                break;
            case R.id.join_activity_btn:
                joinAct();
                break;
            case R.id.comment_btn:
                showCommemtEditView();
                break;
            case R.id.add_commemt_btn:
                uploadCommemt();
                break;
            default:
                break;
        }

    }

    private void uploadCommemt() {
    }


    private void showCommemtEditView() {
        if(mCommemtLinearLayoutVisible)
            return;

        mCommemtEditText.setText("");
        mBottomBtnsLinearLayout.setVisibility(View.GONE);
        mCommemtLinearLayout.setVisibility(View.VISIBLE);
        mCommemtLinearLayoutVisible = true;
    }

    private void hideCommemtEditView() {
        if (mCommemtLinearLayoutVisible) {
            mCommemtLinearLayout.setVisibility(View.GONE);
            mBottomBtnsLinearLayout.setVisibility(View.VISIBLE);
            mCommemtLinearLayoutVisible = false;
        }
    }

    private void showNoCommemtTipsView() {
      if(mNoCommemtTipsVisible)
          return;
        mNoCommemtTipsTextView.setVisibility(View.VISIBLE);
        mNoCommemtTipsVisible = true;
    }

    private void hideNoCommemtTipsView() {
        if(mNoCommemtTipsVisible) {
            mNoCommemtTipsTextView.setVisibility(View.GONE);
            mNoCommemtTipsVisible = false;
        }
    }

    private void joinAct() {
    }

    //防止程序没反应过来 导致多次触发.
    private boolean mMapClickCanClick = true;

    private void browseActivityTravelMapPath() {

        if (!mMapClickCanClick)
            return;
        mMapClickCanClick = false;

        AVQuery<AVTravelMapPath> mapPathQuery = AVQuery.getQuery(AVTravelMapPath.class);
        mapPathQuery.getInBackground(mTravelAct.getMapPath().getObjectId(), new GetCallback<AVTravelMapPath>() {
            @Override
            public void done(AVTravelMapPath avTravelMapPath, AVException e) {
                if (e != null) {
                    ToastUtil.toastLong(TravelActivityBrowseActivity.this, "网络异常!");
                    e.printStackTrace();
                    return;
                }

                if (null == avTravelMapPath) {
                    ToastUtil.toastLong(TravelActivityBrowseActivity.this, "服务器找不到数据!");
                    return;
                }

                List<AVGeoPoint> avGeoPoints = avTravelMapPath.getMapPathLatLngArr();
                if (null == avGeoPoints || avGeoPoints.size() <= 1) {
                    ToastUtil.toastLong(TravelActivityBrowseActivity.this, "服务器找不到数据!");
                    return;
                }

                int length = avGeoPoints.size();
                AVGeoPoint avGeo = null;
                ArrayList<LatLng> points = new ArrayList<LatLng>();
                for (int i = 0; i < length; i++) {
                    avGeo = avGeoPoints.get(i);
                    points.add(new LatLng(avGeo.getLatitude(), avGeo.getLongitude()));
                }

                Intent intent = new Intent(TravelActivityBrowseActivity.this, MapPathActivity.class);
                intent.putParcelableArrayListExtra(MapPathActivity.MAP_LATLNG_POINT_LIST_KEY, points);
                startActivity(intent);
                mMapClickCanClick = true;
            }
        });


    }

}
