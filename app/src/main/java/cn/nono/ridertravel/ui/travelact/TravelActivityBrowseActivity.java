package cn.nono.ridertravel.ui.travelact;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.avos.avoscloud.AVACL;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.bean.av.AVBaseUserInfo;
import cn.nono.ridertravel.bean.av.AVComment;
import cn.nono.ridertravel.bean.av.AVMUser;
import cn.nono.ridertravel.bean.av.AVTravelActivity;
import cn.nono.ridertravel.bean.av.AVTravelMapPath;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.baidumap.MapPathActivity;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;
import cn.nono.ridertravel.util.DataSimpleGetUtil;
import cn.nono.ridertravel.util.SimpleDateUtil;

/**
 * Created by Administrator on 2016/4/22.
 */
public class TravelActivityBrowseActivity extends BaseNoTitleActivity implements View.OnClickListener {

    public static final String AV_TRAVEL_ACT_KEY = "AV_ACT";

    //代表（仅仅获取部分参加人员信息）
    private List<AVBaseUserInfo> simpleJoinUsers = new ArrayList<AVBaseUserInfo>();
    private List<AVComment> simpleCommemts = new ArrayList<AVComment>();

    BaseAdapter mSomeUsersAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return simpleJoinUsers.size();
        }

        @Override
        public Object getItem(int position) {
            return simpleJoinUsers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AVBaseUserInfo avBaseUserInfo = simpleJoinUsers.get(position);
            ViewHolderSomeUser viewHolder;
            if(null == convertView) {
                viewHolder = new ViewHolderSomeUser();
                convertView = LayoutInflater.from(TravelActivityBrowseActivity.this).inflate(R.layout.item_participators_list, null);
                viewHolder.headIconImgeView = (ImageView) convertView.findViewById(R.id.head_icon_imageview);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolderSomeUser) convertView.getTag();
            //头像填充
//            ....


            return convertView;
        }
    };
    private LinearLayout mCommentLinearLayout;
    private LinearLayout mParticipatorsLinearLayout;
    private LinearLayout mContentLinearLayout;
    private MapView mBaiduMapView;
    private BaiduMap mBaiduMap;
    private List<LatLng> mPathLatLngs;
    BitmapDescriptor mBlueTexture = BitmapDescriptorFactory.fromAsset("icon_road_blue_arrow.png");


    class ViewHolderSomeUser {
        public ImageView  headIconImgeView;
    }

    BaseAdapter mSomeCommentAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            int count = simpleCommemts.size();
            if(count <= 0)
                showNoCommemtTipsView();
            else
                hideNoCommemtTipsView();
            return count;
        }

        @Override
        public Object getItem(int position) {
            return simpleCommemts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderCommemt viewHolder;
            AVComment avComment = simpleCommemts.get(position);
            if(null == convertView) {
                convertView = LayoutInflater.from(TravelActivityBrowseActivity.this).inflate(R.layout.item_comment_list,null);
                viewHolder = new ViewHolderCommemt();
                viewHolder.headIconImgeView = (ImageView) convertView.findViewById(R.id.head_icon_imageview);
                viewHolder.nickNameTextView = (TextView) convertView.findViewById(R.id.nickname_tv);
                viewHolder.commentCreatTimeTextView = (TextView) convertView.findViewById(R.id.comment_create_time_tv);
                viewHolder.commentContentTextView = (TextView) convertView.findViewById(R.id.comment_content_tv);
                convertView.setTag(viewHolder);
            }

            viewHolder = (ViewHolderCommemt) convertView.getTag();
            AVBaseUserInfo baseUserInfo = avComment.getUserBaseInfo();
            if(null != baseUserInfo) {
                viewHolder.nickNameTextView.setText(baseUserInfo.getNickname());
                //touxian

            }
            viewHolder.commentCreatTimeTextView.setText(SimpleDateUtil.formatDateMDHM(avComment.getCreatedAt().getTime()));
            viewHolder.commentContentTextView.setText(avComment.getContent());
            return convertView;
        }
    };

