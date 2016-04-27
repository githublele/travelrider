package cn.nono.ridertravel.ui.travelact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.List;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.bean.av.AVBaseUserInfo;
import cn.nono.ridertravel.bean.av.AVTravelActivity;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;

/**
 * Created by Administrator on 2016/4/27.
 */
public class ActParticipatorActivity extends BaseNoTitleActivity implements View.OnClickListener{
    public static final String AV_TRAVEL_ACT_KEY = "AV_ACT";

    private AVTravelActivity mAvTravelAct;
    private Button mBackButton;
    private ListView mParticipatorsListView;
    private ArrayList<AVBaseUserInfo> userInfos = new ArrayList<AVBaseUserInfo>();
    private BaseAdapter mParticipatorsAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return userInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return userInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AVBaseUserInfo avBaseUserInfo = userInfos.get(position);
            ViewHolder viewHolder;
            if(null == convertView) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(ActParticipatorActivity.this).inflate(R.layout.item_act_participators_list, null);
                viewHolder.headIconImgeView = (ImageView) convertView.findViewById(R.id.head_icon_imageview);
                viewHolder.nickNameTextView = (TextView) convertView.findViewById(R.id.nickname_tv);
                viewHolder.signatureTextView = (TextView) convertView.findViewById(R.id.signature_tv);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            //头像填充
//            ....
            viewHolder.nickNameTextView.setText(avBaseUserInfo.getNickname());
//            String userSignature = avBaseUserInfo.getUserSignature();
//            if(null != userSignature && !userSignature.isEmpty()) {
//                viewHolder.signatureTextView.setText(userSignature);
//            } else {
//                viewHolder.signatureTextView.setText("");
//            }


            return convertView;
        }
    };

    class ViewHolder {
        public ImageView headIconImgeView;
        public TextView signatureTextView;
        public TextView nickNameTextView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_participator);

        mAvTravelAct = getIntent().getParcelableExtra(AV_TRAVEL_ACT_KEY);
        if(null == mAvTravelAct) {
            finish();
            return;
        }

        mBackButton = (Button) findViewById(R.id.back_btn);
        mBackButton.setOnClickListener(this);
        mParticipatorsListView = (ListView) findViewById(R.id.participator_listview);
        mParticipatorsListView.setAdapter(mParticipatorsAdapter);

        mAvTravelAct.getParticipatorsBaseInfo().getQuery()
                .setLimit(5).findInBackground(new FindCallback<AVBaseUserInfo>() {
            @Override
            public void done(List<AVBaseUserInfo> list, AVException e) {
                if (null != e) {
                    e.printStackTrace();
                    ToastUtil.toastShort(ActParticipatorActivity.this,"连接服务器异常");
                } else {
                    if(null != list && list.size() > 0) {
                        userInfos.addAll(list);
                        mParticipatorsAdapter.notifyDataSetInvalidated();
                        return;
                    }

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
