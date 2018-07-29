package com.example.heun.myapplication;
//https://m.blog.naver.com/PostView.nhn?blogId=ssawospark&logNo=100181575391&proxyReferer=https%3A%2F%2Fwww.google.co.kr%2F
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.NMapView.OnMapStateChangeListener;
import com.nhn.android.maps.NMapView.OnMapViewTouchEventListener;
import com.nhn.android.maps.maplib.NGPoint;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.maps.overlay.NMapPathLineStyle;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay.OnStateChangeListener;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.os.Build;
import android.provider.Settings;
import android.Manifest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import static java.lang.Math.*;

public class MainActivity extends NMapActivity {
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };
    String APiKey = "";//클라이언트 아이디 키 
    NMapView mMapView;		//MapView 객체(지도 생성, 지도데이터)
    NMapController mMapController;	//지도 상태 컨트롤 객체
    NMapViewerResourceProvider mMapViewerResourceProvider;	//지도 뷰어 리소스 곱급자 객체 생성
    NMapOverlayManager mOverlayManager;		//오버레이 관리 객체
    //OnStateChangeListener onPOIdataStateChangeListener;		//오버레이 아이템 변화 이벤트 콜백 인터페이스
    NMapMyLocationOverlay mMyLocationOverlay;	//지도 위에 현재 위치를 표시하는 오버레이 클래스
    NMapLocationManager mMapLocationManager;	//단말기의 현재 위치 탐색 기능 사용 클래스
    NMapCompassManager mMapCompassManager;		//단말기의 나침반 기능 사용 클래스
    OnMapViewTouchEventListener onMapViewTouchEventListener;
    OnMapStateChangeListener onMapViewStateChangeListener;
    NMapPOIitem[] items =new NMapPOIitem[20];
    JSONArray peoples = null;
    String myJSON;
    private static final String TAG_RESULTS = "result";
    double a=0,b=0;
    Intent intent = getIntent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //지도 화면 생성
        mMapView = new NMapView(this);
        //API 키 설정
        mMapView.setApiKey(APiKey);
        //7aBXm5SPP5
        //지도 화면 초기화
        mMapView.setClickable(true);
        //지도 상태 변화를 위한 listener 등록
        mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
        mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);
        //지도 컨트롤러(줌 인/아웃 등) 사용
        mMapController = mMapView.getMapController();
        // 줌 인/아웃 버튼 생성
        mMapView.setBuiltInZoomControls(true, null);
        //지도 중심좌표 및 축적 레벨 설정
        //맵뷰 모드 설정
 /*
        mMapController.setMapViewMode(NMapView.VIEW_MODE_VECTOR);	//일반지도
        mMapController.setMapViewMode(NMapView.VIEW_MODE_HYBRID);	//위성지도
        mMapController.setMapViewTrafficMode(true);	//실시간 교통지도 보기 모드 설정
        mMapController.setMapViewBicycleMode(true);	//자전거 지도 보기 모드 설정 */
        //화면에 지도 표시
        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);
        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
        boolean isGrantStorage = grantExternalStoragePermission();
       if(isGrantStorage)
           startMyLocation(); //내 위치 찾기 시작 함수
        setContentView(mMapView);
        }
    private void startMyLocation() {//내 위치 찾아서 이동.
        mMapLocationManager = new NMapLocationManager(this);
        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);
        mMapLocationManager.enableMyLocation(true);
        boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);
        if (!isMyLocationEnabled) {	//위치 탐색이 불가능하면
            Toast.makeText(MainActivity.this, "GPS권한을 허용해주십시오.",
                    Toast.LENGTH_LONG).show();
            Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(goToSettings);
            return;
        }
        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);
    }
    private void stopMyLocation() {
        mMapLocationManager.disableMyLocation();	//현재 위치 탐색 종료
        if (mMapView.isAutoRotateEnabled()) {		//지도 회전기능이 활성화 상태라면
            mMyLocationOverlay.setCompassHeadingVisible(false);	//나침반 각도표시 제거
            mMapCompassManager.disableCompass();	//나침반 모니터링 종료
            mMapView.setAutoRotateEnabled(false, false);//지도 회전기능 중지
        }
    }
    private void testOverlayMaker() { // 오버레이 아이템 예시
        int markerId = NMapPOIflagType.PIN;	//마커 id설정
        //POI 데이터 관리 클래스 생성(POI데이터 수, 사용 리소스 공급자)
        NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
        poiData.beginPOIdata(2);	// POI 아이템 추가 시작
        NMapPOIitem item1 = poiData.addPOIitem(a+0.007,b+0.006, "국민", markerId, 0);
        NMapPOIitem item2 = poiData.addPOIitem(a+0.0045,b-0.00045, "우리", markerId, 0);
        item1.setRightAccessory(true,NMapPOIflagType.CLICKABLE_ARROW);
        poiData.endPOIdata();		// POI 아이템 추가 종료
        //POI 데이터 오버레이 객체 생성(여러 개의 오버레이 아이템을 포함할 수 있는 오버레이 클래스)
        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
        poiDataOverlay.showAllPOIdata(11);	//모든 POI 데이터를 화면에 표시(zomLevel)
        //POI 아이템이 선택 상태 변경 시 호출되는 콜백 인터페이스 설정
        poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);
    }
    private final NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener(){
       @Override
        public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem poiItem){ //오버레이의 버튼을 눌렀을때
            Log.i("이름",poiItem.getTitle());
        }
        @Override
        public void onFocusChanged(NMapPOIdataOverlay nMapPOIdataOverlay,NMapPOIitem poiitem){
        }
    };
    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {
        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {
            if (mMapController != null) {
                mMapController.animateTo(myLocation);
                a=myLocation.getLongitude();
                b=myLocation.getLatitude();

                getData("http://117.16.43.25/php_connect.php");
            }
            return true;
        }
        @Override
        public void onLocationUpdateTimeout(NMapLocationManager locationManager) {
        }
        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {
            stopMyLocation();
        }
    };
