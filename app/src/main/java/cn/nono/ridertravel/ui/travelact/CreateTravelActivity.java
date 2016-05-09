package cn.nono.ridertravel.ui.travelact;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;

import org.feezu.liuli.timeselector.TimeSelector;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.RiderTravelApplication;
import cn.nono.ridertravel.bean.av.AVBaseUserInfo;
import cn.nono.ridertravel.bean.av.AVMUser;
import cn.nono.ridertravel.bean.av.AVTravelActivity;
import cn.nono.ridertravel.bean.av.AVTravelMapPath;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.ContentInputActivity;
import cn.nono.ridertravel.ui.baidumap.SearchPlaceActivity;
import cn.nono.ridertravel.ui.baidumap.TravelMapPathActivity;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;

/**
 * Created by Administrator on 2016/4/15.
 */
public class CreateTravelActivity extends BaseNoTitleActivity implements View.OnClickListener{


    public final static int HEADLINE_INPUT_REQ_CODE = 1;
    public final static int INTRODUCE_INPUT_REQ_CODE = 2;
    public final static int TRAVEL_PATH_INPUT_REQ_CODE = 3;
    public final static int FALL_IN_PLACE_INPUT_REQ_CODE = 4;
    public final static int PHONE_INPUT_REQ_CODE = 5;


    Button backButton;
    Button headlineButton;
    Button introduceButton;
    Button travelPathButton;
    Button fallInPlaceButton;
    Button actStartDateButton;
    Button actEndDateButton;
    Button registrationDeadlineButton;
    Button phoneButton;
    Button issueButton;
    TextView creatorTextView;

