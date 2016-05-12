package cn.nono.ridertravel.ui.baidumap;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.RouteStep;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.bean.av.AVMUser;
import cn.nono.ridertravel.bean.av.AVTravelMapPath;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;
import cn.nono.ridertravel.util.DrivingRouteOverlay;
import cn.nono.ridertravel.util.OverlayManager;

/**
 * Created by Administrator on 2016/4/6.
 */
public class TravelMapPathActivity extends BaseNoTitleActivity implements View.OnClickListener,OnGetRoutePlanResultListener{


    // 浏览路线节点相关
    RouteLine route = null;
    OverlayManager routeOverlay = null;
    boolean useDefaultIcon = true;
    // 搜索相关
    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用

    List<Marker> mMarkers = new ArrayList<Marker>();


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
    Boolean mMapLinearLayoutIsShow = false;
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
                convertView = LayoutInflater.from(TravelMapPathActivity.this).inflate(R.layout.item_place_suggestion,null);
                viewHolder = new ViewHolder();
                viewHolder.contenTextView = (TextView) convertView.findViewById(R.id.place_suggestion_textview);
                convertView.setTag(viewHolder);
            }

            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.contenTextView.setText(mPlaceSuggestionInfos.get(position).name);
            return convertView;
        }

        class ViewHolder {
            public TextView contenTextView;
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
            searchSuggestion(s.toString());
        }
    };
    private String mBaseUrlPlaceSuggertion = "http://api.map.baidu.com/place/v2/suggestion?output=json&region="+mRegion+"&mcode="+mCode+"&ak="+mAk;

    private void searchSuggestion(String str) {
        if(null == str || str.isEmpty())
            return;
        mRequestQueue.cancelAll("place_sug");
        StringBuilder sb = new StringBuilder(mBaseUrlPlaceSuggertion);
        sb.append("&query=");
        try {
            sb.append(URLEncoder.encode(str, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        if(mLocationLat != 0.0 && mLocationLng != 0.0) {
            sb.append("&location=");
            sb.append(mLocationLat);
            sb.append(",");
            sb.append(mLocationLng);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,sb.toString(),null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(null == jsonObject) {
                    mPlaceSuggestionInfos.clear();
                    mPlaceSuggestionAdapter.notifyDataSetInvalidated();
                    return;
                }

                try {
                    if(0 != jsonObject.getInt("status"))
                        return;
                    List<PlaceInfo> placeInfos = parsejsonPlaceSuggestionS(jsonObject);
                    mPlaceSuggestionInfos.clear();
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
                ToastUtil.toastShort(TravelMapPathActivity.this,"数据异常!");
            }
        });

        request.setTag("place_sug");
        mRequestQueue.add(request);
        hideBaiduMap();
    }

    private List<PlaceInfo> parsejsonPlaceSuggestionS(JSONObject jsonObject) {
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

                LatLng point = new LatLng(placeInfo.locationLat, placeInfo.locationLng);
                searchPlacInfoFromNet(point);
                addMark(point);
                searchPath();

                // 地图地位地点
                MapStatus mapStatus = new MapStatus.Builder().target(point).zoom(18).build();
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
                mBaiduMap.setMapStatus(mapStatusUpdate);
                showBaiduMap();

            }
        });

        mClearInputTextBtn = (Button) findViewById(R.id.clear_input_text_btn);
        mClearInputTextBtn.setOnClickListener(this);
        mOkButton = (Button) findViewById(R.id.select_btn);
        mOkButton.setOnClickListener(this);
        mSearchInputEditText = (EditText) findViewById(R.id.search_input_edittext);
        mSearchInputEditText.addTextChangedListener(mTextWatch);

        initBaiduMapView();


        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

    }

    private void initBaiduMapView() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                searchPlacInfoFromNet(marker.getPosition());
                searchPath();
            }

            @Override
            public void onMarkerDragStart(Marker marker) {
            }
        });

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                Button button = new Button(getApplicationContext());
                button.setBackgroundResource(R.drawable.popup);
                button.setText("删除");
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        marker.remove();
                        mMarkers.remove(marker);
                        mBaiduMap.hideInfoWindow();
                        searchPath();
                    }
                });
                LatLng ll = marker.getPosition();
                mInfoWindow = new InfoWindow(button, ll, -47);
                mBaiduMap.showInfoWindow(mInfoWindow);
                searchPath();
                return true;
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addMark(latLng);
                searchPath();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    private void searchPath() {

        if(mMarkers.size() <= 1)
            return;
        int size = mMarkers.size();
        LatLng startPoint = mMarkers.get(0).getPosition();
        LatLng endPoint = mMarkers.get(size -1).getPosition();

        List<PlanNode> passWayPoints = new ArrayList<PlanNode>();
        if(size > 2) {
            int length = size - 1;
            for (int i = 1; i < length; i++) {
                passWayPoints.add(PlanNode.withLocation(mMarkers.get(i).getPosition()));
            }
        }


        DrivingRoutePlanOption drPlan = new DrivingRoutePlanOption();
        PlanNode stNode = PlanNode.withLocation(startPoint);
        PlanNode enNode = PlanNode.withLocation(endPoint);
        drPlan.from(stNode);
        drPlan.to(enNode);
        drPlan.passBy(passWayPoints);
        mSearch.drivingSearch(drPlan);

    }

    private void addMark(LatLng point) {
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_geo);
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap).zIndex(5);
        Marker marker = (Marker) mBaiduMap.addOverlay(option);
        marker.setDraggable(true);
        mMarkers.add(marker);
        if(null != mInfoWindow) {
            mBaiduMap.hideInfoWindow();
        }
    }

    private InfoWindow mInfoWindow;



    private String mBaseUrlPlaceInfo = "http://api.map.baidu.com/geocoder/v2/?coordtype=bd09ll&output=json&ak="+mAk+"&mcode="+mCode;
    private void searchPlacInfoFromNet(LatLng position) {
        //刷新经纬度
        mSelectPlaceInfo.locationLng = position.longitude;
        mSelectPlaceInfo.locationLat = position.latitude;

        mRequestQueue.cancelAll("addr");
        StringBuilder sb = new StringBuilder(mBaseUrlPlaceInfo);
        sb.append("&location=");
        sb.append(position.latitude);
        sb.append(",");
        sb.append(position.longitude);
        JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, sb.toString(), null, new Response.Listener<JSONObject>() {
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

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("xx",volleyError.getMessage());
                ToastUtil.toastShort(TravelMapPathActivity.this,"数据异常!");
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
            case R.id.select_btn:
                saveMapPath();
                break;
            case R.id.ok_tv:
                break;
            default:
                break;
        }
    }


    private EditText dlgInputEditText = null;
    private AlertDialog dlg = null;
    private DialogInterface.OnClickListener mDlgOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
           if (DialogInterface.BUTTON_POSITIVE == which) {
              String str =  dlgInputEditText.getText().toString();
               if(null == str || str.isEmpty())
                   return;
               mPathName = str;
               dialog.dismiss();
               saveMapPathToNet();
           }

            dlgInputEditText.setText("");
            dialog.dismiss();


        }
    };


    public static final String MAP_PATH_KEY = "mappath";
    private AVTravelMapPath mMapPath = null;
    private void saveMapPathToNet() {
        mMapPath = buildMapPathAVObj();
        if(null == mMapPath) {
            ToastUtil.toastLong(TravelMapPathActivity.this,"路线没有选择!");
            return;
        }


        mMapPath.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(null != e) {
                    ToastUtil.toastLong(TravelMapPathActivity.this,"保存失败。");
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra(MAP_PATH_KEY,mMapPath);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

    private void saveMapPath() {
        if(null == dlgInputEditText)
            dlgInputEditText = new EditText(TravelMapPathActivity.this);
        if(null == dlg)
            dlg =  new AlertDialog.Builder(TravelMapPathActivity.this)
                    .setTitle("请输入路线名称")
                    .setView(dlgInputEditText)
                    .setCancelable(false).setPositiveButton("确定", mDlgOnClickListener)
                    .setNegativeButton("取消", mDlgOnClickListener).create();
            dlg.show();

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

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(TravelMapPathActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {

            route = result.getRouteLines().get(0);

            if(null != routeOverlay)
                routeOverlay.removeFromMap();

            List<RouteStep> stepList = route.getAllStep();
            mRouteLatLngPoints.clear();
            for (RouteStep step:stepList) {
                mRouteLatLngPoints.addAll(step.getWayPoints());
            }

            OverlayOptions ooPolyline = new PolylineOptions().width(10).dottedLine(true)
                    .points(mRouteLatLngPoints).customTexture(mBlueTexture);

            if(null != mPolyline)
            mPolyline.remove();
            mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);

        }
    }

    private  List<LatLng> mRouteLatLngPoints = new ArrayList<LatLng>();
    private  String mPathName = null;

    BitmapDescriptor mBlueTexture = BitmapDescriptorFactory.fromAsset("icon_road_blue_arrow.png");

    Polyline mPolyline = null;
    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }




    class PlaceInfo {
        public String name;
        public double locationLat = 0.0;
        public double locationLng = 0.0;
        public String city;
        public String district;
    }

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private AVTravelMapPath buildMapPathAVObj() {
        if(null == mPathName || mPathName.isEmpty() || mRouteLatLngPoints.size() < 1)
            return null;
        AVMUser user = AVUser.getCurrentUser(AVMUser.class);
        if(null == user) {
            login();
            return null;
        }

        AVTravelMapPath path = new AVTravelMapPath();
        path.setAuthorPointer(user);
        List<AVGeoPoint>  avGeoPoints = new ArrayList<AVGeoPoint>();
        AVGeoPoint avGeoPoint = null;
        for (LatLng latlng:mRouteLatLngPoints) {
            avGeoPoint = new AVGeoPoint();
            avGeoPoint.setLatitude(latlng.latitude);
            avGeoPoint.setLongitude(latlng.longitude);
            avGeoPoints.add(avGeoPoint);
        }
        path.setMapLatlngs(avGeoPoints);
        path.setName(mPathName);
        return path;
    }

    private void showBaiduMap() {
        if(mMapLinearLayoutIsShow)
            return;
        mPlaceSuggestionListView.setVisibility(View.GONE);
        mMapLinearLayout.setVisibility(View.VISIBLE);
        mMapLinearLayoutIsShow = true;
    }

    private void hideBaiduMap() {
        if(mMapLinearLayoutIsShow) {
            mMapLinearLayout.setVisibility(View.GONE);
            mPlaceSuggestionListView.setVisibility(View.VISIBLE);
            mMapLinearLayoutIsShow = false;
        }
    }

    @Override
    protected void onDestroy() {
        mRequestQueue.stop();
        mMapView.onDestroy();
        super.onDestroy();
    }

}