class ViewHolderCommemt {
    public ImageView  headIconImgeView;
    public TextView commentContentTextView;
    public TextView nickNameTextView;
    public TextView commentCreatTimeTextView;
}

    private ScrollView mScrollView;
    private ImageView mActThumbnailMapImageView;
    private TextView mActStateTextView;
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

    private LinearLayout mCommemtEditLinearLayout;
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


    private int mLoadNetDateFlag = 0;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if(mLoadNetDateFlag == 0)
                        mLoadNetDateFlag++;
                    else
                        ToastUtil.toastShort(TravelActivityBrowseActivity.this,"拉取数据完毕");
                    break;
                default:
                    break;
            }
        }
    };

    //加载其余的网络数据
    private void loadNetDate() {
        mLoadNetDateFlag = 0;
        mTravelAct.getParticipatorsBaseInfo().getQuery()
                .setLimit(5).findInBackground(new FindCallback<AVBaseUserInfo>() {
            @Override
            public void done(List<AVBaseUserInfo> list, AVException e) {
                if (null != e) {
                    e.printStackTrace();
                    ToastUtil.toastShort(TravelActivityBrowseActivity.this,"连接服务器异常");
                } else {
                    if(null != list && list.size() > 0) {
                        simpleJoinUsers.addAll(list);
                        mSomeUsersAdapter.notifyDataSetInvalidated();
                    }
                }
              Message msg = mHandler.obtainMessage();
                msg.what = 0;
                mHandler.sendMessage(msg);
            }
        });

        AVQuery<AVComment> query = AVQuery.getQuery(AVComment.class);
        query.setLimit(5).include(AVComment.USER_BASE_INFO_KEY).orderByDescending("createAt")
                .whereEqualTo(AVComment.TRAVELACTIVITY_KEY,mTravelAct)
                .findInBackground(new FindCallback<AVComment>() {
                    @Override
                    public void done(List<AVComment> list, AVException e) {
                        if(null != e) {
                            e.printStackTrace();
                            ToastUtil.toastShort(TravelActivityBrowseActivity.this,"连接服务器异常");
                        }
                        if(null != list && list.size() > 0) {
                            simpleCommemts.addAll(list);
                            mSomeCommentAdapter.notifyDataSetInvalidated();
                        }

                        Message msg = mHandler.obtainMessage();
                        msg.what = 0;
                        mHandler.sendMessage(msg);
                    }
                });

        //加载路线地图经纬度数据
        AVQuery<AVTravelMapPath> mapPathAVQuery = AVQuery.getQuery(AVTravelMapPath.class);

        AVTravelMapPath avTravelMapPath = mTravelAct.getMapPath();
        //晕倒。。。。。测试数据比较多。。。
        if(null == avTravelMapPath) {
            ToastUtil.toastShort(TravelActivityBrowseActivity.this,"路径信息缺失。。");
            return;
        }

        mapPathAVQuery.getInBackground(avTravelMapPath.getObjectId(), new GetCallback<AVTravelMapPath>() {
            @Override
            public void done(AVTravelMapPath avTravelMapPath, AVException e) {

                if(null != e && e.getCode() == AVException.OBJECT_NOT_FOUND) {
                    ToastUtil.toastShort(TravelActivityBrowseActivity.this,"路径信息缺失。。");
                    return;
                }
                if(null == e) {
                    if(null != avTravelMapPath) {
                      List<AVGeoPoint> avGeoPoints =  avTravelMapPath.getMapPathLatLngArr();
                        if(null == avGeoPoints || avGeoPoints.size() <= 1) {
                            ToastUtil.toastShort(TravelActivityBrowseActivity.this,"路径信息缺失。。");
                            return;
                        }
                        //转换 百度的经纬度
                        mPathLatLngs = new ArrayList<LatLng>();
                        int length = avGeoPoints.size();
                        AVGeoPoint avPoint = null;
                        for (int i = 0; i < length; i++) {
                            avPoint = avGeoPoints.get(i);
                            mPathLatLngs.add(new LatLng(avPoint.getLatitude(),avPoint.getLongitude()));
                        }
                        showPathOnMap();
                    }
                }

            }
        });


    }

    private void showPathOnMap() {
        if(null == mPathLatLngs || mPathLatLngs.size() <= 1)
            return;

        // 地图地位地点 定位（把地图拉到路径那个地方）
        MapStatus mapStatus = new MapStatus.Builder().target(mPathLatLngs.get(0)).zoom(18).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mBaiduMap.setMapStatus(mapStatusUpdate);

        //画路径折线图
        OverlayOptions ooPolyline11 = new PolylineOptions().width(5).points(mPathLatLngs).customTexture(mBlueTexture).dottedLine(true);
        mBaiduMap.addOverlay(ooPolyline11);

    }

    private void initView() {

        mScrollView = (ScrollView) findViewById(R.id.content_scrollView);
        mScrollView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    hideCommemtEditView();
            }
        });
        
