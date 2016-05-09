package cn.nono.ridertravel.ui.usercenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.bean.av.AVBaseUserInfo;
import cn.nono.ridertravel.bean.av.AVMUser;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.diary.MyTravelDiaryFragment;
import cn.nono.ridertravel.util.ImageLoaderOptionsSetting;

/**
 * Created by Administrator on 2016/5/9.
 */
public class UserCenterFragment extends Fragment implements RadioGroup.OnCheckedChangeListener,View.OnClickListener{

    private RadioGroup mRadioGroup;
    private Fragment mMyDiaryFragment = null;
    private Fragment mDiaryCollectionFragment = null;
    private Fragment mMyActivityFragment = null;

    private Fragment mCurrentShowFragment = null;
    private TextView mUserNickname;
    private ImageView mUserHeadImageView;
    private RadioButton mUserSexRadioButton;
    private Button mUserInfoEditBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_center,null);
        mRadioGroup =  (RadioGroup) view.findViewById(R.id.navigation_radioGroup);
        mRadioGroup.setOnCheckedChangeListener(this);
        mRadioGroup.check(R.id.my_diary_radioButton);

        mUserNickname = (TextView) view.findViewById(R.id.user_nickname_tv);
        mUserHeadImageView = (ImageView) view.findViewById(R.id.user_head_icon_imageview);
        mUserSexRadioButton = (RadioButton) view.findViewById(R.id.user_sex);
        mUserInfoEditBtn = (Button) view.findViewById(R.id.user_info_edit_btn);
        mUserInfoEditBtn.setOnClickListener(this);
        initData();
        return view;
    }

    private void initData() {
        AVMUser user = (AVMUser) AVUser.getCurrentUser();
        //没有登录 不可能进入这里。
//        常规
        if(null == user)
            return;
        AVQuery<AVBaseUserInfo> query = AVQuery.getQuery(AVBaseUserInfo.class);
        query.getInBackground(user.getBaseInfo().getObjectId(), new GetCallback<AVBaseUserInfo>() {
            @Override
            public void done(AVBaseUserInfo avBaseUserInfo, AVException e) {
                if(null != e) {
                    ToastUtil.toastShort(getActivity(),"网络获取数据异常 "+e.getCode());
                    e.printStackTrace();
                    return;
                }

                if(null != avBaseUserInfo) {
                    mUserNickname.setText(avBaseUserInfo.getNickname());
                    if(AVBaseUserInfo.SEX_MAN.equals(avBaseUserInfo.getSex())) {
                        mUserSexRadioButton.setChecked(true);
                    } else {
                        mUserSexRadioButton.setChecked(false);
                    }

                   AVFile headAVFile = avBaseUserInfo.getHead();
                   if(null != headAVFile) {
                       ImageLoader.getInstance().displayImage(headAVFile.getUrl(),mUserHeadImageView, ImageLoaderOptionsSetting.getConstantImageLoaderDefaultOptions());
                   }
                }

            }
        });



    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction tc = getChildFragmentManager().beginTransaction();
        if(null != mCurrentShowFragment)
            tc.hide(mCurrentShowFragment);

        if(R.id.my_diary_radioButton == checkedId) {
            if(null == mMyDiaryFragment) {
                mMyDiaryFragment = new MyTravelDiaryFragment();
                tc.add(R.id.content_ll, mMyDiaryFragment);
            }
            mCurrentShowFragment = mMyDiaryFragment;
        }
//后期再把这个补上
        /*
        else if (R.id.diary_collection_radioButton == checkedId) {
            if(null == mDiaryCollectionFragment) {
                mDiaryCollectionFragment = new ?();
                tc.add(R.id.content, mDiaryCollectionFragment);
            }
            mCurrentShowFragment = mDiaryCollectionFragment;
        }
        //R.id.my_activity_radioButton
        else {
            if(null == mMyActivityFragment) {
                mMyActivityFragment = new ?();
                tc.add(R.id.content, mMyActivityFragment);
            }
            mCurrentShowFragment = mMyActivityFragment;
        }
        */

        tc.show(mCurrentShowFragment).commit();
    }

    @Override
    public void onClick(View v) {
        //用户信息编辑 待完成
        ToastUtil.toastShort(getActivity(),"构建中。。。。");
    }
}