public void getData(String url) {
    class GetDataJSON extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String uri = params[0];
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(5000);
                con.setConnectTimeout(5000);
                StringBuilder sb = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                }
                return sb.toString().trim();
            } catch (Exception e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result) {
            Log.i("내위치",a+"@@@@"+b);
            myJSON = result;
            int cnt=0;
            double aa=0;
            int markerId = NMapPOIflagType.PIN;	//마커 id설정
            //POI 데이터 관리 클래스 생성(POI데이터 수, 사용 리소스 공급자)
            NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
            poiData.beginPOIdata(2);	// POI 아이템 추가 시작
            try {
                JSONObject jsonObj = new JSONObject(myJSON);
                peoples = jsonObj.getJSONArray(TAG_RESULTS);
                for (int i = 0; i < peoples.length(); i++) {
                    JSONObject c = peoples.getJSONObject(i);
                    int pcindex = c.getInt("pid");
                    String name = c.getString("pname");
                    double locationx = change(c.getInt("location_y"));
                    double locationy = change(c.getInt("location_x"));
                    Log.i("PHPRequest", "#####" + c.getInt("location_y")+"@@@"+c.getInt("location_x"));
                    aa=distance(a,b,locationx,locationy);
                    if(aa<=20000){
                        String ad_si = c.getString("addr_city");
                        String ad_rns = c.getString("addr_country");
                        String ad_rn = c.getString("addr_district");
                        String number = c.getString("pcallnum");
                        String abc= ad_si+ad_rns+ad_rn+number;
                        items[cnt]=poiData.addPOIitem(locationx,locationy,name, markerId, 0);
                        items[cnt].setRightButton(false);
                        Log.i("itemoveray","표시했다."+items[cnt].getPoint().getLongitude()+"!!!"+items[cnt].getPoint().getLatitude()+"@@"+name);
                        cnt++;
                    }
                    Log.i("cnt의 값","cnt는"+cnt);
                }
                poiData.endPOIdata();		// POI 아이템 추가 종료
                NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
                poiDataOverlay.showAllPOIdata(7);	//모든 POI 데이터를 화면에 표시(zomLevel)
                //POI 아이템이 선택 상태 변경 시 호출되는 콜백 인터페이스 설정
                poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    GetDataJSON g = new GetDataJSON();
    g.execute(url);
}
    public double distance(double lat1,double lon1,double lat2,double lon2){ // 두 좌표사이의 거리를 계산(m 단위)
        double theta, dist;
        theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환
        return dist;
    }
    private double deg2rad(double deg){
        return (double)(deg * Math.PI / (double)180d);
    }
    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad){
        return (double)(rad * (double)180d / Math.PI);
    }
    public double change(int a){
        double b=Math.pow(10,7);
        return (double)a/b*1.0;
    }
    private boolean grantExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return false;
            }
        }else{
            Toast.makeText(this, "External Storage Permission is Grant", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= 23) {
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){

                //resume tasks needing this permission
            }
        }
    }

}
