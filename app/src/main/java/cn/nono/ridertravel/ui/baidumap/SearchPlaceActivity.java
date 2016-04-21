package cn.nono.ridertravel.ui.baidumap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.nono.ridertravel.R;

/**
 * Created by Administrator on 2016/4/6.
 */
public class SearchPlaceActivity extends Activity implements View.OnClickListener{

    public static final    String LAT_KEY = "lat";
    public static final    String LNG_KEY = "lng";
    public static final    String ADDR_KEY = "addr";

    ListView mPlaceSuggestionListView;
    Button mOkButton;
    Button mClearInputTextBtn;
    Button mSearchBtn;
    EditText mSearchInputEditText;
    MapView mMapView;
    BaiduMap mBaiduMap;
    RequestQueue mRequestQueue;
    LinearLayout mMapLinearLayout;
    TextView mPlaceNameTextView;
    TextView mPlaceAddrTextView;
    Boolean mMapLinearLayoutVisble = false;
    List<PlaceInfo> mPlaceSuggestionInfos = new ArrayList<PlaceInfo>();
    private PlaceInfo mSelectPlaceInfo = new PlaceInfo();

    String mRegion = "全国";
    double mLocationLat = 0.0;
    double mLocationLng = 0.0;
    String mAk = "85LgnkwmerfhhsQaCWt6uTYIE9Y6tdNa";
    String mSha1 = "DA:CA:06:0C:7E:BE:18:47:34:0C:0E:6D:25:A8:82:2F:E1:36:91:9A";
    String mRootPackagePath = "cn.nono.ridertravel";
    String mCode = mSha1+";"+mRootPackagePath;

