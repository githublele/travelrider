package cn.nono.ridertravel.bean.av;

import com.avos.avoscloud.AVObject;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

/**
 * Created by Administrator on 2016/4/21.
 */
public class AVTravelMapPath extends AVObject {

    //    private  static final String _key = "";
          private  static final String AUTHOR_KEY = "authorPointer";
          private  static final String MAP_LATLNG_PATHS_ARR_KEY = "travelPathLatlngArr";
          private static  final String PATH_NAME_KEY = "name";

    public AVMUser getAuthorPointer() {
        return getAVObject(AUTHOR_KEY);
    }

    public void setAuthorPointer(AVMUser user) {
        put(AUTHOR_KEY,user);
    }

    public List<LatLng> getMapPathLatLngArr() {
        return getList(MAP_LATLNG_PATHS_ARR_KEY);
    }

    public void setMapLatlngs(List<LatLng> latlngs) {
        put(MAP_LATLNG_PATHS_ARR_KEY,latlngs);
    }

    public String getName() {
        return getString(PATH_NAME_KEY);
    }

    public void setName(String pathName) {
        put(PATH_NAME_KEY,pathName);
    }





}
