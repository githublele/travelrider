package cn.nono.ridertravel.ui.usercenter;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.List;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.bean.av.AVMUser;
import cn.nono.ridertravel.bean.av.AVTravelActivity;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.travelact.TravelActivityBrowseActivity;
import cn.nono.ridertravel.util.SimpleDateUtil;

/**
 * Created by Administrator on 2016/4/25.
 */
public class ActivityJoinedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private ListView mTravelActsListView;
    private SwipeRefreshLayout mSwipRefreshLayout;
    private ArrayList<AVTravelActivity> mTravelActivities = new ArrayList<AVTravelActivity>();

    private BaseAdapter mListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mTravelActivities.size();
        }

        @Override
        public Object getItem(int position) {
            return mTravelActivities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ActivityJoinedFragment.ViewHolder  viewHolder;
            if(null == convertView) {
                viewHolder = new ActivityJoinedFragment.ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_travel_act_list,null);
                viewHolder.actStateTextView = (TextView) convertView.findViewById(R.id.activity_state_tv);
                viewHolder.actHeadlineTextView = (TextView) convertView.findViewById(R.id.diary_headline_tv);
                viewHolder.actStartDateTextView = (TextView) convertView.findViewById(R.id.activity_start_date_tv);
                viewHolder.actEndDateTextView = (TextView) convertView.findViewById(R.id.activity_end_date_tv);
                viewHolder.actIssuerTextView = (TextView) convertView.findViewById(R.id.activity_issuer_tv);
                viewHolder.actFallInPlaceTextView = (TextView) convertView.findViewById(R.id.activity_fall_in_place_tv);
                viewHolder.actMapImageView = (ImageView) convertView.findViewById(R.id.map_thumbnail_imageview);
                convertView.setTag(viewHolder);
            }

            viewHolder = (ViewHolder) convertView.getTag();
            AVTravelActivity avTravelActivity = mTravelActivities.get(position);
            viewHolder.actHeadlineTextView.setText(avTravelActivity.getHeadline());
            viewHolder.actStartDateTextView.setText(SimpleDateUtil.formatDateYMDHMS(avTravelActivity.getStartDateMillisTime()));
            viewHolder.actEndDateTextView.setText(SimpleDateUtil.formatDateYMDHMS(avTravelActivity.getEndDateMillisTime()));
            viewHolder.actIssuerTextView.setText(avTravelActivity.getIsserBaseInfo().getNickname());
            viewHolder.actFallInPlaceTextView.setText(avTravelActivity.getFallInPlace());

            return convertView;
        }
    };



    @Override
    public void onRefresh() {
        AVMUser avmUser = (AVMUser) AVUser.getCurrentUser();
        if(null == avmUser)
            return;
        mSwipRefreshLayout.setRefreshing(true);
        AVQuery<AVTravelActivity> activityAVQuery = avmUser.getJoinedTravelActivitysRelation().getQuery();;
        activityAVQuery.include(AVTravelActivity.ISSUER_BASE_INFO_POINTER_KEY);
        activityAVQuery.orderByDescending(AVObject.CREATED_AT);
        activityAVQuery.setLimit(10);
        activityAVQuery.findInBackground(new FindCallback<AVTravelActivity>() {
            @Override
            public void done(List<AVTravelActivity> list, AVException e) {
                mSwipRefreshLayout.setRefreshing(false);
                if(null != e && AVException.OBJECT_NOT_FOUND != e.getCode()) {
                    ToastUtil.toastLong(getActivity(),"网络异常。"+e.getCode());
                    e.printStackTrace();
                    return;
                }

                if(null == list || list.size() <= 0) {
                    ToastUtil.toastLong(getActivity(),"没有数据");
                    return;
                }

                mTravelActivities.clear();
                mTravelActivities.addAll(list);
                mListAdapter.notifyDataSetInvalidated();
            }
        });
    }

    class ViewHolder {
        TextView actStateTextView;
        TextView actHeadlineTextView;
        TextView actStartDateTextView;
        TextView actEndDateTextView;
        TextView actIssuerTextView;
        TextView actFallInPlaceTextView;
        ImageView actMapImageView;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travel_act_joined, container, false);
        init(view);
        initData();
        return view;
    }

    private void init(View view) {

       mTravelActsListView = (ListView) view.findViewById(R.id.activity_list_listview);
       mTravelActsListView.setAdapter(mListAdapter);
       mTravelActsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               AVTravelActivity act = mTravelActivities.get(position);
               Intent intent = new Intent(getActivity(),TravelActivityBrowseActivity.class);
               intent.putExtra(TravelActivityBrowseActivity.AV_TRAVEL_ACT_KEY,act);
               startActivity(intent);
           }
       });

        mSwipRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_ly);
        mSwipRefreshLayout.setOnRefreshListener(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initData() {
       onRefresh();
    }
}
