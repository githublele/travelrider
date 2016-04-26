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
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.List;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.bean.av.AVBaseUserInfo;
import cn.nono.ridertravel.bean.av.AVComment;
import cn.nono.ridertravel.bean.av.AVTravelActivity;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;
import cn.nono.ridertravel.util.SimpleDateUtil;

/**
 * Created by Administrator on 2016/4/27.
 */
public class ActCommentActivity extends BaseNoTitleActivity implements View.OnClickListener{

    public static final String AV_TRAVEL_ACT_KEY = "AV_ACT";

    private ListView mListView;
    private Button mBackBtn;

    private AVTravelActivity mTravelAct;

    private List<AVComment> simpleCommemts = new ArrayList<AVComment>();


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
                convertView = LayoutInflater.from(ActCommentActivity.this).inflate(R.layout.item_comment_list,null);
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
    private TextView mNoCommemtTipsTextView;

    class ViewHolderCommemt {
        public ImageView  headIconImgeView;
        public TextView commentContentTextView;
        public TextView nickNameTextView;
        public TextView commentCreatTimeTextView;
    }

    private boolean mNoCommemtTipsVisible = false;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_comment);
        mTravelAct = getIntent().getParcelableExtra(AV_TRAVEL_ACT_KEY);
        if (mTravelAct == null) {
            finish();
            return;
        }
       mListView = (ListView) findViewById(R.id.commemt_listview);
       mBackBtn = (Button) findViewById(R.id.back_btn);
        mNoCommemtTipsTextView = (TextView) findViewById(R.id.tips_no_commemt_tv);
        loadData();

    }

    private void loadData() {
        AVQuery<AVComment> query = AVQuery.getQuery(AVComment.class);
        query.include(AVComment.USER_BASE_INFO_KEY).orderByDescending("createAt")
                .whereEqualTo(AVComment.TRAVELACTIVITY_KEY,mTravelAct.getObjectId())
                .findInBackground(new FindCallback<AVComment>() {
                    @Override
                    public void done(List<AVComment> list, AVException e) {
                        if(null != e) {
                            e.printStackTrace();
                            ToastUtil.toastShort(ActCommentActivity.this,"连接服务器异常");
                        }
                        if(null != list && list.size() > 0) {
                            simpleCommemts.addAll(list);
                            mSomeCommentAdapter.notifyDataSetInvalidated();
                        }


                    }
                });
    }

    @Override
    public void onClick(View v) {
        finish();
        return;
    }
}
