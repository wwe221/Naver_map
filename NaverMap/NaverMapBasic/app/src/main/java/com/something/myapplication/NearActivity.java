package com.something.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.nhn.android.maps.NMapView;
public class NearActivity extends AppCompatActivity{
    public static NearActivity getInstance() { return new NearActivity(); }
    @Override
    protected void onCreate(@Nullable Bundle bundle){
        super.onCreate(bundle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        boolean isGrantStorage = grantExternalStoragePermission();
        if(isGrantStorage) {
            setContentView(R.layout.activity_main);
            callmap();
            Intent intent = getIntent();

        }
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
    private void callmap() {
        Fragment1 fragment1 = new Fragment1();
        fragment1.setArguments(new Bundle());
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.near_nmapview, fragment1);
        fragmentTransaction.commit();
    }
    public void setIng(){
        TextView ing =(TextView)findViewById(R.id.ing);
        ing.setText("");}
}
