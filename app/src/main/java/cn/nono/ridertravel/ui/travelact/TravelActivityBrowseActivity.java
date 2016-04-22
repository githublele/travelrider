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
            return 0;
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
    private Button mBackBtn;
    private Button mJoinActBtn;
    private Button mAddCommemtBtn;

    private AVTravelActivity mTravelAct;
    private AVMUser mUser = null;
    private LinearLayout mBottomBtnsLinearLayout;
    private LinearLayout mCommemtLinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_browser);
//        mTravelAct = getIntent().getParcelableExtra(AV_TRAVEL_ACT_KEY);
//        if (mTravelAct == null) {
//            finish();
//            return;
//        }
        initView();

    }

    private void initView() {

        mActThumbnailMapImageView = (ImageView) findViewById(R.id.map_thumbnail_imageview);
        mActThumbnailMapImageView.setOnClickListener(this);
        mActStateTextView = (TextView) findViewById(R.id.activity_state_tv);
        mCityTextView = (TextView) findViewById(R.id.city_tv);
        mActStarDateTextView = (TextView) findViewById(R.id.activity_start_date_tv);
        mActEndDateTextView = (TextView) findViewById(R.id.activity_end_date_tv);
        mPhoneTextView = (TextView) findViewById(R.id.phone_tv);
        mIssuerTextView = (TextView) findViewById(R.id.issuer_tv);
        mFallInPlaceTextView = (TextView) findViewById(R.id.fall_in_place_tv);
        mActIntroductionTextView = (TextView) findViewById(R.id.activity_introduction_tv);
        mSomeUserGridView = (GridView) findViewById(R.id.user_list_gridview);
//        mSomeUserGridView.setAdapter(mSomeUsersAdapter);
        mSomeCommemtListView = (ListView) findViewById(R.id.commemt_listview);
//        mSomeCommemtListView.setAdapter(mSomeCommentAdapter);
        mBackBtn = (Button) findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(this);
        mJoinActBtn = (Button) findViewById(R.id.join_activity_btn);
        mJoinActBtn.setOnClickListener(this);
        mAddCommemtBtn = (Button) findViewById(R.id.comment_btn);
        mAddCommemtBtn.setOnClickListener(this);

        mBottomBtnsLinearLayout = (LinearLayout) findViewById(R.id.bottom_btns_ll);
        mCommemtLinearLayout = (LinearLayout) findViewById(R.id.commemt_ll);
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
                addCommemt();
                break;
            default:
                break;
        }

    }

    EditText commemtEditText;
    Button addBtn;
    View contentView = null;

    private void addCommemt() {
/*
        if (contentView == null) {
            contentView = LayoutInflater.from(this).inflate(R.layout.popu_window_add_commemt, null);
            commemtEditText = (EditText) contentView.findViewById(R.id.comment_edittext);
            addBtn = (Button) contentView.findViewById(R.id.add_commemt_btn);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.toastShort(TravelActivityBrowseActivity.this, commemtEditText.getText().toString());
                }
            });
        }

        PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("xx", "onTouch: ");
                return false;
            }
        });

        popupWindow.setBackgroundDrawable(new ColorDrawable(0xfff8f8f8));
        popupWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.BOTTOM,0,0);
   */

        mBottomBtnsLinearLayout.setVisibility(View.GONE);
        mCommemtLinearLayout.setVisibility(View.VISIBLE);
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
