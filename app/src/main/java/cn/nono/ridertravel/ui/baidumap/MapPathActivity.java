package cn.nono.ridertravel.ui.baidumap;

import android.os.Bundle;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;

/**
 * Created by Administrator on 2016/4/22.
 */
public class MapPathActivity extends BaseNoTitleActivity{

    public static final String MAP_LATLNG_POINT_LIST_KEY = "map_points";

    private List<LatLng> mLatLngPoints = null;

    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_path_brower);
        mLatLngPoints = getIntent().getParcelableArrayListExtra(MAP_LATLNG_POINT_LIST_KEY);
        if (mLatLngPoints == null || mLatLngPoints.size() <= 1) {
            finish();
            return;
        }
        mMapView = (MapView) findViewById(R.id.map_baidumap);
    }
}