//        mActThumbnailMapImageView = (ImageView) findViewById(R.id.map_thumbnail_imageview);
//        mActThumbnailMapImageView.setOnClickListener(this);


        mActStateTextView = (TextView) findViewById(R.id.activity_state_tv);

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
        mParticipatorsLinearLayout =(LinearLayout) findViewById(R.id.participators_ll);
        mParticipatorsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TravelActivityBrowseActivity.this,ActParticipatorActivity.class);
                intent.putExtra(ActCommentActivity.AV_TRAVEL_ACT_KEY,mTravelAct);
                startActivity(intent);
            }
        });
        mSomeUserGridView = (GridView) findViewById(R.id.user_list_gridview);
        mSomeUserGridView.setAdapter(mSomeUsersAdapter);
        mNoCommemtTipsTextView = (TextView) findViewById(R.id.tips_no_comment_tv);
        mSomeCommemtListView = (ListView) findViewById(R.id.comment_listview);

        mSomeCommemtListView.setAdapter(mSomeCommentAdapter);
        mCommentLinearLayout = (LinearLayout) findViewById(R.id.comment_ll);
        mCommentLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(TravelActivityBrowseActivity.this,ActCommentActivity.class);
                intent.putExtra(ActCommentActivity.AV_TRAVEL_ACT_KEY,mTravelAct);
                startActivity(intent);
            }
        });
        mBackBtn = (Button) findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(this);
        mJoinActBtn = (Button) findViewById(R.id.join_activity_btn);
        mJoinActBtn.setOnClickListener(this);
        mAddCommemtBtn = (Button) findViewById(R.id.comment_btn);
        mAddCommemtBtn.setOnClickListener(this);

        mBottomBtnsLinearLayout = (LinearLayout) findViewById(R.id.bottom_btns_ll);
        mCommemtEditText = (EditText) findViewById(R.id.comment_edittext);
        mCommemtEditLinearLayout = (LinearLayout) findViewById(R.id.comment_edit_ll);

        mUploadCommemtBtn = (Button) findViewById(R.id.add_commemt_btn);
        mUploadCommemtBtn.setOnClickListener(this);

        mContentLinearLayout = (LinearLayout) findViewById(R.id.content_ll);
        //取消显示的评论View
        mContentLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideCommemtEditView();
            }
        });


        initBaiduMapView();
    }

    private void initBaiduMapView() {
        mBaiduMapView = (MapView) findViewById(R.id.ativity_path_baidumap);
        mBaiduMap = mBaiduMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
//            case R.id.map_thumbnail_imageview:
//                browseActivityTravelMapPath();
//                break;
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

    private int currClickBtnid = 0;
    private void uploadCommemt() {
        currClickBtnid = R.id.add_commemt_btn;
        AVComment comment = new AVComment();
        comment.setTravelActivity(mTravelAct);

        String commemtContent = DataSimpleGetUtil.getEditTextData(mCommemtEditText,"");
        if(commemtContent.isEmpty()) {
            return;
        }
        comment.setContent(commemtContent);

        AVMUser avUser = (AVMUser) AVUser.getCurrentUser();
        if(null == avUser) {
            login();
            return;
        }
        comment.setUser(avUser);
        AVACL avacl = new AVACL();
        avacl.setPublicReadAccess(true);
        avacl.setWriteAccess(avUser,true);
        comment.setACL(avacl);

        AVBaseUserInfo baseUserInfo = getAplicationBaseUserInfoCache();
        if (null == baseUserInfo) {
            showProgressDialg();
            AVQuery<AVBaseUserInfo> query = AVQuery.getQuery(AVBaseUserInfo.class);
            query.getInBackground(avUser.getBaseInfo().getObjectId(), new GetCallback<AVBaseUserInfo>() {
                @Override
                public void done(AVBaseUserInfo avBaseUserInfo, AVException e) {
                    hideProgressDialg();
                    if(null != e || avBaseUserInfo == null) {
                        ToastUtil.toastShort(TravelActivityBrowseActivity.this,"获取数据失败");
                        if(null != e)
                            e.printStackTrace();
                        return;
                    }
                    updateAplicationBaseUserInfoCache(avBaseUserInfo);
                    uploadCommemt();
                }
            });
            return;
        }
        comment.setUserBaseInfo(baseUserInfo);

        showProgressDialg("提交评论。。。");
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                hideProgressDialg();
                if(null != e) {
                    e.printStackTrace();
                    ToastUtil.toastShort(TravelActivityBrowseActivity.this,"添加失败");
                    return;
                }

                refreshAct(1);
                mCommemtEditText.setText("");
                hideCommemtEditView();
            }
        });

    }

    private void refreshAct(int flag) {
        //刷新评论
        if(1 == flag) {
            if(simpleCommemts.size() < 5) {
                AVQuery<AVComment> query = AVQuery.getQuery(AVComment.class);
                query.setLimit(5).include(AVComment.USER_BASE_INFO_KEY).orderByDescending("createAt")
                        .whereEqualTo(AVComment.TRAVELACTIVITY_KEY,mTravelAct.getObjectId())
                        .findInBackground(new FindCallback<AVComment>() {
                            @Override
                            public void done(List<AVComment> list, AVException e) {
                                if(null != e) {
                                    e.printStackTrace();
                                    ToastUtil.toastShort(TravelActivityBrowseActivity.this,"连接服务器异常");
                                }
                                if(null != list && list.size() > 0) {
                                    simpleCommemts.addAll(list);
                                    mSomeCommentAdapter.notifyDataSetInvalidated();
                                }

                            }
                        });
            }

        }
    }


    private void showCommemtEditView() {
        if(mCommemtLinearLayoutVisible)
            return;

        mCommemtEditText.setText("");
        mBottomBtnsLinearLayout.setVisibility(View.GONE);
        mCommemtEditLinearLayout.setVisibility(View.VISIBLE);
        mCommemtLinearLayoutVisible = true;
    }

    private void hideCommemtEditView() {
        if (mCommemtLinearLayoutVisible) {
            mCommemtEditLinearLayout.setVisibility(View.GONE);
            mBottomBtnsLinearLayout.setVisibility(View.VISIBLE);
            mCommemtLinearLayoutVisible = false;
            mCommemtEditText.setText("");
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

    private AVMUser mAVuser = null;
    private void joinAct() {
        AVMUser user = (AVMUser) AVUser.getCurrentUser();
        if(null == user) {
            login();
            return;
        }
        mAVuser = user;

        Long actRegistDeadline = mTravelAct.getRegistrationDeadline();
        if(actRegistDeadline == null || actRegistDeadline.longValue() <= 0l) {
            ToastUtil.toastLong(TravelActivityBrowseActivity.this,"报名已经截止，加入失败。");
            return;
        }

        long currTime = System.currentTimeMillis();
        if(currTime > actRegistDeadline.longValue()) {
            ToastUtil.toastLong(TravelActivityBrowseActivity.this,"报名已经截止，加入失败。");
            return;
        }

        final AVBaseUserInfo baseUserInfo = getAplicationBaseUserInfoCache();
        if (null == baseUserInfo) {
            showProgressDialg();
            AVQuery<AVBaseUserInfo> query = AVQuery.getQuery(AVBaseUserInfo.class);
            query.getInBackground(user.getBaseInfo().getObjectId(), new GetCallback<AVBaseUserInfo>() {
                @Override
                public void done(AVBaseUserInfo avBaseUserInfo, AVException e) {
                    hideProgressDialg();
                    if(null != e || avBaseUserInfo == null) {
                        ToastUtil.toastShort(TravelActivityBrowseActivity.this,"获取数据失败");
                        if(null != e)
                        e.printStackTrace();
                        return;
                    }
                    updateAplicationBaseUserInfoCache(avBaseUserInfo);
                    joinAct();
                }
            });
            return;
        }

        mTravelAct.getParticipatorsBaseInfo().getQuery().whereEqualTo(AVBaseUserInfo.OBJECT_ID,baseUserInfo.getObjectId())
                .getFirstInBackground(new GetCallback<AVBaseUserInfo>() {
                    @Override
                    public void done(AVBaseUserInfo avBaseUserInfo, AVException e) {
                        if(null != e) {
                            ToastUtil.toastShort(TravelActivityBrowseActivity.this,"连接服务器失败"+e.getCode());
                            e.printStackTrace();
                            return;
                        }

                        if (null == avBaseUserInfo) {
                            try {

                                showProgressDialg("加入该活动...");

                                //由于leanclude 没有事务支持。好像只能通过云代码来实现，所以暂时这个问题忽略。走业务流程。
                                AVTravelActivity travelActivity = AVObject.createWithoutData(AVTravelActivity.class,mTravelAct.getObjectId());
                                mAVuser.getJoinedTravelActivitysRelation().add(travelActivity);
                                //。。。
                                mAVuser.saveInBackground();

                                travelActivity.getParticipatorsBaseInfo().add(baseUserInfo);
                                travelActivity.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        hideProgressDialg();
                                        if(null == e) {
                                            ToastUtil.toastLong(TravelActivityBrowseActivity.this,"加入成功");
                                        }else {
                                            e.printStackTrace();
                                            ToastUtil.toastLong(TravelActivityBrowseActivity.this,"加入失败"+e.getCode());
                                        }
                                        hideProgressDialg();
                                    }
                                });

                            } catch (AVException e1) {
                                e1.printStackTrace();
                                hideProgressDialg();
                            }


                        } else {
                            ToastUtil.toastShort(TravelActivityBrowseActivity.this,"你已参加本次活动,无需再次参加.");
                        }
                    }
                });

    }


    @Override
    protected void onLoginActivityResult(int resultCode, Intent data) {
        if(RESULT_OK != resultCode)
            return;
        if(currClickBtnid == R.id.add_commemt_btn) {
            uploadCommemt();
        } else {
            joinAct();
        }
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
