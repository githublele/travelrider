package cn.nono.ridertravel.ui.travelact;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import cn.nono.ridertravel.bean.av.AVTravelActivity;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.util.SimpleDateUtil;

/**
 * Created by Administrator on 2016/4/25.
 */
public class TravelActivityFragment extends Fragment implements View.OnClickListener{

    private static int CREATE_ACTIVITY_REQ_CODE = 1;

    private Button mCreateActBtn;
    private ListView mTravelActsListView;
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
            TravelActivityFragment.ViewHolder  viewHolder;
            if(null == convertView) {
                viewHolder = new TravelActivityFragment.ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_travel_act_list,null);
                viewHolder.actStateTextView = (TextView) convertView.findViewById(R.id.activity_state_tv);
                viewHolder.actHeadlineTextView = (TextView) convertView.findViewById(R.id.activity_start_date_tv);
                viewHolder.actStartDateTextView = (TextView) convertView.findViewById(R.id.activity_start_date_tv);
                viewHolder.actEndDateTextView = (TextView) convertView.findViewById(R.id.activity_end_date_tv);
                viewHolder.actIssuerTextView = (TextView) convertView.findViewById(R.id.activity_issuer_tv);
                viewHolder.actFallInPlaceTextView = (TextView) convertView.findViewById(R.id.activity_fall_in_place_tv);
                viewHolder.actMapImageView = (ImageView) convertView.findViewById(R.id.activity_map_imageView);
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
        View view = inflater.inflate(R.layout.fragment_travel_act, container, false);
        init(view);
        refreshActivityListFromNet();
        return view;
    }

    private void init(View view) {
       mCreateActBtn = (Button) view.findViewById(R.id.create_activity_btn);
       mCreateActBtn.setOnClickListener(this);
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
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (R.id.create_activity_btn == vId) {
            Intent intent = new Intent(getActivity(),CreateTravelActivity.class);
            startActivityForResult(intent, CREATE_ACTIVITY_REQ_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (CREATE_ACTIVITY_REQ_CODE == requestCode) {
            if (resultCode != Activity.RESULT_OK)
                return;
            refreshActivityListFromNet();
        }
    }

    private void refreshActivityListFromNet() {
        mTravelActivities.clear();
        AVQuery<AVTravelActivity> activityAVQuery = AVQuery.getQuery(AVTravelActivity.class);
        activityAVQuery.include(AVTravelActivity.ISSUER_BASE_INFO_POINTER_KEY);
        activityAVQuery.orderByDescending("createdAt");
        activityAVQuery.findInBackground(new FindCallback<AVTravelActivity>() {
            @Override
            public void done(List<AVTravelActivity> list, AVException e) {
                if(null != e) {
                    ToastUtil.toastLong(getActivity(),"拉取数据异常！");
                    e.printStackTrace();
                    return;
                }
                mTravelActivities.addAll(list);
                mListAdapter.notifyDataSetInvalidated();


            }
        });

    }
}
