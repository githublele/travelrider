package cn.nono.ridertravel.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.ui.base.BaseNoTitleFragmentActivity;
import cn.nono.ridertravel.ui.diary.MyTravelDiaryFragmentT;

/**
 * Created by Administrator on 2016/5/5.
 */
public class UserCenterActivity extends BaseNoTitleFragmentActivity implements RadioGroup.OnCheckedChangeListener{

    private RadioGroup mRadioGroup;
    private Fragment mMyDiaryFragment = null;
    private Fragment mDiaryCollectionFragment = null;
    private Fragment mMyActivityFragment = null;

    private Fragment mCurrentShowFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);

        mRadioGroup =  (RadioGroup) findViewById(R.id.navigation_radioGroup);
        mRadioGroup.setOnCheckedChangeListener(this);
        mRadioGroup.check(R.id.my_diary_radioButton);


    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        FragmentTransaction tc = getSupportFragmentManager().beginTransaction();
        if(null != mCurrentShowFragment)
            tc.hide(mCurrentShowFragment);

        if(R.id.my_diary_radioButton == checkedId) {
            if(null == mMyDiaryFragment) {
                mMyDiaryFragment = new MyTravelDiaryFragmentT();
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
}
