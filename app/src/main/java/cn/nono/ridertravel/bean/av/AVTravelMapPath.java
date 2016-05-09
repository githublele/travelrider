package cn.nono.ridertravel.bean.av;

import android.annotation.SuppressLint;

import com.avos.avoscloud.AVACL;
import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;

import java.util.List;

/**
 * Created by Administrator on 2016/4/21.
 */
@SuppressLint("ParcelCreator")
@AVClassName("TravelMapPath")
public class AVTravelMapPath extends AVObject {

    //    private  static final String _key = "";
          private  static final String AUTHOR_KEY = "authorPointer";
          private  static final String MAP_LATLNG_PATHS_ARR_KEY = "travelPathLatlngArr";
          private static  final String PATH_NAME_KEY = "name";

    public AVTravelMapPath() {
        super();
        AVACL avacl = new AVACL();
        avacl.setPublicReadAccess(true);
        avacl.setPublicWriteAccess(true);
        setACL(avacl);
    }

    public AVMUser getAuthorPointer() {
        return getAVObject(AUTHOR_KEY);
    }

    public void setAuthorPointer(AVMUser user) {
        put(AUTHOR_KEY,user);
    }

    public List<AVGeoPoint> getMapPathLatLngArr() {
        return getList(MAP_LATLNG_PATHS_ARR_KEY);
    }

    public void setMapLatlngs(List<AVGeoPoint> latlngs) {
        put(MAP_LATLNG_PATHS_ARR_KEY,latlngs);
    }

    public String getName() {
        return getString(PATH_NAME_KEY);
    }

    public void setName(String pathName) {
        put(PATH_NAME_KEY,pathName);
    }




    AVGeoPoint av = new AVGeoPoint();


}