    AVTravelActivity mAVAcitivity = new AVTravelActivity();
    AVMUser mUser = null;
    AVBaseUserInfo mUserBaseInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_act_create);
        //登录拦截。
        mUser = (AVMUser) AVUser.getCurrentUser(AVMUser.class);
        if (mUser == null) {
            login();
            finish();
            return;
        }
        mAVAcitivity.setIssuer(mUser);

        initView();

        mUserBaseInfo = ((RiderTravelApplication)getApplication()).getUserBaseInfo();
        if(null == mUserBaseInfo) {
            loadUserBaseInfoDate();
        } else {
            mAVAcitivity.setBaseUserInfo(mUserBaseInfo);
            creatorTextView.setText(mUserBaseInfo.getNickname());
        }


    }

    ProgressDialog progressDlg = null;
    private void loadUserBaseInfoDate() {
        progressDlg = new ProgressDialog(this);
        progressDlg.setMessage("连接服务器...");
        progressDlg.show();

        AVQuery<AVBaseUserInfo> query = AVQuery.getQuery(AVBaseUserInfo.class);
        query.getInBackground(mUser.getBaseInfo().getObjectId(), new GetCallback<AVBaseUserInfo>() {
            @Override
            public void done(AVBaseUserInfo avBaseUserInfo, AVException e) {
                if(null != e) {
                    progressDlg.dismiss();
                    ToastUtil.toastShort(CreateTravelActivity.this, "连接失败");
                    finish();
                    return;
                } else {
                    ((RiderTravelApplication)getApplication()).updateUserBaseInfo(avBaseUserInfo);
                    ToastUtil.toastShort(CreateTravelActivity.this, "连接成功");
                    mUserBaseInfo = avBaseUserInfo;
                    progressDlg.dismiss();
                    creatorTextView.setText(mUserBaseInfo.getNickname());
                    return;
                }
            }
        });

    }

    private void initView() {
        backButton = (Button) findViewById(R.id.back_btn);
        backButton.setOnClickListener(this);
        headlineButton = (Button) findViewById(R.id.headline_btn);
        headlineButton.setOnClickListener(this);
        introduceButton = (Button) findViewById(R.id.activity_introduce_btn);
        introduceButton.setOnClickListener(this);
        travelPathButton = (Button) findViewById(R.id.travel_path_btn);
        travelPathButton.setOnClickListener(this);
        fallInPlaceButton = (Button) findViewById(R.id.fall_in_place_btn);
        fallInPlaceButton.setOnClickListener(this);
        actStartDateButton = (Button) findViewById(R.id.activity_start_date_btn);
        actStartDateButton.setOnClickListener(this);
        actEndDateButton = (Button) findViewById(R.id.activity_end_date_btn);
        actEndDateButton.setOnClickListener(this);
        registrationDeadlineButton = (Button) findViewById(R.id.registration_deadline_btn);
        registrationDeadlineButton.setOnClickListener(this);
        phoneButton = (Button) findViewById(R.id.phone_btn);
        phoneButton.setOnClickListener(this);
        issueButton = (Button) findViewById(R.id.issue_btn);
        issueButton.setOnClickListener(this);

        creatorTextView = (TextView) findViewById(R.id.creator_tv);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        int viewId = v.getId();
        switch (viewId) {
            case R.id.back_btn:
                goBack();
                break;
            case R.id.headline_btn:
                intent = new Intent(this, ContentInputActivity.class);
                intent.putExtra(ContentInputActivity.TITLE_KEY,"标题");
                intent.putExtra(ContentInputActivity.CONTENT_DEFAULT_KEY,mAVAcitivity.getHeadline());
                startActivityForResult(intent,HEADLINE_INPUT_REQ_CODE);
            break;
            case R.id.activity_introduce_btn:
                intent = new Intent(this, ContentInputActivity.class);
                intent.putExtra(ContentInputActivity.CONTENT_DEFAULT_KEY,mAVAcitivity.getActivityIntroduce());
                intent.putExtra(ContentInputActivity.TITLE_KEY,"活动介绍");
                startActivityForResult(intent,INTRODUCE_INPUT_REQ_CODE);
                break;
            case R.id.travel_path_btn:
                createTravelMapPath();
                break;
            case R.id.fall_in_place_btn:
                selectFallInPlace();
                break;
            case R.id.activity_start_date_btn:
                showSelectTimeDlg(R.id.activity_start_date_btn);
                break;
            case R.id.activity_end_date_btn:
                showSelectTimeDlg(R.id.activity_end_date_btn);
                break;
            case R.id.registration_deadline_btn:
                showSelectTimeDlg(R.id.registration_deadline_btn);
                break;
            case R.id.phone_btn:
                intent = new Intent(this, ContentInputActivity.class);
                intent.putExtra(ContentInputActivity.CONTENT_DEFAULT_KEY,mAVAcitivity.getPhone());
                intent.putExtra(ContentInputActivity.TITLE_KEY,"联系电话");
                startActivityForResult(intent,PHONE_INPUT_REQ_CODE);
                break;
            case R.id.issue_btn:
                createActToNet();
                break;
            default:
                break;
        }
    }



    private void createActToNet() {

        //必要检查

        mAVAcitivity.setBaseUserInfo(mUserBaseInfo);
        mAVAcitivity.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(null != e) {
                    e.printStackTrace();
                    ToastUtil.toastShort(CreateTravelActivity.this,"创建失败");
                    return;
                }

                mUser.getCreateTravelActivitysRelation().add(mAVAcitivity);
                mUser.getJoinedTravelActivitysRelation().add(mAVAcitivity);
                mUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        ToastUtil.toastShort(CreateTravelActivity.this,"succession");
                        setResult(RESULT_OK);
                        finish();
                    }
                });
                ;
            }
        });

    }


    private void createTravelMapPath() {
        Intent intent = new Intent(this, TravelMapPathActivity.class);
        startActivityForResult(intent,TRAVEL_PATH_INPUT_REQ_CODE);
    }

    private void selectFallInPlace() {
        Intent intent = new Intent(this, SearchPlaceActivity.class);
        startActivityForResult(intent,FALL_IN_PLACE_INPUT_REQ_CODE);
    }



    TimeSelector timeSelector;
    int timeType = -1;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    long startTime = -1;
    long endTime = -1;
    long registTime = -1;
    private void showSelectTimeDlg(int timeFlag) {
            if(timeSelector == null) {

                timeSelector = new TimeSelector(this, new TimeSelector.ResultHandler() {
                    @Override
                    public void handle(String time) {
                        try {
                            if(R.id.activity_start_date_btn == timeType) {
                                startTime = simpleDateFormat.parse(time).getTime();
                                mAVAcitivity.setStartDateMillisTime(startTime);
                                actStartDateButton.setText(time);
                                actEndDateButton.setText("");
                                endTime = -1;
                                mAVAcitivity.setEndDateMillisTime(0l);
                            } else if (R.id.activity_end_date_btn == timeType) {
                                endTime = simpleDateFormat.parse(time).getTime();
                                if(endTime <= startTime) {
                                    ToastUtil.toastLong(getApplicationContext(),"输入有误(结束时间必须大于开始时间！).");
                                    timeType = -1;
                                    return;
                                }
                                mAVAcitivity.setEndDateMillisTime(endTime);
                                actEndDateButton.setText(time);
                            } else if (R.id.registration_deadline_btn == timeType) {
                                registTime = simpleDateFormat.parse(time).getTime();
                                if(registTime < startTime) {
                                    ToastUtil.toastLong(getApplicationContext(),"输入有误(报名截止时间必须在活动开始前！).");
                                    timeType = -1;
                                    return;
                                }
                                mAVAcitivity.setRegistrationDeadline(registTime);
                                registrationDeadlineButton.setText(time);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        timeType = -1;

                    }
                },simpleDateFormat.format(new Date()),"2030-01-01 00:00");

                timeSelector.setScrollUnit(TimeSelector.SCROLLTYPE.YEAR,TimeSelector.SCROLLTYPE.MONTH,
                        TimeSelector.SCROLLTYPE.DAY,TimeSelector.SCROLLTYPE.HOUR,TimeSelector.SCROLLTYPE.MINUTE
                        );
            }

        if(R.id.registration_deadline_btn == timeFlag) {
            if(startTime == -1 || endTime == -1) {
                ToastUtil.toastLong(getApplication(),"请输入开始时间和结束时间。");
                return;
            }

        }
            timeType = timeFlag;
            timeSelector.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       switch (requestCode) {
           case HEADLINE_INPUT_REQ_CODE:
               headlineInputResult(resultCode,data);
               break;
           case INTRODUCE_INPUT_REQ_CODE:
               introduceInputResult(resultCode,data);
           break;
           case TRAVEL_PATH_INPUT_REQ_CODE:
               travelPathInputResult(resultCode,data);
           break;
           case FALL_IN_PLACE_INPUT_REQ_CODE:
               fallIntInputResult(resultCode,data);
           break;
           case PHONE_INPUT_REQ_CODE:
               phoneInputResult(resultCode,data);
           break;
           default:
               break;

       }


    }

    private void phoneInputResult(int resultCode, Intent data) {
        if(resultCode != RESULT_OK || null == data)
            return;

          String phone = data.getStringExtra(ContentInputActivity.RES_CONTENT_KEY);
          if(null == phone || phone.isEmpty())
            return;
          phoneButton.setText(phone);
          mAVAcitivity.setPhone(phone);
    }

    private void fallIntInputResult(int resultCode, Intent data) {
        if(resultCode != RESULT_OK || null == data)
            return;
        double lat = data.getDoubleExtra(SearchPlaceActivity.LAT_KEY,Double.MAX_VALUE);
        double lng = data.getDoubleExtra(SearchPlaceActivity.LNG_KEY,Double.MAX_VALUE);
        String addr = data.getStringExtra(SearchPlaceActivity.ADDR_KEY);
        if(lat == Double.MAX_VALUE || lng == Double.MAX_VALUE || null == addr || addr.isEmpty()) {
            ToastUtil.toastLong(this,"地址返回有误，请重新选择地址。");
            return;
        }

        fallInPlaceButton.setText(addr);
        mAVAcitivity.setFallInPlace(addr);
        mAVAcitivity.setFallInPlaceLat(lat);
        mAVAcitivity.setFallInPlaceLng(lng);

    }

    private void travelPathInputResult(int resultCode, Intent data) {
        if(resultCode != RESULT_OK || null == data)
            return;
        AVTravelMapPath path = data.getParcelableExtra(TravelMapPathActivity.MAP_PATH_KEY);
        if(null == path)
            return;

        String pathName = path.getName();

        try {
            path = AVObject.createWithoutData(AVTravelMapPath.class,path.getObjectId());
            mAVAcitivity.setTravelMapPath(path);
            travelPathButton.setText(pathName);
        } catch (AVException e) {
            e.printStackTrace();
        }



    }

    private void introduceInputResult(int resultCode, Intent data) {
        if(resultCode != RESULT_OK || null == data)
            return;

            String introduce = data.getStringExtra(ContentInputActivity.RES_CONTENT_KEY);
            if(null == introduce || introduce.isEmpty())
                return;
            introduceButton.setText(introduce);
            mAVAcitivity.setActivityIntroduce(introduce);

    }

    private void headlineInputResult(int resultCode, Intent data) {
        if(resultCode != RESULT_OK || null == data)
            return;

            String headline = data.getStringExtra(ContentInputActivity.RES_CONTENT_KEY);
            if(null == headline || headline.isEmpty())
                return;
            headlineButton.setText(headline);
            mAVAcitivity.setHeadline(headline);

    }

    private void goBack() {
        finish();
    }
}
