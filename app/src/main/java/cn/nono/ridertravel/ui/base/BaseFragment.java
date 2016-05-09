package cn.nono.ridertravel.ui.base;

import android.content.Intent;
import android.support.v4.app.Fragment;

import cn.nono.ridertravel.ui.LoginActivity;

/**
 * Created by Administrator on 2016/5/9.
 */
public class BaseFragment extends Fragment {

    private static final int LOGIN_REQ_CODE = 99;

    protected void login() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent,LOGIN_REQ_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(LOGIN_REQ_CODE == requestCode) {
            onLoginActivityResult(resultCode,data);
        }

    }

    /**
     * 登录成功重写该方法
     * @param resultCode
     * @param data
     */
    protected void onLoginActivityResult(int resultCode, Intent data) {

    }


}