    BaseAdapter mPlaceSuggestionAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mPlaceSuggestionInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return mPlaceSuggestionInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(null == convertView) {
                convertView = LayoutInflater.from(SearchPlaceActivity.this).inflate(R.layout.item_place_suggestion,null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.place_suggestion_textview);
                convertView.setTag(viewHolder);
            }

            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.textView.setText(mPlaceSuggestionInfos.get(position).name);
            return convertView;
        }

        class ViewHolder {
            public TextView textView;
        }
    };

    TextWatcher mTextWatch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.toString().isEmpty())
                return;
            placeSuggestionFromNet(s.toString());
            hideBaiduMap();
        }
    };

    private void placeSuggestionFromNet(String str) {
        if(null == str || str.isEmpty())
            return;
        mRequestQueue.cancelAll("place_sug");
        StringBuilder sb = new StringBuilder("http://api.map.baidu.com/place/v2/suggestion?");
        sb.append("query=");
        try {
            sb.append(URLEncoder.encode(str, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sb.append("&region=");
        sb.append(mRegion);
        sb.append("&mcode=");
        sb.append(mCode);
        sb.append("&output=json");
        sb.append("&ak=");
        sb.append(mAk);
        if(mLocationLat != 0.0 && mLocationLng != 0.0) {
            sb.append("&location=");
            sb.append(mLocationLat);
            sb.append(",");
            sb.append(mLocationLng);
        }


        String url = sb.toString();
//        sb.append("http://api.map.baidu.com/place/v2/suggestion?query="+ URLEncoder.encode("道滘", "UTF-8")+"&region=119&mcode="+"DA:CA:06:0C:7E:BE:18:47:34:0C:0E:6D:25:A8:82:2F:E1:36:91:9A;nono.com.lbst"+"&output=json&ak=mVeEvXasBghiGRzIDq0rQq6gvB1QTsap&location="+point.latitude+","+point.longitude);



        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.i("xx",jsonObject.toString());
                if(null == jsonObject)
                    return;


                try {
                    if(0 != jsonObject.getInt("status"))
                        return;
                    List<PlaceInfo> placeInfos = jsonPlaceSuggestionParse(jsonObject);
                    if(null == placeInfos)
                        mPlaceSuggestionInfos.clear();
                    else
                        mPlaceSuggestionInfos = placeInfos;

                    mPlaceSuggestionAdapter.notifyDataSetInvalidated();

                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("xx",volleyError.getMessage());
            }
        });

        request.setTag("place_sug");
        mRequestQueue.add(request);
    }

    private List<PlaceInfo> jsonPlaceSuggestionParse(JSONObject jsonObject) {
        List<PlaceInfo> placeInfos = new ArrayList<PlaceInfo>();
        try {
            PlaceInfo placeInfo;
            JSONObject placeInfoJson;
            JSONObject locationJson;
            JSONArray resArr = jsonObject.getJSONArray("result");
            int length = resArr.length();
            for (int i = 0; i < length ;i++) {
                placeInfoJson = resArr.getJSONObject(i);
                placeInfo = new PlaceInfo();
                placeInfo.name = placeInfoJson.getString("name");
                placeInfo.city = placeInfoJson.getString("city");
                placeInfo.district = placeInfoJson.getString("district");
                if(placeInfoJson.isNull("location")) {
                    placeInfo = null;
                    placeInfoJson = null;
                    locationJson = null;
                    continue;
                }

                locationJson = placeInfoJson.getJSONObject("location");
                placeInfo.locationLat = locationJson.getDouble("lat");
                placeInfo.locationLng = locationJson.getDouble("lng");
                placeInfos.add(placeInfo);
                placeInfo = null;
                placeInfoJson = null;
                locationJson = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return placeInfos;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_place);
        mRequestQueue = Volley.newRequestQueue(getApplication());
        mRequestQueue.start();
        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRequestQueue.stop();
    }

    private void initView() {
        mMapLinearLayout = (LinearLayout) findViewById(R.id.map_ll);
        mPlaceNameTextView = (TextView) findViewById(R.id.place_name_textview);
        mPlaceAddrTextView = (TextView) findViewById(R.id.place_addr_textview);
        mPlaceSuggestionListView = (ListView) findViewById(R.id.place_suggestion_listview);
        mPlaceSuggestionListView.setAdapter(mPlaceSuggestionAdapter);
        mPlaceSuggestionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlaceInfo placeInfo = mPlaceSuggestionInfos.get(position);

                //刷新经纬度
                mSelectPlaceInfo.locationLat = placeInfo.locationLat;
                mSelectPlaceInfo.locationLng = placeInfo.locationLng;

                mSearchInputEditText.setText(placeInfo.name);

//                mBaiduMap.setMyLocationEnabled(true);

                LatLng point = new LatLng(placeInfo.locationLat, placeInfo.locationLng);
                searchPlacInfoFromNet(point);
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_geo);
                OverlayOptions option = new MarkerOptions().position(point).icon(bitmap).zIndex(5);
                Marker marker = (Marker) mBaiduMap.addOverlay(option);
                marker.setDraggable(true);

                MapStatus mapStatus = new MapStatus.Builder().target(point).zoom(18).build();
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
                mBaiduMap.setMapStatus(mapStatusUpdate);
                showBaiduMap();

            }
        });

        mClearInputTextBtn = (Button) findViewById(R.id.clear_input_text_btn);
        mClearInputTextBtn.setOnClickListener(this);
        mSearchBtn = (Button) findViewById(R.id.search_btn);
        mSearchBtn.setOnClickListener(this);
        mOkButton = (Button) findViewById(R.id.select_btn);
        mOkButton.setOnClickListener(this);
        mSearchInputEditText = (EditText) findViewById(R.id.search_input_edittext);
        mSearchInputEditText.addTextChangedListener(mTextWatch);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {
                Log.i("xx","onMarkerDrag :"+marker.getPosition().toString());
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.i("xx","onMarkerDragEnd :"+marker.getPosition().toString());
                searchPlacInfoFromNet(marker.getPosition());
            }

            @Override
            public void onMarkerDragStart(Marker marker) {
                    Log.i("xx","onMarkerDragStart :"+marker.getPosition().toString());
            }
        });

    }

    private void showBaiduMap() {
        if(mMapLinearLayoutVisble)
            return;
        mPlaceSuggestionListView.setVisibility(View.GONE);
        mMapLinearLayout.setVisibility(View.VISIBLE);
        mMapLinearLayoutVisble = true;
    }

    private void hideBaiduMap() {
        if(mMapLinearLayoutVisble) {
            mMapLinearLayout.setVisibility(View.GONE);
            mPlaceSuggestionListView.setVisibility(View.VISIBLE);
            mMapLinearLayoutVisble = false;
        }
    }


    private void searchPlacInfoFromNet(LatLng position) {
        //刷新经纬度
        mSelectPlaceInfo.locationLng = position.longitude;
        mSelectPlaceInfo.locationLat = position.latitude;

        mRequestQueue.cancelAll("addr");

        StringBuilder sb = new StringBuilder("http://api.map.baidu.com/geocoder/v2/?");
        sb.append("ak=");
        sb.append(mAk);
        sb.append("&coordtype=bd09ll&output=json");
        sb.append("&location=");
        sb.append(position.latitude);
        sb.append(",");
        sb.append(position.longitude);
        sb.append("&mcode=");
        sb.append(mCode);


        String url = sb.toString();
        Log.i("xx",url);
        JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(null == jsonObject)
                    return;
                try {
                    if(0 != jsonObject.getInt("status") )
                        return;
                    JSONObject resJson = jsonObject.getJSONObject("result");
                    String placeName = resJson.getString("sematic_description");
                    String placeAddr = resJson.getString("formatted_address");
                    StringBuilder sb = new StringBuilder();
                    sb.append(placeAddr);
                    sb.append(placeName);
                    mPlaceNameTextView.setText(placeName);
                    mPlaceAddrTextView.setText(sb.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                Log.i("xx",jsonObject.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("xx",volleyError.getMessage());
            }
        });

        jsonRequest.setTag("addr");
        mRequestQueue.add(jsonRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                goBack();
                break;
            case  R.id.clear_input_text_btn:
                clearInputText();
                break;
            case R.id.search_btn:
                searchPlaceInfo();
                break;
            case R.id.select_btn:
                selectPlace();
                break;
            default:
                break;
        }
    }



    private void selectPlace() {
        //这里数据的获取和检验没做！！！！待添加校验代码
        Intent intent = new Intent();
        intent.putExtra(LAT_KEY,mSelectPlaceInfo.locationLat);
        intent.putExtra(LNG_KEY,mSelectPlaceInfo.locationLng);
        intent.putExtra(ADDR_KEY,mPlaceNameTextView.getText());

        setResult(RESULT_OK,intent);
        finish();


    }

    private void searchPlaceInfo() {

    }

    private void clearInputText() {

        hideBaiduMap();
        mSearchInputEditText.setText("");
        mPlaceSuggestionInfos.clear();
        mPlaceSuggestionAdapter.notifyDataSetInvalidated();
    }

    private void goBack() {
        setResult(RESULT_CANCELED);
        finish();
    }


    class PlaceInfo {
        public String name;
        public double locationLat = 0.0;
        public double locationLng = 0.0;
        public String city;
        public String district;
    }

    @Override
    protected void onDestroy() {
        mRequestQueue.stop();
        super.onDestroy();
    }
}
